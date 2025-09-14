#version 150

layout(std140) uniform LightData {
    vec4 LightScreenPosRadius;
    vec4 LightColorBright;
    vec2 ScreenSize;
    vec2 _pad;
};

out vec4 fragColor;

void main() {
    vec2 fragUV = gl_FragCoord.xy;
    float dist = length(fragUV - LightScreenPosRadius.xy);

    float radius = LightScreenPosRadius.w;
    float edge = 2.0;

    float mask = smoothstep(radius, radius - edge, dist);

    fragColor = vec4(LightColorBright.rgb * mask, 1.0);
}
