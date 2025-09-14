package awildgoose.gooselights.lights;

import net.minecraft.util.math.Vec3d;

public class SpotLight extends Light {
    public Vec3d direction;
    public float angle;

    public SpotLight(Vec3d position, Vec3d direction, float angle, float radius, float brightness, int color, boolean bloom) {
        super(position, radius, brightness, color, bloom);
        this.direction = direction.normalize();
        this.angle = angle;
    }

    public void lookAt(Vec3d target) {
        this.direction = target.subtract(this.position).normalize();
    }
}
