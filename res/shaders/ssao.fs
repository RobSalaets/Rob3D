#version 120

varying vec4 ssTexCoord;
varying vec3 vViewRay;

varying vec3 vNormal;

uniform vec3 R_kernel[8];
uniform vec3 R_ssaoDimension;
uniform float R_ssaoRadius;
uniform float R_ssaoExposure;
uniform sampler2D R_ssaoDepth;
uniform sampler2D R_noise;

uniform mat4 T_projection;

void main()
{
	vec4 vTexcoord = ssTexCoord;
	vTexcoord.xy /= vTexcoord.w;
	vTexcoord.xy = vTexcoord.xy * 0.5 + 0.5;
	
	vec3 viewRay = vViewRay * 1.0 / vViewRay.z;
	
	vec2 uNoiseScale = vec2(R_ssaoDimension.x / 4.0 , R_ssaoDimension.y / 4.0);
	vec4 normalTexInfo = texture2D(R_ssaoDepth, vTexcoord.xy);
	vec3 origin = viewRay * normalTexInfo.r;
	
	vec3 normal = vNormal;
	normal = normalize(normal);
	
	vec3 rvec = texture2D(R_noise, vTexcoord.xy * uNoiseScale).xyz * 2.0 - 1.0;
    vec3 tangent = normalize(rvec.xyz - normal * dot(rvec.xyz, normal));
    vec3 bitangent = cross(normal, tangent);
    mat3 tbn = mat3(tangent, bitangent, normal);
	
	float occlusion = 0.0;
	for (int i = 0; i < R_kernel.length(); i++) {

// get sample position:
		vec3 sample = tbn * R_kernel[i];
		sample = sample * R_ssaoRadius + origin;
  
// project sample position:
		vec4 offset = vec4(sample, 1.0);
		offset = T_projection * offset;
		offset.xy /= offset.w;
		offset.xy = offset.xy * 0.5 + 0.5;
		
  
// get sample depth:
		float sampleDepth = texture2D(R_ssaoDepth, offset.xy).r;
  
// range check & accumulate:
		float rangeCheck= abs(origin.z - sampleDepth) < R_ssaoRadius ? 1.0 : 0.0;
		occlusion += (sampleDepth <= sample.z ? 1.0 : 0.0) * rangeCheck;
	}										
	occlusion = 1.0 - (occlusion * R_ssaoExposure/ R_kernel.length());
	
	gl_FragColor =  vec4(occlusion,occlusion,occlusion, normalTexInfo.r);
}
