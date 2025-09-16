package awildgoose.gooselights.gpu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3f;

import java.awt.*;
import java.nio.ByteBuffer;

@Environment(EnvType.CLIENT)
@SuppressWarnings("unused")
public class GPULight {
    public static final int TYPE_OMNI = 0;
    public static final int TYPE_SPOT = 1;

    private final Vector3f position;
    private Color color;
    private float radius;
    private float intensity;
    private boolean enabled;

    // spotlight-specific
    private final Vector3f forward = new Vector3f(0, -1, 0);
    private float innerCutoff = (float) Math.cos(Math.toRadians(12.5));
    private float outerCutoff = (float) Math.cos(Math.toRadians(17.5));
    private int type;

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

    public Vector3f getPosition() {
        return new Vector3f(position);
    }

    public void setPosition(Vector3f position) {
        this.position.set(position);
        Colormap.markDirty();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
        Colormap.markDirty();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
        Colormap.markDirty();
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
        Colormap.markDirty();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        Colormap.markDirty();
    }

    public Vector3f getForward() {
        return new Vector3f(forward);
    }

    public void setForward(Vector3f forward) {
        this.forward.set(forward).normalize();
        Colormap.markDirty();
    }

    public float getInnerCutoff() {
        return innerCutoff;
    }

    public void setInnerCutoff(float degrees) {
        this.innerCutoff = (float) Math.cos(Math.toRadians(degrees));
        Colormap.markDirty();
    }

    public float getOuterCutoff() {
        return outerCutoff;
    }

    public void setOuterCutoff(float degrees) {
        this.outerCutoff = (float) Math.cos(Math.toRadians(degrees));
        Colormap.markDirty();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
        Colormap.markDirty();
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
