package awildgoose.gooselights.lights;

import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class SpotLight implements DynamicLightBehavior {
    private Vec3d pos;
    private Vec3d direction;
    private double coneAngleRadians;
    private int luminance;
    private double range;

    private BoundingBox box;
    private boolean dirty = true;

    public SpotLight(Vec3d pos, Vec3d direction, double coneAngleRadians, int luminance, double range) {
        this.pos = pos;
        this.direction = direction.normalize();
        this.coneAngleRadians = coneAngleRadians;
        this.luminance = luminance;
        this.range = range;
        recalcBox();
    }

    private void recalcBox() {
        this.box = new BoundingBox(
                (int) (pos.x - range), (int) (pos.y - range), (int) (pos.z - range),
                (int) (pos.x + range), (int) (pos.y + range), (int) (pos.z + range)
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
        if (dist > range) return 0;

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

    public void setPos(Vec3d pos) {
        this.pos = pos;
        recalcBox();
    }

    public void setDirection(Vec3d dir) {
        this.direction = dir.normalize();
        this.dirty = true;
    }

    public void setConeAngleRadians(double radians) {
        this.coneAngleRadians = radians;
        this.dirty = true;
    }

    public void setLuminance(int luminance) {
        this.luminance = luminance;
        this.dirty = true;
    }

    public void setRange(double range) {
        this.range = range;
        recalcBox();
    }
}