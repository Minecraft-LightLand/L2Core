package dev.xkmc.l2core.events;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2core.base.effects.ClientEffectCap;
import dev.xkmc.l2core.base.effects.EffectToClient;
import dev.xkmc.l2core.base.effects.api.ClientRenderEffect;
import dev.xkmc.l2core.base.effects.api.DelayedEntityRender;
import dev.xkmc.l2core.base.effects.api.FirstPlayerRenderEffect;
import dev.xkmc.l2core.base.effects.api.IconRenderRegion;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.Proxy;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = L2Core.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ClientEffectRenderEvents {

	private static final ArrayList<DelayedEntityRender> ICONS = new ArrayList<>();

	@SubscribeEvent
	public static void clientTick(ClientTickEvent.Post event) {
		AbstractClientPlayer player = Proxy.getClientPlayer();
		if (player != null) {
			for (var entry : player.getActiveEffectsMap().entrySet()) {
				if (entry.getKey() instanceof FirstPlayerRenderEffect effect) {
					effect.onClientLevelRender(player, entry.getValue());
				}
			}
		}
	}

	@SubscribeEvent
	public static void levelRenderLast(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;
		LevelRenderer renderer = event.getLevelRenderer();
		MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		PoseStack stack = event.getPoseStack();

		// cache the previous handler
		for (DelayedEntityRender icon : ICONS) {
			renderIcon(stack, buffers, icon, event.getPartialTick().getGameTimeDeltaTicks(), camera, renderer.entityRenderDispatcher);
		}
		buffers.endBatch();

		ICONS.clear();
	}

	@SubscribeEvent
	public static void onLivingEntityRender(RenderLivingEvent.Post<?, ?> event) {
		LivingEntity entity = event.getEntity();
		if (!L2LibReg.EFFECT.type().isProper(entity)) return;
		if (entity.getTags().contains("ClientOnly")) return;//TODO
		ClientEffectCap cap = L2LibReg.EFFECT.type().getOrCreate(entity);
		List<Pair<ClientRenderEffect, Integer>> l0 = new ArrayList<>();
		for (var entry : cap.map.entrySet()) {
			if (entry.getKey().value() instanceof ClientRenderEffect effect) {
				l0.add(Pair.of(effect, entry.getValue()));
			}
		}
		if (l0.isEmpty()) return;
		List<Pair<Integer, DelayedEntityRender>> l1 = new ArrayList<>();
		int index = 0;

		for (var e : l0) {
			int lv = e.getSecond();
			int size = l1.size();
			int I = index;
			e.getFirst().render(entity, lv, x -> l1.add(Pair.of(I, x)));
			if (l1.size() > size) {
				index++;
			}
		}

		int n = index;
		int w = (int) Math.ceil(Math.sqrt(n));
		int h = (int) Math.ceil(n * 1d / w);

		for (var e : l1) {
			int i = e.getFirst();
			int iy = i / w;
			int iw = Math.min(w, n - iy * w);
			int ix = i - iy * w;
			ICONS.add(e.getSecond().resize(IconRenderRegion.of(w, ix, iy, iw, h)));
		}

	}

	private static void renderIcon(PoseStack pose, MultiBufferSource buffer, DelayedEntityRender icon,
								   float partial, Camera camera, EntityRenderDispatcher dispatcher) {
		LivingEntity entity = icon.entity();
		float f = entity.getBbHeight() / 2;

		double x0 = Mth.lerp(partial, entity.xOld, entity.getX());
		double y0 = Mth.lerp(partial, entity.yOld, entity.getY());
		double z0 = Mth.lerp(partial, entity.zOld, entity.getZ());
		Vec3 offset = dispatcher.getRenderer(entity).getRenderOffset(entity, partial);
		Vec3 cam_pos = camera.getPosition();
		double d2 = x0 - cam_pos.x + offset.x();
		double d3 = y0 - cam_pos.y + offset.y();
		double d0 = z0 - cam_pos.z + offset.z();

		pose.pushPose();
		pose.translate(d2, d3 + f, d0);
		pose.mulPose(camera.rotation());
		PoseStack.Pose entry = pose.last();
		VertexConsumer ivertexbuilder = buffer.getBuffer(get2DIcon(icon.rl()));

		float ix0 = -0.5f + icon.region().x();
		float ix1 = ix0 + icon.region().scale();
		float iy0 = -0.5f + icon.region().y();
		float iy1 = iy0 + icon.region().scale();
		float u0 = icon.tx();
		float v0 = icon.ty();
		float u1 = icon.tx() + icon.tw();
		float v1 = icon.ty() + icon.th();

		iconVertex(entry, ivertexbuilder, ix1, iy0, u0, v1);
		iconVertex(entry, ivertexbuilder, ix0, iy0, u1, v1);
		iconVertex(entry, ivertexbuilder, ix0, iy1, u1, v0);
		iconVertex(entry, ivertexbuilder, ix1, iy1, u0, v0);
		pose.popPose();
	}

	private static void iconVertex(PoseStack.Pose entry, VertexConsumer builder, float x, float y, float u, float v) {
		builder.addVertex(entry.pose(), x, y, 0)
				.setUv(u, v)
				.setNormal(entry, 0.0F, 1.0F, 0.0F);
	}

	public static RenderType get2DIcon(ResourceLocation rl) {
		return RenderType.create(
				"entity_body_icon",
				DefaultVertexFormat.POSITION_TEX,
				VertexFormat.Mode.QUADS, 256, false, true,
				RenderType.CompositeState.builder()
						.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_SHADER)
						.setTextureState(new RenderStateShard.TextureStateShard(rl, false, false))
						.setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
						.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
						.createCompositeState(false)
		);
	}

	public static void sync(EffectToClient eff) {
		if (Minecraft.getInstance().level == null) return;
		Entity e = Minecraft.getInstance().level.getEntity(eff.entity());
		if (!(e instanceof LivingEntity le)) return;
		if (!L2LibReg.EFFECT.type().isProper(le)) return;
		ClientEffectCap cap = L2LibReg.EFFECT.type().getOrCreate(le);
		if (eff.exist()) {
			cap.map.put(eff.effect(), eff.level());
		} else {
			cap.map.remove(eff.effect());
		}
	}

}
