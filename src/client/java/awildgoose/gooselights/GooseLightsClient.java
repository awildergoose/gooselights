package awildgoose.gooselights;

import awildgoose.gooselights.lights.LightManager;
import awildgoose.gooselights.lights.OmniLight;
import awildgoose.gooselights.lights.SpotLight;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Client initializer
 */
public class GooseLightsClient implements ClientModInitializer {
	OmniLight omni;
	SpotLight spot;

	@Override
	public void onInitializeClient() {
		WorldRenderEvents.END.register(ctx -> {
			if (omni == null || spot == null) {
				omni = new OmniLight(new Vec3d(7, -63, 10), 15, 12);
				LightManager.addLight(omni);

				spot = new SpotLight(new Vec3d(20, -63, 20),
						new Vec3d(0, -1, 0),
						Math.toRadians(25),
						15,
						20);
				LightManager.addLight(spot);
			}

			Quaternionf camRot = ctx.camera().getRotation();
			Vector3f forward = new Vector3f(0, 0, -1).rotate(camRot);

			if (MinecraftClient.getInstance().player != null) {
				spot.setPos(MinecraftClient.getInstance().player.getPos());
			}

			// Freeze vertical rotation because why not
			spot.setDirection(new Vec3d(forward.x, 0, forward.z));
			spot.setLuminance(30);
			spot.setConeAngleRadians(Math.toRadians(45));
			spot.setRadius(20);
		});
	}
}