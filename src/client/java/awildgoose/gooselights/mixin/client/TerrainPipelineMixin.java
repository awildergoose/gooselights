package awildgoose.gooselights.mixin.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.UniformType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderPipeline.Builder.class)
public class TerrainPipelineMixin {
    @Inject(at = @At("RETURN"), method = "withSampler", cancellable = true)
    public void withSampler(String sampler, CallbackInfoReturnable<RenderPipeline.Builder> cib) {
        cib.setReturnValue(cib.getReturnValue().withUniform("GooseLights", UniformType.UNIFORM_BUFFER));
    }
}
