#version 150

layout(std140) uniform LightData {
    vec4 LightPosRadius;
    vec4 LightColorBright;
    vec4 LightDirCone;
    vec2 ScreenSize;
    float Bloom;
    float _pad0;
};

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy;

    float radius = LightPosRadius.z;
    float lightType = LightPosRadius.w;

    float intensity = 0.0;

    if (lightType < 0.5) {
        // omni
        float dist = length(uv - LightPosRadius.xy);
        intensity = clamp(1.0 - dist / radius, 0.0, 1.0);
        intensity = pow(intensity, 2.0);
    } else {
        // spot
        vec2 ndc = (uv / ScreenSize) * 2.0 - 1.0;
        float aspect = ScreenSize.x / ScreenSize.y;
        float fovY = 2.0 * atan(tan(0.5));
        vec3 ray = normalize(vec3(ndc.x * aspect * tan(fovY/2.0),
                                  ndc.y * tan(fovY/2.0),
                                  -1.0));

        float cosTheta = dot(ray, normalize(LightDirCone.xyz));
        intensity = smoothstep(LightDirCone.w, 1.0, cosTheta);

        float dist = length(uv - LightPosRadius.xy);
        intensity *= clamp(1.0 - dist / radius, 0.0, 1.0);
        intensity = pow(intensity, 2.0);
    }

    intensity *= LightColorBright.a;
    intensity *= (1.0 + Bloom);

    fragColor = vec4(LightColorBright.rgb * intensity, 1.0);
}
