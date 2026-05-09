package dev.xkmc.l2core.init;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import java.util.function.Function;

@EventBusSubscriber(value = Dist.CLIENT, modid = L2Core.MODID)
public class L2CoreClient {

	public static final RenderPipeline PIPELINE_ICON =
			RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
					.withLocation(L2Core.loc("pipeline/icon"))
					.withVertexShader("core/position_tex")
					.withFragmentShader("core/position_tex")
					.withSampler("Sampler0")
					.withColorTargetState(new ColorTargetState(BlendFunction.ADDITIVE))
					.withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
					.build();

	public static final Function<Identifier, RenderType> TYPE_ICON = Util.memoize((tex) -> RenderType
			.create("l2icon", RenderSetup.builder(PIPELINE_ICON)
					.withTexture("Sampler0", tex)
					.createRenderSetup()));

	@SubscribeEvent
	public static void registerPipeline(RegisterRenderPipelinesEvent event) {
		event.registerPipeline(PIPELINE_ICON);
	}

}
