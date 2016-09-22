#version 120

varying vec2 texCoord0;
uniform sampler2D R_filterTexture;
uniform sampler2D R_additive;

void main()
{
	gl_FragColor = texture2D(R_filterTexture, texCoord0) * texture2D(R_additive, texCoord0);
}
