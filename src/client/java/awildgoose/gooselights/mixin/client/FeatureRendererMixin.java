package awildgoose.gooselights.mixin.client;

import awildgoose.gooselights.GooseLightsClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FeatureRenderer.class)
public class FeatureRendererMixin {
    /**
     * @author awildergoose
     * @reason Storing the living entity state to get the world position
     */
    @Inject(at = @At("HEAD"), method = "renderModel")
    private static void renderModel(
            EntityModel<?> model, Identifier texture, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntityRenderState state, int color,
            CallbackInfo ci
    ) {
        GooseLightsClient.lastRenderState = state;
    }
}
