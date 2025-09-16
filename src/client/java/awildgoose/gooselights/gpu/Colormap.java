package awildgoose.gooselights.gpu;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import static awildgoose.gooselights.GooseLightsClient.MAX_LIGHTS;

public class Colormap {
    public static int UPDATE_FREQUENCY = 2;
    public static List<GPULight> lights = new java.util.ArrayList<>();

    public static void setColormap(RenderPass renderPass) {
        if (colormap == null || tickCounter % UPDATE_FREQUENCY == 0)
            updateColormap();

        renderPass.setUniform("GooseLights", colormap);
    }

    public static int tickCounter = 0;
    private static GpuBuffer buffer;
    private static GpuBufferSlice colormap;

    private static void updateColormap() {
        int stride = 64;
        int size = 4 + 12 + MAX_LIGHTS * stride;

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
    }
}
