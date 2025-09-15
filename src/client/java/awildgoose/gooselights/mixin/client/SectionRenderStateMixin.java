package awildgoose.gooselights.mixin.client;

import awildgoose.gooselights.GooseLightsClient;
import awildgoose.gooselights.gpu.GPULight;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayerGroup;
import net.minecraft.client.render.SectionRenderState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static awildgoose.gooselights.GooseLightsClient.GooseLogger;
import static awildgoose.gooselights.GooseLightsClient.MAX_LIGHTS;

@Mixin(SectionRenderState.class)
public class SectionRenderStateMixin {
    @Inject(
            method = "renderSection",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;bindDefaultUniforms(Lcom/mojang/blaze3d/systems/RenderPass;)V",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void gooseLights$injectUniforms(BlockRenderLayerGroup group,
                                            CallbackInfo ci,
                                            RenderSystem.ShapeIndexBuffer shapeIndexBuffer,
                                            GpuBuffer gpuBuffer,
                                            VertexFormat.IndexType indexType,
                                            BlockRenderLayer[] blockRenderLayers,
                                            MinecraftClient minecraftClient,
                                            boolean bl,
                                            Framebuffer framebuffer,
                                            RenderPass renderPass
    ) {
        if (colormap == null || tickCounter % 60 == 0)
            updateColormap();

        renderPass.setUniform("GooseLights", colormap);
        tickCounter++;
    }

    @Unique private static int tickCounter = 0;
    @Unique private static GpuBufferSlice colormap;

    @Unique
    private static void updateColormap() {
        ByteBuffer buf = ByteBuffer.allocateDirect(MAX_LIGHTS * 32).order(ByteOrder.nativeOrder());

        buf.putInt(GooseLightsClient.lights.size());
        buf.putInt(0).putInt(0).putInt(0); // padding

        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < GooseLightsClient.lights.size()) {
                GooseLightsClient.lights.get(i).upload(buf);
            } else {
                // empty light
                for (int j = 0; j < 8; j++) buf.putFloat(0f);
            }
        }

        buf.flip();
        GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "GooseLights UBO", buf.remaining(), buf);
        colormap = buffer.slice();

        GooseLogger.info("Colormap updated!");
    }
}
