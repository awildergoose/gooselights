package awildgoose.gooselights.lights;

import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.awt.*;

public class Light {
    public boolean bloom;
    public Vec3d position;
    public float radius;
    public float brightness;
    public Vector3f color;
    public LightType type;

    public Vector3f direction;
    public float coneAngle;

    public Light(Vec3d position, float radius, float brightness, Color color, boolean bloom) {
        this.position = position;
        this.radius = radius;
        this.brightness = brightness;
        this.color = new Vector3f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f);
        this.bloom = bloom;
        this.type = LightType.OMNI;
    }

    public Light(Vec3d position, Vec3d dir, float coneAngle, float radius, float brightness, Color color, boolean bloom) {
        this.position = position;
        this.direction = new Vector3f((float)dir.x, (float)dir.y, (float)dir.z).normalize();
        this.coneAngle = coneAngle;
        this.radius = radius;
        this.brightness = brightness;
        this.color = new Vector3f(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f);
        this.bloom = bloom;
        this.type = LightType.SPOT;
    }
}