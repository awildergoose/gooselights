#version 150

#moj_import <minecraft:dynamictransforms.glsl>

// this can be used for GameTime
// #moj_import <minecraft:globals.glsl>

struct GPULight {
    vec3 position;
    float radius;
    vec3 color;
    float intensity;
    // spotlight
    vec3 forward;
    float innerCutoff;
    float outerCutoff;
    int type; // 0 = omni, 1 = spot
};

layout(std140) uniform GooseLights {
    int lightCount;
    int pad0; int pad1; int pad2; // 16 bytes
    GPULight lights[MAX_LIGHTS];
};

vec4 getAccumulatedLight(vec3 Position, vec4 vanillaLight) {
    // Position is the block pos of this chunk
    // ColorModulator.xyz is the world pos of this chunk
    vec3 worldPos = Position + ColorModulator.xyz;
    vec4 accumulatedLight = vanillaLight;

    for (int i = 0; i < lightCount; i++) {
        GPULight light = lights[i];
        vec3 toPoint = worldPos - light.position;
        float dist = length(toPoint);
        vec3 lightDir = normalize(toPoint);

        float attenuation = 0.0;
        if (light.type == 0) { // omni
            attenuation = max(0.0, 1.0 - dist / light.radius);
        } else { // spot
            vec3 forward = normalize(light.forward);
            float cosTheta = dot(lightDir, forward);
            if (cosTheta > light.outerCutoff) {
                float angleFactor = 0.0;
                if (cosTheta >= light.innerCutoff) {
                    angleFactor = 1.0;
                } else {
                    angleFactor = (cosTheta - light.outerCutoff) / (light.innerCutoff - light.outerCutoff);
                }
                attenuation = max(0.0, 1.0 - dist / light.radius) * angleFactor;
            }
        }

        accumulatedLight += vec4(light.color * light.intensity * attenuation, 0.0);
    }

    accumulatedLight = clamp(accumulatedLight, 0.0, 1.0);
    return accumulatedLight;
}