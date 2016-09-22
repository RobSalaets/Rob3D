#version 130
varying vec2 texCoord0;
uniform sampler2D diffuse;
uniform vec3 R_ambient;

void main()
{
	gl_FragColor =  texture2D(diffuse, texCoord0) * vec4(R_ambient, 1.0);
}
