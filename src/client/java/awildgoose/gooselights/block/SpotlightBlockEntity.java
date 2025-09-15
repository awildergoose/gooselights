package awildgoose.gooselights.block;

import awildgoose.gooselights.GooseLightsClient;
import awildgoose.gooselights.gpu.GPULight;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;

import java.awt.*;

public class SpotlightBlockEntity extends BlockEntity {
    private GPULight lampLight;

    float alignX, alignY;
    Color lightColor = Color.WHITE;

    public SpotlightBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.SPOTLIGHT_ENTITY, pos, state);
    }

    @Override
    public void markRemoved() {
        super.markRemoved();
        removeLight();
    }

    @Override
    protected void writeData(WriteView view) {
        view.putFloat("alignX", alignX);
        view.putFloat("alignY", alignY);
        view.putInt("color", lightColor.getRGB());

        super.writeData(view);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        alignX = view.getFloat("alignX", 0);
        alignY = view.getFloat("alignY", 0);
        lightColor = new Color(view.getInt("color", Color.WHITE.getRGB()), true);
    }

    void addLight(BlockPos pos) {
        Vector3f lightPos = new Vector3f(
                pos.getX() + 0.5f,
                pos.getY() + 1f,
                pos.getZ() + 0.5f
        );

        lampLight = new GPULight(
                lightPos,
                lightColor,
                15f,
                10f,
                calculateForward(),
                90f,
                90f
        );
        lampLight.type = GPULight.TYPE_SPOT;
        GooseLightsClient.lights.add(lampLight);
    }

    void removeLight() {
        if (lampLight != null) {
            GooseLightsClient.lights.remove(lampLight);
            lampLight = null;
        }
    }

    public void addAlignX() {
        alignX = (alignX + 15) % 360;
        updateLightDirection();
    }

    public void addAlignY() {
        alignY = (alignY + 15) % 360;
        updateLightDirection();
    }

    public void setColor(Color color) {
        this.lightColor = color;
        if (lampLight != null) {
            lampLight.color = color;
        }
    }

    private Vector3f calculateForward() {
        float yawRad = (float) Math.toRadians(alignX);
        float pitchRad = (float) Math.toRadians(alignY);
        return new Vector3f(
                (float) (-Math.sin(yawRad) * Math.cos(pitchRad)),
                (float) -Math.sin(pitchRad),
                (float) (-Math.cos(yawRad) * Math.cos(pitchRad))
        ).normalize();
    }

    private void updateLightDirection() {
        if (lampLight == null) return;
        lampLight.forward.set(calculateForward());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
