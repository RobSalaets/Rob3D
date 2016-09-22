#version 420
layout(location = 0)in vec3 position;
layout(location = 1)in vec2 texCoord;
layout(location = 2)in vec3 normal;
layout(location = 3)in vec3 tangent;
layout(location = 4)in vec4 indices;
layout(location = 5)in vec4 weights;

out vec2 texCoord0;
out vec3 worldPos0;
out mat3 tbnMatrix;

uniform mat4 T_model;
uniform mat4 T_MVP;
uniform mat4 animationJoints[16];


void main()
{
		vec4 pos = animationJoints[ int(indices.x)] * vec4(position, 1.0) * weights.x + animationJoints[ int(indices.y)] * vec4(position, 1.0) * weights.y + animationJoints[ int(indices.z)] * vec4(position, 1.0) * weights.z + animationJoints[ int(indices.w)] * vec4(position, 1.0) * weights.w;
		gl_Position = T_MVP * pos;
		texCoord0 = texCoord; 
		worldPos0 = (T_model * pos).xyz;
		tbnMatrix = mat3(tangent, cross(tangent, normal), normal);
	
}

