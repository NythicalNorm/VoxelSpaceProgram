#version 150

in vec4 vertexColor;

uniform vec4 BottomColor;
uniform vec4 TopColor;
uniform float TransitionPoint;
uniform float Opacity;

out vec4 fragColor;
in vec3 vertPos;

void main() {
    vec4 color = vertexColor;
    vec3 normalizedVector = normalize(vertPos);
    float normalY =  normalizedVector.y + 0.5;

    fragColor = mix (vertexColor, mix(BottomColor, TopColor, smoothstep(TransitionPoint - 0.0135, 1.0, normalY)), Opacity);
}
