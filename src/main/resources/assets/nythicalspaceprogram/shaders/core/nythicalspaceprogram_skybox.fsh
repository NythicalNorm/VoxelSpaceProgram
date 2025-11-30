#version 150

in vec4 vertexColor;

uniform vec4 nspBottomColor;
uniform vec4 nspTopColor;
uniform float nspTransitionPoint;
uniform float nspOpacity;

out vec4 fragColor;
in vec3 vertPos;

void main() {
    vec4 color = vertexColor;
    vec3 normalizedVector = normalize(vertPos);
    float normalY =  normalizedVector.y + 0.5;
    vec4 newColor =  mix(nspBottomColor, nspTopColor, smoothstep(nspTransitionPoint - 0.0135, nspTransitionPoint, normalY));
    fragColor = newColor;
}
