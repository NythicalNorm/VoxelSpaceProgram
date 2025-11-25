#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform vec3 SunDirection;
uniform float DarkAmount;

in vec2 texCoord0;
in vec3 vertPos;

out vec4 fragColor;

#define PI 3.14159265359

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a == 0.0) {
        discard;
    }

    // gets the arc length from two points on a sphere.
    // cos of acos cancels each other out here.
    float anglefromCenter = vertPos.x*SunDirection.x + vertPos.y*SunDirection.y + vertPos.z*SunDirection.z;
    fragColor = color * ColorModulator * clamp(max(0, -anglefromCenter) + DarkAmount, 0.0, 1.0);
}
