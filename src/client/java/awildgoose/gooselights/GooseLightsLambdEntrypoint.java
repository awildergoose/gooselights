package awildgoose.gooselights;

import awildgoose.gooselights.lights.LightManager;
import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;

@SuppressWarnings("unused")
public class GooseLightsLambdEntrypoint implements DynamicLightsInitializer {
    @Override
    public void onInitializeDynamicLights(DynamicLightsContext context) {
        LightManager.setDlManager(context.dynamicLightBehaviorManager());
    }
}