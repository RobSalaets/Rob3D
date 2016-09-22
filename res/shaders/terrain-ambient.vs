#version 420
layout(location = 0)in vec3 position;
layout(location = 1)in vec2 texCoord;
layout(location = 2)in vec3 normal;

out vec2 texCoord0;

uniform sampler2D heightMap;
uniform float heightScale;
uniform float unitScale;
uniform float mapSize;
uniform float tiling;
uniform vec3 C_eyePos;

uniform mat4 T_MVP;

out float texPol; 

void main()
{
	//add tiling and unitScale
	float grid = 1f/normal.x * unitScale;
	float pX = position.x * unitScale + C_eyePos.x;
	float pZ = position.z * unitScale + C_eyePos.z;
	float snappedX = floor(pX / grid) * grid;
	float snappedZ = floor(pZ / grid) * grid;
	
	//normal contains 'band number'
	float detF = 1f/normal.x;
	
	float morphF = (max(abs(position.x), abs(position.z))-64*detF)/ (64f * detF);
	morphF = clamp(morphF, 0, 1);
	//if ( morphF > 0.0 ) {
	// Get position that we would have if we were on higher level grid
		grid = 2.0 * grid;
		float snappedX2 = floor(pX / grid) * grid;
		float snappedZ2 = floor(pZ / grid) * grid;

	// Linearly interpolate the two, depending on morph factor
		snappedX = mix(snappedX, snappedX2, morphF);
		snappedZ = mix(snappedZ, snappedZ2, morphF);
	//}
	
	vec2 bTexCoord = vec2((snappedX) / (mapSize * unitScale),(snappedZ) / (mapSize * unitScale));
	vec4 ter = texture2D(heightMap, bTexCoord);
    gl_Position = T_MVP * vec4(snappedX, ter.g * heightScale, snappedZ, 1.0);
	
	texPol = ter.r;
    texCoord0 = bTexCoord * tiling;
	
	//worldPos0 = (T_model * vec4(snappedX, ter.g * heightScale, snappedZ, 1.0)).xyz;
	//vec3 tan = vec3(1.0,0.0,0.0);
	//vec3 n = normalize((T_model * vec4(255.0/128.0 * texture2D(terrainNormals, bTexCoord).xzy - 1, 0.0)).xyz);
	//vec3 t = normalize((T_model * vec4(tan, 0.0)).xyz);
	//t = normalize(t - dot(t, n) * n);
	//tbnMatrix = mat3(t, cross(n,t), n);
}


