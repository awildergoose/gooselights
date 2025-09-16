package awildgoose.gooselights.mixin.client;

import awildgoose.gooselights.gpu.Colormap;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayerGroup;
import net.minecraft.client.render.SectionRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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
    private void gooseLights$injectUniforms(
            BlockRenderLayerGroup group,
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
        Colormap.setColormap(renderPass);
    }
}
