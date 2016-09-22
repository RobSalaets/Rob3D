#version 120
#include "lighting.glh"

varying vec2 texCoord0;
varying mat3 tbnMatrix;
varying vec3 worldPos0;

uniform sampler2D diffuse;
uniform sampler2D normalMap;
uniform SpotLight R_spotLight;

void main()
{
	vec3 normal = normalize(tbnMatrix * (255.0/120.0 * texture2D(normalMap, texCoord0.xy).xyz - 1));
    gl_FragColor = texture2D(diffuse, texCoord0.xy) * calcSpotLight(R_spotLight, normal, worldPos0);
}

