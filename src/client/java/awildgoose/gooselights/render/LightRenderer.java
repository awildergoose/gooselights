package awildgoose.gooselights.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class LightRenderer {
    public static final RenderPipeline LIGHT_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder()
                    .withLocation("pipeline/light")
                    .withVertexShader("core/fullscreen")
                    .withFragmentShader("core/light")
                    .withBlend(BlendFunction.ADDITIVE)
                    .withUniform("LightData", UniformType.UNIFORM_BUFFER)
                    .withDepthWrite(false)
                    .withVertexFormat(VertexFormats.POSITION, VertexFormat.DrawMode.TRIANGLES)
                    .build()
    );

    private final GpuBuffer vertexBuffer;
    private final GpuBuffer indexBuffer;

    public LightRenderer() {
        ByteBuffer vertexData = ByteBuffer.allocateDirect(4 * 3 * Float.BYTES)
                .order(ByteOrder.nativeOrder());

        vertexData.putFloat(-1f).putFloat(-1f).putFloat(0f);
        vertexData.putFloat( 1f).putFloat(-1f).putFloat(0f);
        vertexData.putFloat( 1f).putFloat( 1f).putFloat(0f);
        vertexData.putFloat(-1f).putFloat( 1f).putFloat(0f);
        vertexData.flip();

        vertexBuffer = RenderSystem.getDevice()
                .createBuffer(() -> "Fullscreen quad vertices", vertexData.remaining(), vertexData);

        ByteBuffer indexData = ByteBuffer.allocateDirect(6 * Short.BYTES)
                .order(ByteOrder.nativeOrder());
        indexData.putShort((short) 0).putShort((short) 1).putShort((short) 2);
        indexData.putShort((short) 2).putShort((short) 3).putShort((short) 0);
        indexData.flip();

        indexBuffer = RenderSystem.getDevice()
                .createBuffer(() -> "Fullscreen quad indices", indexData.remaining(), indexData);
    }

    public void render(Vector3f lightScreenPos, Vector3f lightColor, float radius, float brightness) {
        MinecraftClient mc = MinecraftClient.getInstance();
        var fb = mc.getFramebuffer();
        var colorView = fb.getColorAttachmentView();
        var depthView = fb.getDepthAttachmentView();

        GpuBufferSlice uniformSlice = writeUniform(lightScreenPos, lightColor, radius, brightness, fb.viewportWidth, fb.viewportHeight);

        try (RenderPass pass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "LightShader", colorView,
                        OptionalInt.empty(), depthView, OptionalDouble.empty())) {

            pass.setPipeline(LIGHT_PIPELINE);
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("LightData", uniformSlice);

            pass.setVertexBuffer(0, vertexBuffer);
            pass.setIndexBuffer(indexBuffer, VertexFormat.IndexType.SHORT);
            pass.drawIndexed(0, 0, 6, 1);
        }
    }

    private static GpuBufferSlice writeUniform(Vector3f screenPos, Vector3f color, float radius, float brightness, float fbWidth, float fbHeight) {
        ByteBuffer buf = ByteBuffer.allocateDirect(48).order(ByteOrder.nativeOrder());

        buf.putFloat(screenPos.x).putFloat(screenPos.y).putFloat(0f).putFloat(radius);
        buf.putFloat(color.x).putFloat(color.y).putFloat(color.z).putFloat(brightness);
        buf.putFloat(fbWidth).putFloat(fbHeight);
        buf.putFloat(0f).putFloat(0f); // padding for std140

        buf.flip();
        GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "Light UBO", buf.remaining(), buf);
        return buffer.slice();
    }
}
