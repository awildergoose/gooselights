package awildgoose.gooselights.gpu;

import org.joml.Vector3f;

import java.awt.*;
import java.nio.ByteBuffer;

public class GPULight {
    public static final int TYPE_OMNI = 0;
    public static final int TYPE_SPOT = 1;

    public Vector3f position;
    public Color color;
    public float radius;
    public float intensity;
    public boolean enabled;

    // spotlight-specific
    public Vector3f forward = new Vector3f(0, -1, 0);
    public float innerCutoff = (float)Math.cos(Math.toRadians(12.5));
    public float outerCutoff = (float)Math.cos(Math.toRadians(17.5));
    public int type = TYPE_OMNI;

    public GPULight(Vector3f position, Color color, float radius, float intensity) {
        this.position = position;
        this.color = color;
        this.radius = radius;
        this.intensity = intensity;
        this.enabled = true;
        this.type = TYPE_OMNI;
    }

    public GPULight(Vector3f position, Color color, float radius, float intensity,
                    Vector3f forward, float innerCutoffDeg, float outerCutoffDeg) {
        this(position, color, radius, intensity);
        this.forward.set(forward).normalize();
        this.innerCutoff = (float)Math.cos(Math.toRadians(innerCutoffDeg));
        this.outerCutoff = (float)Math.cos(Math.toRadians(outerCutoffDeg));
        this.type = TYPE_SPOT;
    }

    public void upload(ByteBuffer buf) {
        buf.putFloat(position.x);
        buf.putFloat(position.y);
        buf.putFloat(position.z);
        buf.putFloat(radius);

        buf.putFloat(color.getRed() / 255f);
        buf.putFloat(color.getGreen() / 255f);
        buf.putFloat(color.getBlue() / 255f);
        buf.putFloat(intensity);

        // spotlight specific
        buf.putFloat(forward.x);
        buf.putFloat(forward.y);
        buf.putFloat(forward.z);
        buf.putFloat(innerCutoff);

        buf.putFloat(outerCutoff);
        buf.putFloat(type);
        buf.putFloat(0f); // padding
        buf.putFloat(0f); // padding
    }
}
