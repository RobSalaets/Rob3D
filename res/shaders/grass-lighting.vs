#version 420
layout(location = 0)in vec3 vertex;
layout(location = 1)in vec2 texCoord;
layout(location = 2)in vec3 wPosition;
layout(location = 3)in vec3 normal;
layout(location = 4)in vec3 rTangent;
//layout(location = 5)in vec2 windOffset;

out vec2 texCoord0;
out vec3 normal0;

uniform mat4 T_ViewProjection;
uniform float time;

void main()
{
	vec3 n = normalize(normal);
	vec3 t = normalize(rTangent);
	mat3 rot = mat3(t, n, cross(n, t));
	gl_Position = T_ViewProjection * vec4(rot * ((vertex) * length(normal)) + vec3(1 + sin(3*time + wPosition.x / 15.0), 0,1 + sin( 3*time + wPosition.x / 15.0)) * 0.25 * vertex.y + wPosition, 1.0);
	texCoord0 = texCoord + vec2(length(rTangent)-1.0,0); 
	normal0 = normal;
}

