package awildgoose.gooselights;

import awildgoose.gooselights.lights.LightManager;
import awildgoose.gooselights.lights.SpotLight;
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

public class GooseLightsClient implements ClientModInitializer {
	private final SpotLight s = new SpotLight(Vec3d.ZERO, Vec3d.ZERO, (float)Math.toRadians(20), 20f, 2.0f, 0xFFFFAAFF, true);
	public LightRenderer renderer;

	public static Vec3d projectToScreen(Vec3d worldPos) {
		MinecraftClient mc = MinecraftClient.getInstance();
		Camera cam = mc.gameRenderer.getCamera();
		Framebuffer fb = mc.getFramebuffer();
		int fbWidth = fb.viewportWidth;
		int fbHeight = fb.viewportHeight;
		Vec3d camPos = cam.getPos();
		Quaternionf camRot = cam.getRotation();

		Vector3f rel = new Vector3f(
				(float)(worldPos.x - camPos.x),
				(float)(worldPos.y - camPos.y),
				(float)(worldPos.z - camPos.z)
		);
		camRot.conjugate().transform(rel);

		Vector3f forward = cam.getHorizontalPlane();
		float z_cam = rel.dot(forward);

		float fov = (float) Math.toRadians(mc.options.getFov().getValue());
		float aspect = fbWidth / (float) fbHeight;
		float ndcX = rel.x / (-rel.z * (float)Math.tan(fov/2f) * aspect);
		float ndcY = rel.y / (-rel.z * (float)Math.tan(fov/2f));

		float px = (ndcX * 0.5f + 0.5f) * fbWidth;
		float py = (ndcY * 0.5f + 0.5f) * fbHeight;

		return new Vec3d(px, py, z_cam);
	}


	@Override
	public void onInitializeClient() {
		ClientLifecycleEvents.CLIENT_STARTED.register(client -> renderer = new LightRenderer());

		WorldRenderEvents.AFTER_ENTITIES.register(context -> {
			MatrixStack matrices = context.matrixStack();

			if (matrices != null) {
				Vec3d screenPos = projectToScreen(s.position);

				renderer.render(
						new Vector3f((float)screenPos.x, (float)screenPos.y, 0),
						new Vector3f(1,1,1), 50, 1
				);
			}

			s.position = new Vec3d(0, -60, 0);
		});

		LightManager.addLight(s);
	}
}