package awildgoose.gooselights.gpu;

import org.joml.Vector3f;

import java.awt.*;
import java.nio.ByteBuffer;

public class GPULight {
    public Vector3f position;
    public Color color;
    public float radius;
    public float intensity;

    public GPULight(Vector3f position, Color color, float radius, float intensity) {
        this.position = position;
        this.color = color;
        this.radius = radius;
        this.intensity = intensity;
    }

    public void upload(ByteBuffer buf) {
        // vec4 posRadius;
        buf.putFloat(position.x);
        buf.putFloat(position.y);
        buf.putFloat(position.z);
        buf.putFloat(radius);

        // vec4 colorIntensity;
        buf.putFloat(color.getRed() / 255f);
        buf.putFloat(color.getGreen() / 255f);
        buf.putFloat(color.getBlue() / 255f);
        buf.putFloat(intensity);
    }
}
