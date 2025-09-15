package awildgoose.gooselights.mixin.client;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.client.gl.DynamicUniforms;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.SectionRenderState;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.AbstractChunkRenderData;
import net.minecraft.client.render.chunk.Buffers;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Mixin(WorldRenderer.class)
public class BuiltChunkGetOriginMixin {
    @Shadow private ObjectArrayList<ChunkBuilder.BuiltChunk> builtChunks;

    /**
     * @author awildergoose
     * @reason Replace uniform values to add instead of subtract
     */
    @Overwrite
    private SectionRenderState renderBlockLayers(Matrix4fc matrix4fc, double d, double e, double f) {
        ObjectListIterator<ChunkBuilder.BuiltChunk> objectListIterator = this.builtChunks.listIterator(0);
        EnumMap<BlockRenderLayer, List<RenderPass.RenderObject<GpuBufferSlice[]>>> enumMap = new EnumMap<>(BlockRenderLayer.class);
        int i = 0;

        for (BlockRenderLayer blockRenderLayer : BlockRenderLayer.values()) {
            enumMap.put(blockRenderLayer, new ArrayList<>());
        }

        List<DynamicUniforms.UniformValue> list = new ArrayList<>();
        Vector4f vector4f = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
        Matrix4f matrix4f = new Matrix4f();

        while (objectListIterator.hasNext()) {
            ChunkBuilder.BuiltChunk builtChunk = objectListIterator.next();
            AbstractChunkRenderData abstractChunkRenderData = builtChunk.getCurrentRenderData();

            for (BlockRenderLayer blockRenderLayer2 : BlockRenderLayer.values()) {
                Buffers buffers = abstractChunkRenderData.getBuffersForLayer(blockRenderLayer2);
                if (buffers != null) {
                    GpuBuffer gpuBuffer;
                    VertexFormat.IndexType indexType;
                    if (buffers.getIndexBuffer() == null) {
                        if (buffers.getIndexCount() > i) {
                            i = buffers.getIndexCount();
                        }

                        gpuBuffer = null;
                        indexType = null;
                    } else {
                        gpuBuffer = buffers.getIndexBuffer();
                        indexType = buffers.getIndexType();
                    }

                    BlockPos blockPos = builtChunk.getOrigin();
                    int j = list.size();
                    list.add(
                            new DynamicUniforms.UniformValue(
                                    matrix4fc, new Vector4f(
                                    blockPos.getX(),
                                    blockPos.getY(),
                                    blockPos.getZ(),
0
                                    ), new Vector3f(
                                            (float)(blockPos.getX() - d),
                                            (float)(blockPos.getY() - e),
                                            (float)(blockPos.getZ() - f)
                                    ), matrix4f, 1.0F
                            )
                    );
                    enumMap.get(blockRenderLayer2)
                            .add(
                                    new RenderPass.RenderObject<>(
                                            0,
                                            buffers.getVertexBuffer(),
                                            gpuBuffer,
                                            indexType,
                                            0,
                                            buffers.getIndexCount(),
                                            (gpuBufferSlicesx, uniformUploader) -> uniformUploader.upload("DynamicTransforms", gpuBufferSlicesx[j])
                                    )
                            );
                }
            }
        }

        GpuBufferSlice[] gpuBufferSlices = RenderSystem.getDynamicUniforms()
                .writeAll(list.toArray(new DynamicUniforms.UniformValue[0]));
        return new SectionRenderState(enumMap, i, gpuBufferSlices);
    }
}
