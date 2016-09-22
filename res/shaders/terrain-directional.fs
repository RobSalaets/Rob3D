#version 420
#include "terrain-lighting.fsh"

uniform DirectionalLight R_directionalLight;

vec4 CalcLightingEffect(vec3 normal, vec3 worldPos, float specCoefficient)
{
	return CalcDirectionalLight(R_directionalLight, normal, worldPos, specCoefficient);
}

#include "terrain-lightingMain.fsh"
