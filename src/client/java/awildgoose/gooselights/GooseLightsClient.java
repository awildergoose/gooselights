package awildgoose.gooselights;

import awildgoose.gooselights.gpu.GPULight;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Client initializer
 */
public class GooseLightsClient implements ClientModInitializer {
	public static final int MAX_LIGHTS = 256;
	public static List<GPULight> lights = new ArrayList<>();

	public static String MOD_ID = "gooselights";
	public static Logger GooseLogger = LoggerFactory.getLogger(MOD_ID);

	/**
	 * Called by fabric to initialize the mod
	 */
	public GooseLightsClient() {}

	@Override
	public void onInitializeClient() {
		GPULight light = new GPULight(
				new Vector3f(4.0f, -62.0f, 2.0f),
				Color.RED,
				10,
				1
		);

		lights.add(light);

		WorldRenderEvents.END.register(ctx -> {
			light.position = ctx.camera().getCameraPos().toVector3f();
			light.radius = 10;
		});
	}
}