package awildgoose.gooselights.mixin.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.UniformType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

import static awildgoose.gooselights.GooseLightsClient.MAX_LIGHTS;

@Mixin(RenderPipeline.Builder.class)
public class TerrainPipelineMixin {
    @Inject(at = @At("RETURN"), method = "withFragmentShader", cancellable = true)
    public void withFragmentShader(String fragmentShader, CallbackInfoReturnable<RenderPipeline.Builder> cib) {
        if (Objects.equals(fragmentShader, "core/terrain")) {
            var builder = cib.getReturnValue();
            cib.setReturnValue(builder
                    .withShaderDefine("MAX_LIGHTS", MAX_LIGHTS)
                    .withUniform("GooseLights", UniformType.UNIFORM_BUFFER)
            );
        }
    }
}
