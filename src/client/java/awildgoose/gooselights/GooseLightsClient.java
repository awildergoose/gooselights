package awildgoose.gooselights;

import awildgoose.gooselights.block.ModBlocks;
import awildgoose.gooselights.gpu.GPULight;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;

import java.util.List;

public class GooseLightsClient implements ClientModInitializer {
	public static final int MAX_LIGHTS = 256;
	public static List<GPULight> lights = new java.util.ArrayList<>();
	public static String MOD_ID = "gooselights";

	@Override
	public void onInitializeClient() {
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((client, world) -> lights.clear());
		ModBlocks.initialize();
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.OPERATOR).register((itemGroup) -> itemGroup.add(ModBlocks.SPOTLIGHT.asItem()));
	}
}