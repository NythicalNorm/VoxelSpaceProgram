#version 150

in vec4 vertexColor;

uniform vec4 nspOverlayColor;
uniform vec4 nspAtmoColor;
uniform float nspOverlayAngle;
uniform float nspAtmoAngle;

out vec4 fragColor;
in vec3 vertPos;

#define h_PI 1.57079632679

void main() {
    vec4 color = vertexColor;
    vec3 normalizedVector = normalize(vertPos);
    float normalY =  normalizedVector.y;
    float overlayBool = step(nspOverlayAngle,  normalY); // this is one if y inside the planet
    float atmoBool = step(normalY, nspOverlayAngle)*step(nspAtmoAngle, normalY);

    float sphereOutlineAngle = (normalY - nspOverlayAngle)/(1-nspOverlayAngle);
    vec4 planetOverlayColor = mix(nspAtmoColor, nspOverlayColor, clamp(sin(sphereOutlineAngle*h_PI), 0.0, 1.0)) * overlayBool;
    float atmoPercent = (normalY - nspOverlayAngle)/(nspAtmoAngle - nspOverlayAngle);

    //atmosphere alpha scales with negative of (x-1)^2
    float totalAlpha = overlayBool*nspOverlayColor.a + (clamp((atmoPercent-1)*(atmoPercent-1), 0.0, 1.0) * atmoBool);
    vec4 addedColor = planetOverlayColor + (atmoBool*nspAtmoColor);

    fragColor = vec4(addedColor.r, addedColor.g, addedColor.b, totalAlpha);
}
