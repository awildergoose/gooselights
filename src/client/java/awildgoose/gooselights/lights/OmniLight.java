package awildgoose.gooselights.lights;

import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

/**
 * Omnidirectional light for things like torches, lanterns, glow-lichen, etc
 */
@SuppressWarnings("unused")
public class OmniLight implements DynamicLightBehavior {
    private Vec3d pos;
    private int luminance;
    private double radius;

    private BoundingBox box;
    private boolean dirty = true;

    /**
     * Creates a new omni-directional light
     * @param pos Position of light
     * @param luminance Luminance of light (how bright it is)
     * @param radius Radius of light (how big it is)
     */
    public OmniLight(Vec3d pos, int luminance, double radius) {
        this.pos = pos;
        this.luminance = luminance;
        this.radius = radius;
        recalcBox();
    }

    private void recalcBox() {
        this.box = new BoundingBox(
                (int) (pos.x - radius), (int) (pos.y - radius), (int) (pos.z - radius),
                (int) (pos.x + radius), (int) (pos.y + radius), (int) (pos.z + radius)
        );
        this.dirty = true;
    }

    @Override
    public double lightAtPos(BlockPos target, double falloffRatio) {
        double dx = target.getX() + 0.5 - pos.x;
        double dy = target.getY() + 0.5 - pos.y;
        double dz = target.getZ() + 0.5 - pos.z;
        double dist = Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
        return Math.max(0, luminance - dist * falloffRatio);
    }

    @Override
    public @NotNull BoundingBox getBoundingBox() {
        return box;
    }

    @Override
    public boolean hasChanged() {
        if (dirty) {
            dirty = false;
            return true;
        }
        return false;
    }

    /**
     * Set position of light
     * @param pos New position
     */
    public void setPos(Vec3d pos) {
        this.pos = pos;
        recalcBox();
    }

    /**
     * Set radius of light
     * @param radius New radius (size)
     */
    public void setRadius(double radius) {
        this.radius = radius;
        recalcBox();
    }

    /**
     * Set luminance of light
     * @param luminance New luminance (brightness)
     */
    public void setLuminance(int luminance) {
        this.luminance = luminance;
        this.dirty = true;
    }
}