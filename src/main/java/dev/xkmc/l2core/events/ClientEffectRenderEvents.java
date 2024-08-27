package dev.xkmc.l2core.events;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.datafixers.util.Pair;
import dev.xkmc.l2core.base.effects.ClientEffectCap;
import dev.xkmc.l2core.base.effects.EffectToClient;
import dev.xkmc.l2core.base.effects.api.*;
import dev.xkmc.l2core.init.L2Core;
import dev.xkmc.l2core.init.L2LibReg;
import dev.xkmc.l2core.util.Proxy;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static net.minecraft.client.renderer.RenderStateShard.POSITION_TEX_SHADER;

@EventBusSubscriber(value = Dist.CLIENT, modid = L2Core.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ClientEffectRenderEvents {

	private static final ArrayList<IDelayedRender> ICONS = new ArrayList<>();

	public static void addIcon(IDelayedRender icon) {
		ICONS.add(icon);
	}

	@SubscribeEvent
	public static void clientTick(ClientTickEvent.Post event) {
		AbstractClientPlayer player = Proxy.getClientPlayer();
		if (player != null) {
			for (var entry : player.getActiveEffectsMap().entrySet()) {
				if (entry.getValue().getEffect().value() instanceof FirstPlayerRenderEffect effect) {
					effect.onClientLevelRender(player, entry.getValue());
				}
			}
		}
	}

	private static final RenderType DUMMY = RenderType.create(
			"entity_icon_preparation", DefaultVertexFormat.POSITION_TEX,
			VertexFormat.Mode.QUADS, 256, false, false,
			RenderType.CompositeState.builder().createCompositeState(false)
	);

	private static final Function<ResourceLocation, RenderType> ICON_TYPE = Util.memoize(rl -> RenderType.create(
			"entity_body_icon",
			DefaultVertexFormat.POSITION_TEX,
			VertexFormat.Mode.QUADS, 256, false, false,
			RenderType.CompositeState.builder()
					.setShaderState(POSITION_TEX_SHADER)
					.setTextureState(new RenderStateShard.TextureStateShard(rl, false, false))
					.setTransparencyState(RenderStateShard.ADDITIVE_TRANSPARENCY)
					.setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
					.createCompositeState(false)
	));

	@SubscribeEvent
	public static void levelRenderLast(RenderLevelStageEvent event) {
		if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;

		DUMMY.setupRenderState();
		DUMMY.clearRenderState();
		MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
		Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
		PoseStack stack = event.getPoseStack();
		float pTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);
		Map<ResourceLocation, List<IDelayedRender>> map = new HashMap<>();
		for (var e : ICONS) map.computeIfAbsent(e.rl(), k -> new ArrayList<>()).add(e);
		for (var ent : map.entrySet()) {
			VertexConsumer vc = buffers.getBuffer(ICON_TYPE.apply(ent.getKey()));
			for (var e : ent.getValue()) {
				renderIcon(stack, vc, e, pTick, camera);
			}
		}
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

	private static void renderIcon(PoseStack pose, VertexConsumer vc, IDelayedRender icon,
								   float partial, Camera camera) {
		Vec3 pos = icon.pos(partial);
		Vec3 cam_pos = camera.getPosition();
		pose.pushPose();
		pose.translate(pos.x() - cam_pos.x, pos.y() - cam_pos.y, pos.z() - cam_pos.z);
		pose.mulPose(camera.rotation());
		PoseStack.Pose entry = pose.last();

		float ix0 = -0.5f + icon.region().x();
		float ix1 = ix0 + icon.region().scale();
		float iy0 = -0.5f + icon.region().y();
		float iy1 = iy0 + icon.region().scale();
		float u0 = icon.tx();
		float v0 = icon.ty();
		float u1 = icon.tx() + icon.tw();
		float v1 = icon.ty() + icon.th();

		iconVertex(entry, vc, ix0, iy0, u0, v1);
		iconVertex(entry, vc, ix1, iy0, u1, v1);
		iconVertex(entry, vc, ix1, iy1, u1, v0);
		iconVertex(entry, vc, ix0, iy1, u0, v0);
		pose.popPose();
	}

	private static void iconVertex(PoseStack.Pose entry, VertexConsumer builder, float x, float y, float u, float v) {
		builder.addVertex(entry.pose(), x, y, 0).setUv(u, v);
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
