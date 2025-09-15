package awildgoose.gooselights;

import awildgoose.gooselights.gpu.GPULight;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;

import java.util.List;

public class GooseLightsClient implements ClientModInitializer {
	public static final int MAX_LIGHTS = 256;
	public static List<GPULight> lights = new java.util.ArrayList<>();

	@Override
	public void onInitializeClient() {
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> lights.clear());
	}
}