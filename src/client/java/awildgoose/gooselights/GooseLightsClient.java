package awildgoose.gooselights;

import awildgoose.gooselights.gpu.Colormap;
import awildgoose.gooselights.gpu.GPULight;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.joml.Vector3f;

import java.awt.*;

public class GooseLightsClient implements ClientModInitializer {
	public static final int MAX_LIGHTS = 256;

	public static LivingEntityRenderState lastRenderState;

	@Override
	public void onInitializeClient() {
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> {
			Colormap.lights.clear();
			Colormap.lights.add(new GPULight(new Vector3f(0, -60, 0), Color.RED, 15f, 15f));
		});
	}
}