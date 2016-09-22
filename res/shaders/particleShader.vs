#version 120
attribute vec3 vertex;
attribute float null;
attribute float null2;
attribute vec4 color;
attribute vec4 position;

uniform mat4 T_MVP;
uniform vec3 C_forward;
uniform vec3 C_up;


varying vec2 texCoord0;
varying vec3 color0;

void main()
{ 
	mat3 rot = mat3(normalize(cross(C_up, C_forward)), C_up * -1, C_forward * -1);
    gl_Position = T_MVP * vec4(rot *  (vertex * color.w) + position.xyz, 1.0);
	
	
	  float x;
	  float y;
	  if(vertex.x < 0.0)
		x = 0;
	  else
		x = 1;
	  if(vertex.y < 0.0)
		y = 0;
	  else
		y = 1;
      texCoord0 = vec2(x,y); 
	  color0 = color.xyz;
}

