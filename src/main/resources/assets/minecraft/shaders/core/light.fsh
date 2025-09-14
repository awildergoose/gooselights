#version 150

layout(std140) uniform LightData {
    vec4 LightPosRadius;    // xy = screen pos, z = radius, w = lightType
    vec4 LightColorBright;  // rgb = color, a = brightness
    vec2 ScreenSize;
    float Bloom;
    float _pad0;
};

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy;
    float screenY = ScreenSize.y - uv.y;

    float dist = length(uv - LightPosRadius.xy);
    float radius = LightPosRadius.z;
    float lightType = LightPosRadius.w;
    float intensity = clamp(1.0 - dist / radius, 0.0, 1.0);
    intensity = pow(intensity, 2.0);
    intensity *= LightColorBright.a;
    intensity *= (1.0 + Bloom);

    fragColor = vec4(LightColorBright.rgb * intensity, 1.0);
}
