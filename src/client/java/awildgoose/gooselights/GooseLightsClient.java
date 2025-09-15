package awildgoose.gooselights;

import awildgoose.gooselights.gpu.GPULight;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GooseLightsClient implements ClientModInitializer {
	public static final int MAX_LIGHTS = 256;
	public static List<GPULight> lights = new ArrayList<>();
	private static final Random random = new Random();
	private static final float GRASS_Y = -63f;

	@Override
	public void onInitializeClient() {
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> lights.clear());

		WorldRenderEvents.END.register(ctx -> {
			Vector3f camPos = ctx.camera().getCameraPos().toVector3f();

			if (lights.isEmpty()) {
				populateTestLights(camPos);
			}

			for (GPULight light : lights) {
				if (light.type == GPULight.TYPE_SPOT) {
					Vector3f camDir = new Vector3f(0, 0, -1);
					ctx.camera().getRotation().transform(camDir);
					light.position.set(camPos);
					light.forward.set(camDir).normalize();
				}

				if (light.color.equals(Color.ORANGE)) {
					float t = (System.currentTimeMillis() % 10000) / 10000f;
					light.position.x = 10f * (float)Math.cos(t * Math.PI * 2);
					light.position.z = 10f * (float)Math.sin(t * Math.PI * 2);
					light.position.y = GRASS_Y + 20f;
					light.forward.set(new Vector3f(-light.position.x, -light.position.y + GRASS_Y, -light.position.z).normalize());
				}
			}
		});
	}

	private void populateTestLights(Vector3f camPos) {
		GPULight flashlight = new GPULight(
				new Vector3f(camPos),
				Color.RED,
				15f,
				1f,
				new Vector3f(0, 0, -1),
				12.5f,
				17.5f
		);
		flashlight.type = GPULight.TYPE_SPOT;
		lights.add(flashlight);

		lights.add(new GPULight(new Vector3f(10, GRASS_Y, 10), Color.CYAN, 8f, 3f));
		lights.add(new GPULight(new Vector3f(-8, GRASS_Y, 12), Color.MAGENTA, 6f, 3f));
		lights.add(new GPULight(new Vector3f(5, GRASS_Y, -7), Color.YELLOW, 10f, 3f));

		for (int i = 0; i < 5; i++) {
			Vector3f pos = new Vector3f(
					camPos.x + random.nextFloat() * 20 - 10,
					GRASS_Y + random.nextFloat() * 4,
					camPos.z + random.nextFloat() * 20 - 10
			);
			Color col = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat());
			lights.add(new GPULight(pos, col, 5f + random.nextFloat() * 5f, 0.5f + random.nextFloat() * 0.5f));
		}

		GPULight spinningSpot = new GPULight(
				new Vector3f(0, GRASS_Y + 20f, 0),
				Color.ORANGE,
				12f,
				1f,
				new Vector3f(1, -1, 0),
				10f,
				20f
		);
		spinningSpot.type = GPULight.TYPE_SPOT;
		lights.add(spinningSpot);
	}
}