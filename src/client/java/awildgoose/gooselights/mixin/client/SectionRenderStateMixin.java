package awildgoose.gooselights.mixin.client;

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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static awildgoose.gooselights.GooseLightsClient.GooseLogger;

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
        if (colormap == null) initColormap();
        if (tickCounter % 300 == 0)
            updateColormap();

        renderPass.setUniform("GooseLights", colormap);
        tickCounter++;
    }

    @Unique private static int tickCounter = 0;
    @Unique private static GpuBufferSlice colormap;

    @Unique
    private static void initColormap() {
        int arraySize = 256;
        int stride = 16;
        ByteBuffer buf = ByteBuffer.allocateDirect(arraySize * stride).order(ByteOrder.nativeOrder());

        for (int i = 0; i < arraySize; i++) {
            buf.putFloat(1f);
            buf.putFloat(1f);
            buf.putFloat(1f);
            buf.putFloat(0f);
        }

        buf.flip();
        GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "GooseLights UBO", buf.remaining(), buf);
        colormap = buffer.slice();
    }

    @Unique
    private static void updateColormap() {
        int arraySize = 256;
        int stride = 16;
        ByteBuffer buf = ByteBuffer.allocateDirect(arraySize * stride).order(ByteOrder.nativeOrder());

        for (int i = 0; i < arraySize; i++) {
            buf.putFloat((float) Math.random());
            buf.putFloat((float) Math.random());
            buf.putFloat((float) Math.random());
            buf.putFloat(0f);
        }

        buf.flip();
        GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "GooseLights UBO", buf.remaining(), buf);
        colormap = buffer.slice();

        GooseLogger.debug("Colormap updated!");
    }
}
