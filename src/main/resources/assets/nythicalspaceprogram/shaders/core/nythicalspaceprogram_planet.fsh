#version 150

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform vec3 SunDirection;
uniform float DarkAmount;
uniform vec4 AtmoFilterColor;

in vec2 texCoord0;
in vec3 vertPos;

out vec4 fragColor;

#define PI 3.14159265359
#define h_PI 1.57079632679
#define n 0.9362314391 // correct variable for dark amount = 0.1

void main() {
    vec4 color = texture(Sampler0, texCoord0);
    if (color.a == 0.0) {
        discard;
    }

    // gets the arc length from two points on a sphere.
    float dot = vertPos.x*SunDirection.x + vertPos.y*SunDirection.y + vertPos.z*SunDirection.z;
    float yInterceptMultiplier = (acos(DarkAmount)-PI)/h_PI;
    float anglefromCenter = cos(yInterceptMultiplier*acos(dot));

    float halfCircleBool = step(-DarkAmount, anglefromCenter);
    float fsh_brightness = (clamp(-anglefromCenter, 0.0, 1.0)*(1-halfCircleBool) + DarkAmount*halfCircleBool);
    vec4 finalColor = mix(AtmoFilterColor, color, fsh_brightness);
    finalColor.a = ColorModulator.a;
    fragColor = finalColor;
}
