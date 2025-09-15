package awildgoose.gooselights;

import awildgoose.gooselights.lights.LightManager;
import dev.lambdaurora.lambdynlights.api.DynamicLightsContext;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;

/**
 * LambDynamicLights entrypoint for setting the DL manager of LightManager
 */
@SuppressWarnings("unused")
public class GooseLightsLambdEntrypoint implements DynamicLightsInitializer {
    @Override
    public void onInitializeDynamicLights(DynamicLightsContext context) {
        LightManager.setDlManager(context.dynamicLightBehaviorManager());
    }
}