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



void main()
{
	vec3 directionToEye = normalize(C_eyePos - worldPos0);
	vec2 texCoords = CalcParallaxTexCoords(dispMap, tbnMatrix, directionToEye, texCoord0, dispMapScale, dispMapBias);
	
	vec3 normal = normalize(tbnMatrix * (255.0/128.0 * texture2D(normalMap, texCoords).xyz - 1));
	float specCoefficient = texture2D(normalMap, texCoords).a;
    float shadow = CalcShadowAmount(R_shadowMap, shadowMapCoords0);
	vec4 lightingAmt = CalcLightingEffect(normal, worldPos0, specCoefficient) * shadow;
	vec3 fogFactor =  vec3(clamp((length(C_eyePos  - worldPos0) - R_fogStart)/R_fogSpan, 0.0, 1.0));
	
	gl_FragColor = texture2D(diffuse, texCoords) * lightingAmt * vec4(vec3(1.0-fogFactor), 1.0) + vec4(R_fogColor,1.0) * vec4(fogFactor, 1.0);
}
