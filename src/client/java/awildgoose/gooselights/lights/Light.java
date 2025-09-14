package awildgoose.gooselights.lights;

import net.minecraft.util.math.Vec3d;

public abstract class Light {
    public boolean bloom;
    public Vec3d position;
    public float radius;
    public float brightness;
    public int color;

    public Light(Vec3d position, float radius, float brightness, int color, boolean bloom) {
        this.position = position;
        this.radius = radius;
        this.brightness = brightness;
        this.color = color;
        this.bloom = bloom;
    }
}
