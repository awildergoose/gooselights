package awildgoose.gooselights.render;

import awildgoose.gooselights.lights.Light;
import awildgoose.gooselights.lights.LightType;
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
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import org.joml.Quaternionf;
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

    public void render(Vector3f lightScreenPos, Light light) {
        MinecraftClient mc = MinecraftClient.getInstance();
        var fb = mc.getFramebuffer();
        var colorView = fb.getColorAttachmentView();
        var depthView = fb.getDepthAttachmentView();

        Camera cam = mc.gameRenderer.getCamera();
        Quaternionf camRot = cam.getRotation();
        Vector3f lightDirCam = new Vector3f(light.direction);
        camRot.conjugate().transform(lightDirCam);
        lightDirCam.normalize();

        GpuBufferSlice uniformSlice = writeUniform(
                lightScreenPos,
                light.color,
                light.radius,
                light.brightness,
                fb.viewportWidth,
                fb.viewportHeight,
                light.type == LightType.OMNI ? 0f : 1f,
                light.bloom ? 1f : 0f,
                lightDirCam,
                light.coneAngle
        );

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

    private static GpuBufferSlice writeUniform(Vector3f screenPos, Vector3f color, float radius, float brightness, float fbWidth, float fbHeight, float lightType, float bloom, Vector3f direction, float coneAngle) {
        // vec4 = 16
        // vec3 = 12
        // vec2 = 8
        // float = 4
        ByteBuffer buf = ByteBuffer.allocateDirect(64).order(ByteOrder.nativeOrder());

// vec4 LightPosRadius
        buf.putFloat(screenPos.x);
        buf.putFloat(screenPos.y);
        buf.putFloat(radius);
        buf.putFloat(lightType);

// vec4 LightColorBright
        buf.putFloat(color.x);
        buf.putFloat(color.y);
        buf.putFloat(color.z);
        buf.putFloat(brightness);

// vec4 LightDirCone
        buf.putFloat(direction.x);
        buf.putFloat(direction.y);
        buf.putFloat(direction.z);
        buf.putFloat((float)Math.cos(coneAngle/2));

// vec2 ScreenSize
        buf.putFloat(fbWidth);
        buf.putFloat(fbHeight);

// float Bloom + padding
        buf.putFloat(bloom);
        buf.putFloat(0f);

        buf.flip();
        GpuBuffer buffer = RenderSystem.getDevice().createBuffer(() -> "Light UBO", buf.remaining(), buf);
        return buffer.slice();
    }
}
