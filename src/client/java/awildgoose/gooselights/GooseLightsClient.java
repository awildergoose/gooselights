package awildgoose.gooselights;

import awildgoose.gooselights.lights.Light;
import awildgoose.gooselights.lights.LightManager;
import awildgoose.gooselights.lights.LightType;
import awildgoose.gooselights.render.LightRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;

public class GooseLightsClient implements ClientModInitializer {
	private final Light light = new Light(
			Vec3d.ZERO,
			100f,
			20f,
			Color.WHITE,
			false
	);
	public LightRenderer renderer;

	public static Vec3d projectToScreen(Light light) {
		MinecraftClient mc = MinecraftClient.getInstance();
		Camera cam = mc.gameRenderer.getCamera();
		Framebuffer fb = mc.getFramebuffer();
		int fbWidth = fb.viewportWidth;
		int fbHeight = fb.viewportHeight;
		Vec3d camPos = cam.getPos();
		Quaternionf camRot = cam.getRotation();

		Vector3f rel = new Vector3f(
				(float)(light.position.x - camPos.x),
				(float)(light.position.y - camPos.y),
				(float)(light.position.z - camPos.z)
		);

		Vector3f camForward = new Vector3f(0, 0, -1);
		camRot.transform(camForward);

		if (rel.dot(camForward) <= 0) {
			return null;
		}

		camRot.conjugate().transform(rel);

		float fov = (float)Math.toRadians(mc.options.getFov().getValue());
		float aspect = fbWidth / (float)fbHeight;

		float ndcX = rel.x / (-rel.z * (float)Math.tan(fov/2f) * aspect);
		float ndcY = rel.y / (-rel.z * (float)Math.tan(fov/2f));

		float px = (ndcX * 0.5f + 0.5f) * fbWidth;
		float py = (ndcY * 0.5f + 0.5f) * fbHeight;

		float screenRadius = light.type == LightType.OMNI ? light.radius / Math.max(rel.dot(camForward), 0.1f)
				: (float)(Math.tan(light.coneAngle/2) * rel.length());

		return new Vec3d(px, py, screenRadius);
	}

	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> renderer = new LightRenderer());

		WorldRenderEvents.BEFORE_ENTITIES.register(context -> {
			MatrixStack matrices = context.matrixStack();

			if (matrices != null) {
				Vec3d screenPos = projectToScreen(light);

				if (screenPos != null)
					renderer.render(
							new Vector3f((float)screenPos.x, (float)screenPos.y, 0),
							light
					);
			}

			light.position = new Vec3d(0.5, -60, 0.5);
			light.setColor(
					Color.getHSBColor(
							 360,
							1,
							1
					)
			);
			light.brightness = 10;
			light.type = LightType.SPOT;

			if (MinecraftClient.getInstance().player != null) {
				light.direction = MinecraftClient.getInstance().player.getPos().toVector3f()
						.sub(light.position.toVector3f())
						.normalize();
			}
		});

		LightManager.addLight(light);
	}
}