package awildgoose.gooselights.gpu;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static awildgoose.gooselights.GooseLightsClient.MAX_LIGHTS;

public class Colormap {
    private static final List<GPULight> lights = new java.util.ArrayList<>();
    private static boolean dirty = false;

    public static void setColormap(RenderPass renderPass) {
        if (colormap == null || dirty)
            updateColormap();

        renderPass.setUniform("GooseLights", colormap);
    }

    public static void markDirty() {
        dirty = true;
    }

    public static void addLight(GPULight light) {
        lights.add(light);
        markDirty();
    }

    public static void removeLight(GPULight light) {
        lights.remove(light);
        markDirty();
    }

    public static void clearLights() {
        lights.clear();
        markDirty();
    }

    private static GpuBuffer buffer;
    private static GpuBufferSlice colormap;

    private static void updateColormap() {
        int stride = 64;
        int size = 4 + 12 + MAX_LIGHTS * stride;

        Profiler profiler = Profilers.get();
        profiler.push("gooselights");
        ByteBuffer buf = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());

        buf.putInt(lights.size());
        buf.putInt(0).putInt(0).putInt(0); // padding

        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < lights.size() && lights.get(i).enabled) {
                lights.get(i).upload(buf);
            } else {
                for (int j = 0; j < stride / 4; j++) buf.putFloat(0f);
            }
        }

        buf.flip();

        if (buffer != null && !buffer.isClosed()) {
            buffer.close();
        }

        buffer = RenderSystem.getDevice().createBuffer(
                () -> "GooseLights UBO",
                GpuBuffer.USAGE_UNIFORM,
                buf
        );
        colormap = buffer.slice();
        profiler.pop();
    }
}
