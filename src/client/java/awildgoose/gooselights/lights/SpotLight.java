package awildgoose.gooselights.lights;

import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

/**
 * Spotlight for things like flashlights, headlights, searchlights, etc
 */
@SuppressWarnings("unused")
public class SpotLight implements DynamicLightBehavior {
    private Vec3d pos;
    private Vec3d direction;
    private double coneAngleRadians;
    private int luminance;
    private double radius;

    private BoundingBox box;
    private boolean dirty = true;

    /**
     * Creates a new spotlight
     * @param pos Position of light
     * @param direction Direction of light
     * @param coneAngleRadians Angle of spotlight in radians
     * @param luminance Luminance of light (how bright it is)
     * @param radius Radius of light (how big it is)
     */
    public SpotLight(Vec3d pos, Vec3d direction, double coneAngleRadians, int luminance, double radius) {
        this.pos = pos;
        this.direction = direction.normalize();
        this.coneAngleRadians = coneAngleRadians;
        this.luminance = luminance;
        this.radius = radius;
        recalcBox();
    }

    private void recalcBox() {
        this.box = new BoundingBox(
                (int) Math.floor(pos.x - radius),
                (int) Math.floor(pos.y - radius),
                (int) Math.floor(pos.z - radius),
                (int) Math.ceil(pos.x + radius),
                (int) Math.ceil(pos.y + radius),
                (int) Math.ceil(pos.z + radius)
        );
        this.dirty = true;
    }

    @Override
    public double lightAtPos(BlockPos target, double falloffRatio) {
        Vec3d toTarget = new Vec3d(
                target.getX() + 0.5 - pos.x,
                target.getY() + 0.5 - pos.y,
                target.getZ() + 0.5 - pos.z
        );

        double dist = toTarget.length();
        if (dist > radius) return 0;

        Vec3d norm = toTarget.normalize();
        double dot = direction.dotProduct(norm);

        double cosLimit = Math.cos(coneAngleRadians);
        if (dot < cosLimit) return 0;

        double angleFactor = (dot - cosLimit) / (1.0 - cosLimit);
        angleFactor = Math.pow(angleFactor, 4.0);

        return Math.max(0, luminance - dist * falloffRatio) * angleFactor;
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
     * Set direction of light
     * @param dir New direction (automatically gets normalized)
     */
    public void setDirection(Vec3d dir) {
        this.direction = dir.normalize();
        this.dirty = true;
    }

    /**
     * Set cone angle of light
     * @param radians New cone angle (in radians)
     */
    public void setConeAngleRadians(double radians) {
        this.coneAngleRadians = radians;
        this.dirty = true;
    }

    /**
     * Set luminance of light
     * @param luminance New luminance (brightness)
     */
    public void setLuminance(int luminance) {
        this.luminance = luminance;
        this.dirty = true;
    }

    /**
     * Set radius of light
     * @param radius New radius (size)
     */
    public void setRadius(double radius) {
        this.radius = radius;
        recalcBox();
    }
}