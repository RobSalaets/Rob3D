#version 120


varying vec2 texCoord0;
varying vec3 worldPos0;
varying vec4 reflectionCoord0; 
varying vec4 refractionCoord0;

uniform sampler2D R_waveBumpMap;
uniform sampler2D reflectionMap;
uniform sampler2D refractionMap;
uniform float R_time;
uniform vec3 C_eyePos;

uniform float R_bumpTilingAmount; 
uniform float R_windSpeed; 
uniform vec3 R_windDirection;
uniform float R_bumpHeight;
uniform vec3  R_waterColor;
uniform float R_waterColorBlendFactor;


void main()
{
	vec3 directionToEye = normalize(C_eyePos - worldPos0);
	vec3 reflCoord = (reflectionCoord0.xyz / reflectionCoord0.w);
	vec3 refrCoord = (refractionCoord0.xyz / refractionCoord0.w);
	vec4 bump = texture2D(R_waveBumpMap, texCoord0.xy * R_bumpTilingAmount + R_windSpeed * R_time * R_windDirection.xy) ;
	vec2 perturbedReflectionCoords = reflCoord.xy + R_bumpHeight * (bump.rg - 0.5);
	
	float fresnelTerm = 1.0 - dot(directionToEye, normalize(vec3(bump.r,bump.b,bump.g) - 0.5)) * 1.0;
	fresnelTerm = clamp(fresnelTerm, 0.0, 1.0);
	vec4 dullColor = vec4(R_waterColor, 1.0);
	
	gl_FragColor = (fresnelTerm) * texture2D(reflectionMap, perturbedReflectionCoords.xy) + ((1.0 - fresnelTerm) * texture2D(refractionMap, refrCoord.xy + R_bumpHeight * (bump.rg - 0.5)) * (1.0 - R_waterColorBlendFactor) + R_waterColorBlendFactor * dullColor);
}
