#include "sampling.glh"

bool inRange(float value)
{
	return value >= 0.0 && value <= 1.0;
}

float CalcShadowAmount(sampler2D shadowMap, vec4 initialshadowMapCoords)
{
	vec3 shadowMapCoords = (initialshadowMapCoords.xyz / initialshadowMapCoords.w);
	
	if(inRange(shadowMapCoords.z) && inRange(shadowMapCoords.x) && inRange(shadowMapCoords.y)){
		return SampleVarianceShadowMap(shadowMap, shadowMapCoords.xy, shadowMapCoords.z, R_shadowVarianceMin, R_shadowLightBleedReduction);	
	}else{
		return 1.0;
	}
}

vec3 toneMap(vec3 x)
{
    float A = 0.15;
	float B = 0.50;
	float C = 0.10;
	float D = 0.20;
	float E = 0.02;
	float F = 0.30;

    return ((x*(A*x+C*B)+D*E)/(x*(A*x+B)+D*F))-E/F;
}

void main()
{
	vec3 normal = normalize(tbnMatrix * (255.0/128.0 * texture2D(normalMap, texCoord0).xyz - 1));
	float specCoefficient = texture2D(normalMap, texCoord0).a;
    float shadow = CalcShadowAmount(R_shadowMap, shadowMapCoords0);
	vec4 lightingAmt = CalcLightingEffect(normal, worldPos0, specCoefficient) * shadow;
	vec3 fogFactor =  vec3(clamp((length(C_eyePos  - worldPos0) - R_fogStart)/R_fogSpan, 0.0, 1.0));
	float texIndex = clamp(texPol * numTextures, 0, numTextures - 1.0);
	vec3 diffuseSum = texture2DArray(diffuseTextures, vec3(texCoord0, floor(texIndex))).xyz * (1.0 - fract(texIndex)) + texture2DArray(diffuseTextures, vec3(texCoord0, ceil(texIndex))).xyz * fract(texIndex);
	
	
	outColor = vec4(diffuseSum, 1.0) * lightingAmt * vec4(vec3(1.0-fogFactor), 1.0) + vec4(R_fogColor,1.0) * vec4(fogFactor, 1.0);
}
