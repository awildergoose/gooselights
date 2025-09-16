package awildgoose.gooselights;

import awildgoose.gooselights.gpu.GPULight;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.joml.Vector3f;

import java.awt.*;
import java.util.List;

public class GooseLightsClient implements ClientModInitializer {
	public static final int MAX_LIGHTS = 256;
	public static List<GPULight> lights = new java.util.ArrayList<>();

	public static LivingEntityRenderState lastRenderState;

	@Override
	public void onInitializeClient() {
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
			lights.clear();
			lights.add(new GPULight(new Vector3f(0, -60, 0), Color.GREEN, 15f, 1f));
		});
	}
}