attribute vec3 position;
attribute vec2 texCoord;
attribute vec3 normal;
attribute vec3 tangent;

varying vec2 texCoord0;
varying vec3 worldPos0;
varying vec4 shadowMapCoords0;
varying mat3 tbnMatrix;

uniform mat4 T_model;
uniform mat4 T_MVP;
uniform mat4 R_lightMatrix;

void main()
{
    gl_Position = T_MVP * vec4(position, 1.0);
    texCoord0 = texCoord; 
    shadowMapCoords0 = R_lightMatrix * vec4(position, 1.0);
    worldPos0 = (T_model * vec4(position, 1.0)).xyz;
    
//    vec3 n = normalize((T_model * vec4(normal, 0.0)).xyz);
 //   vec3 t = normalize((T_model * vec4(tangent, 0.0)).xyz);
 //   t = normalize(t - dot(t, n) * n);
 //   
 //   vec3 biTangent = cross(t, n);
//    tbnMatrix = mat3(t, biTangent, n);

	vec3 tangent = vec3(0.0,0.0,0.0);
	vec3 v1 = cross(normal, vec3(0.0,0.0,-1.0));
	vec3 v2 = cross(normal, vec3(0.0,-1.0,0.0));
	if(length(v1) > length(v2))
		tangent = v1;
	else
		tangent = v2;
	vec3 n = normalize((T_model * vec4(normal, 0.0)).xyz);
	vec3 t = normalize((T_model * vec4(tangent, 0.0)).xyz);
	t = normalize(t - dot(t, n) * n);
	vec3 b=cross(n,t);
	tbnMatrix = mat3(t, b, n);
}