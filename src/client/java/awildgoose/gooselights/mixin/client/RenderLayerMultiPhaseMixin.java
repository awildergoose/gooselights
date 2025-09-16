package awildgoose.gooselights.mixin.client;

import awildgoose.gooselights.GooseLightsClient;
import awildgoose.gooselights.gpu.Colormap;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ScissorState;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.OptionalDouble;
import java.util.OptionalInt;

@Mixin(RenderLayer.MultiPhase.class)
public abstract class RenderLayerMultiPhaseMixin extends RenderLayer {
    @Shadow private RenderLayer.MultiPhaseParameters phases;
    @Shadow private RenderPipeline pipeline;

    public RenderLayerMultiPhaseMixin(String name, int size, boolean hasCrumbling, boolean translucent, Runnable begin, Runnable end) {
        super(name, size, hasCrumbling, translucent, begin, end);
    }

    /**
     * @author awildergoose
     * @reason Entity uniform for world position
     */
    @Overwrite
    public void draw(BuiltBuffer buffer) {
        this.startDrawing();
        Vector4f worldPosition = new Vector4f(1, 1, 1, 1);
        if (GooseLightsClient.lastRenderState != null && this.getName().startsWith("entity"))
            worldPosition = new Vector4f((float) GooseLightsClient.lastRenderState.x, (float) GooseLightsClient.lastRenderState.y, (float) GooseLightsClient.lastRenderState.z, 1);

        GpuBufferSlice gpuBufferSlice = RenderSystem.getDynamicUniforms()
                .write(
                        RenderSystem.getModelViewMatrix(),
                        worldPosition,
                        RenderSystem.getModelOffset(),
                        RenderSystem.getTextureMatrix(),
                        RenderSystem.getShaderLineWidth()
                );

        try {
            GpuBuffer gpuBuffer = this.pipeline.getVertexFormat().uploadImmediateVertexBuffer(buffer.getBuffer());
            GpuBuffer gpuBuffer2;
            VertexFormat.IndexType indexType;
            if (buffer.getSortedBuffer() == null) {
                RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(buffer.getDrawParameters().mode());
                gpuBuffer2 = shapeIndexBuffer.getIndexBuffer(buffer.getDrawParameters().indexCount());
                indexType = shapeIndexBuffer.getIndexType();
            } else {
                gpuBuffer2 = this.pipeline.getVertexFormat().uploadImmediateIndexBuffer(buffer.getSortedBuffer());
                indexType = buffer.getDrawParameters().indexType();
            }

            //noinspection ReferenceToMixin
            Framebuffer framebuffer = ((MultiPhaseParametersAccessor)(Object)this.phases).getTarget().get();
            GpuTextureView gpuTextureView = RenderSystem.outputColorTextureOverride != null
                    ? RenderSystem.outputColorTextureOverride
                    : framebuffer.getColorAttachmentView();
            GpuTextureView gpuTextureView2 = framebuffer.useDepthAttachment
                    ? (RenderSystem.outputDepthTextureOverride != null ? RenderSystem.outputDepthTextureOverride : framebuffer.getDepthAttachmentView())
                    : null;

            try (RenderPass renderPass = RenderSystem.getDevice()
                    .createCommandEncoder()
                    .createRenderPass(() -> "Immediate draw for " + this.getName(), gpuTextureView, OptionalInt.empty(), gpuTextureView2, OptionalDouble.empty())) {
                renderPass.setPipeline(this.pipeline);
                ScissorState scissorState = RenderSystem.getScissorStateForRenderTypeDraws();
                if (scissorState.method_72091()) {
                    renderPass.enableScissor(scissorState.method_72092(), scissorState.method_72093(), scissorState.method_72094(), scissorState.method_72095());
                }

                RenderSystem.bindDefaultUniforms(renderPass);
                Colormap.setColormap(renderPass);
                renderPass.setUniform("DynamicTransforms", gpuBufferSlice);
                renderPass.setVertexBuffer(0, gpuBuffer);

                for (int i = 0; i < 12; i++) {
                    GpuTextureView gpuTextureView3 = RenderSystem.getShaderTexture(i);
                    if (gpuTextureView3 != null) {
                        renderPass.bindSampler("Sampler" + i, gpuTextureView3);
                    }
                }

                renderPass.setIndexBuffer(gpuBuffer2, indexType);
                renderPass.drawIndexed(0, 0, buffer.getDrawParameters().indexCount(), 1);
            }
        } catch (Throwable var17) {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (Throwable var14) {
                    var17.addSuppressed(var14);
                }
            }

            throw var17;
        }

        if (buffer != null) {
            buffer.close();
        }

        this.endDrawing();
    }
}
