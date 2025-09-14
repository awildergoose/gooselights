#version 150

layout(std140) uniform LightData {
    vec4 LightScreenPosRadius; // xy = screen pos, w = radius
    vec4 LightColorBright;     // rgb = color, a = brightness
    vec2 ScreenSize;
    vec2 _pad;
};

out vec4 fragColor;

void main() {
    vec2 fragUV = gl_FragCoord.xy;

    // Distance in pixels from light center
    float dist = length(fragUV - LightScreenPosRadius.xy);

    float radius = LightScreenPosRadius.w;

    // Basic falloff
    float intensity = clamp(1.0 - dist / radius, 0.0, 1.0);
    intensity = pow(intensity, 2.0); // softer falloff

    fragColor = vec4(LightColorBright.rgb * intensity, 1.0);
}
