#version 150

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:dynamictransforms.glsl>
#moj_import <minecraft:projection.glsl>

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform sampler2D Sampler2;

layout(std140) uniform GooseLights {
    vec4 lights[256];
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

    // vertexColor = Color * minecraft_sample_lightmap(Sampler2, UV2);
    vec4 vanillaLight = minecraft_sample_lightmap(Sampler2, UV2);

    // Position is the block pos of this chunk
    // ColorModulator.xyz is the world pos of this chunk
    vec3 worldPos = Position + ColorModulator.xyz;
    vec4 myLight = vec4(1, 1, 1, 1);
    if (worldPos.x == 1f && worldPos.z == 1f)
        myLight = vec4(1f, 0f, 0f, 1f);
    vertexColor = Color * mix(vanillaLight, myLight, 0.5);

    texCoord0 = UV0;
}
