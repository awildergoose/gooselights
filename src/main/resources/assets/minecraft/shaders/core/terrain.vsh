#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>
#moj_import <minecraft:globals.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;

#define MAX_LIGHTS 256

struct GPULight {
    vec3 position;
    float radius;
    vec3 color;
    float intensity;
};

layout(std140) uniform GooseLights {
    int lightCount;
    int pad0; int pad1; int pad2; // 16 bytes
    GPULight lights[MAX_LIGHTS];
};

out float sphericalVertexDistance;
out float cylindricalVertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;

vec4 minecraft_sample_lightmap(sampler2D lightMap, ivec2 uv) {
    return texture(lightMap, clamp(uv / 256.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0)));
}

void main() {
    vec3 pos = Position + ModelOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    sphericalVertexDistance = fog_spherical_distance(pos);
    cylindricalVertexDistance = fog_cylindrical_distance(pos);

    // Position is the block pos of this chunk
    // ColorModulator.xyz is the world pos of this chunk
    vec3 worldPos = Position + ColorModulator.xyz;

    vec4 accumulatedLight = minecraft_sample_lightmap(Sampler2, UV2);

    for (int i = 0; i < lightCount; i++) {
        GPULight l = lights[i];
        float dist = length(worldPos - l.position);
        if (dist < l.radius) {
            float intensity = (1.0 - dist / l.radius) * l.intensity;
            accumulatedLight += vec4(l.color, 1.0) * intensity;
        }
    }

    accumulatedLight = clamp(accumulatedLight, 0.0, 1.0);
    vertexColor = Color * accumulatedLight;

    texCoord0 = UV0;
}
