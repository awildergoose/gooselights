package awildgoose.gooselights.lights;

import net.minecraft.util.math.Vec3d;

public class PointLight extends Light {
    public PointLight(Vec3d position, float radius, float brightness, int color, boolean bloom) {
        super(position, radius, brightness, color, bloom);
    }
}

