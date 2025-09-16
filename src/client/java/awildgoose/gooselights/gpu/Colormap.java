package awildgoose.gooselights.gpu;

import awildgoose.gooselights.GooseLightsClient;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static awildgoose.gooselights.GooseLightsClient.MAX_LIGHTS;

public class Colormap {
    public static int UPDATE_FREQUENCY = 2;

    public static void setColormap(RenderPass renderPass) {
        if (colormap == null || tickCounter % UPDATE_FREQUENCY == 0)
            updateColormap();

        renderPass.setUniform("GooseLights", colormap);
        tickCounter++;
    }

    private static int tickCounter = 0;
    private static GpuBufferSlice colormap;

    private static void updateColormap() {
        int stride = 64;
        ByteBuffer buf = ByteBuffer.allocateDirect(4 + 12 + MAX_LIGHTS * stride)
                .order(ByteOrder.nativeOrder());

        buf.putInt(GooseLightsClient.lights.size());
        buf.putInt(0).putInt(0).putInt(0); // padding

        for (int i = 0; i < MAX_LIGHTS; i++) {
            if (i < GooseLightsClient.lights.size() && GooseLightsClient.lights.get(i).enabled) {
                GooseLightsClient.lights.get(i).upload(buf);
            } else {
                for (int j = 0; j < stride / 4; j++) buf.putFloat(0f);
            }
        }

        buf.flip();
        GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "GooseLights UBO", buf.remaining(), buf);
        colormap = buffer.slice();
    }
}
