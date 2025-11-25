#version 150

in vec4 vertexColor;

uniform vec4 ColorModulator;

out vec4 fragColor;
in vec3 vertPos;

void main() {
    vec4 color = vertexColor;
    vec4 twoColor = vec4(1.0,1.0,1.0,1.0);
    if (color.a == 0.0) {
        discard;
    }

    float normalY =  vertPos.y + 0.5;
    fragColor = twoColor * normalY; //mix(color, twoColor, vertPos.y);
}
