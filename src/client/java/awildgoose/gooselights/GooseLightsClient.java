package awildgoose.gooselights;

import awildgoose.gooselights.gpu.GPULight;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Client initializer
 */
public class GooseLightsClient implements ClientModInitializer {
	public static final int MAX_LIGHTS = 256;
	public static List<GPULight> lights = new ArrayList<>();

	/**
	 * Called by fabric to initialize the mod
	 */
	public GooseLightsClient() {}

	@Override
	public void onInitializeClient() {
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> lights.clear());
		WorldRenderEvents.END.register(ctx -> {
			Vector3f camPos = ctx.camera().getCameraPos().toVector3f();

			GPULight flashlight;
			if (GooseLightsClient.lights.isEmpty()) {
				flashlight = new GPULight(
						new Vector3f(camPos),
						Color.RED,
						15f,
						1f,
						new Vector3f(0, 0, -1),
						12.5f,
						17.5f
				);
				flashlight.type = GPULight.TYPE_SPOT;
				GooseLightsClient.lights.add(flashlight);
			} else {
				flashlight = GooseLightsClient.lights.get(0);
			}

			flashlight.position.set(camPos);
			Vector3f camDir = new Vector3f(0, 0, -1);
			ctx.camera().getRotation().transform(camDir);
			flashlight.forward.set(camDir).normalize();
			flashlight.position.add(new Vector3f(camDir).mul(1.5f));

			flashlight.radius = 15f;
			flashlight.intensity = 1f;
			flashlight.color = Color.RED;
			flashlight.type = GPULight.TYPE_SPOT;
		});
	}
}