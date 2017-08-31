package com.base.engine.rendering.resourceManagement;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL20.glDrawBuffers;
import static org.lwjgl.opengl.GL30.GL_DEPTH_ATTACHMENT;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER_COMPLETE;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;
import static org.lwjgl.opengl.GL30.glBindRenderbuffer;
import static org.lwjgl.opengl.GL30.glCheckFramebufferStatus;
import static org.lwjgl.opengl.GL30.glFramebufferRenderbuffer;
import static org.lwjgl.opengl.GL30.glFramebufferTexture2D;
import static org.lwjgl.opengl.GL30.glGenFramebuffers;
import static org.lwjgl.opengl.GL30.glGenRenderbuffers;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL30.glRenderbufferStorage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import com.base.engine.core.Util;

public class TextureData extends ReferenceCounter implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4131272544399938591L;
	private transient IntBuffer textures;// IDs
	
	private float[] filters;
	private int[] internalFormat;
	private int[] format;
	private boolean clamp;
	private int[] attachments;
	private int[] pixels;
	private boolean hasAlpha;
	
	private int textureTarget; // TEXTURE_2D
	private int frameBuffer;
	private int renderBuffer;
	private int numTextures;
	private int width;
	private int height;

	//pixels en haslAlpha => ByteBuffer[] hack
	public TextureData(int textureTarget, int width, int height, int numTextures, int[] pixels, boolean hasAlpha, float filters[], int internalFormat[], int format[], boolean clamp, int attachments[]){
		this.textures = Util.createIntBuffer(numTextures);
		this.textureTarget = textureTarget;
		this.numTextures = numTextures;
		this.width = width;
		this.height = height;
		this.frameBuffer = 0;
		this.renderBuffer = 0;
		this.filters = filters;
		this.internalFormat = internalFormat;
		this.format = format;
		this.attachments = attachments;
		this.pixels = pixels;
		this.hasAlpha = hasAlpha;

		ByteBuffer buffer = Util.createByteBuffer(height * width * 4);
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int pixel = pixels[y * width + x];

				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) ((pixel) & 0xFF));

				if(hasAlpha) buffer.put((byte) ((pixel >> 24) & 0xFF));
				else buffer.put((byte) (0xFF));
			}
		}
		buffer.flip();
		ByteBuffer[] buffs = {buffer};
		
		initTextures(buffs, filters, internalFormat, format, clamp);
		initRenderTargets(attachments);
	}
	
	public TextureData(int textureTarget, int width, int height, TextureInitializer init){
		this.textureTarget = textureTarget;
		this.width = width;
		this.height = height;
		this.textures = Util.createIntBuffer(1);
		glGenTextures(textures);
		init.initTexture(textureTarget, width, height, textures.get(0));
	}

	@Override
	protected void finalize(){

		glDeleteTextures(textures);
	}

	public void bind(int textureNum){
		glBindTexture(textureTarget, textures.get(textureNum));
	}

	public void bindAsRenderTarget(){
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
		glViewport(0, 0, width, height);
	}

	private void initTextures(ByteBuffer[] data, float[] filters, int[] internalFormat, int format[], boolean clamp){

		glGenTextures(textures);
		for(int i = 0; i < numTextures; i++){
			glBindTexture(textureTarget, textures.get(i));

			glTexParameterf(textureTarget, GL_TEXTURE_MIN_FILTER, filters[i]);
			glTexParameterf(textureTarget, GL_TEXTURE_MAG_FILTER, filters[i]);

			if(clamp){
				glTexParameterf(textureTarget, GL_TEXTURE_WRAP_S, GL_CLAMP);
				glTexParameterf(textureTarget, GL_TEXTURE_WRAP_T, GL_CLAMP);
			}else{
				glTexParameteri(textureTarget, GL_TEXTURE_WRAP_S, GL_REPEAT);
				glTexParameteri(textureTarget, GL_TEXTURE_WRAP_T, GL_REPEAT);
			}

			glTexImage2D(textureTarget, 0, internalFormat[i], width, height, 0, format[i], GL_UNSIGNED_BYTE, data[i]);
			
			if (filters[i] == GL_NEAREST_MIPMAP_NEAREST ||
					filters[i] == GL_NEAREST_MIPMAP_LINEAR ||
					filters[i] == GL_LINEAR_MIPMAP_NEAREST ||
					filters[i] == GL_LINEAR_MIPMAP_LINEAR)
				{
					glGenerateMipmap(textureTarget);
					//int maxAnisotropy = 0;
					//glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy);
					//System.out.println(maxAnisotropy);
					glTexParameterf(textureTarget, GL_TEXTURE_MAX_ANISOTROPY_EXT, 8.0f); //to enable anisotropic filtering, the value is the max number of pixels the graphics card may sample to approximate the shape 
				}
				else
				{
					glTexParameteri(textureTarget, GL_TEXTURE_BASE_LEVEL, 0);
					glTexParameteri(textureTarget, GL_TEXTURE_MAX_LEVEL, 0);
				}
		}
	}

	private void initRenderTargets(int[] attachments){
		if(attachments.length == 0) return;

		IntBuffer drawBuffers = Util.createIntBuffer(32); // 32 is the max
															// number of bound
															// textures in
															// OpenGL
		assert (numTextures <= 32); // Assert to be sure no buffer overrun
									// should occur

		boolean hasDepth = false;
		for(int i = 0; i < numTextures; i++){
			if(attachments[i] == GL_DEPTH_ATTACHMENT){
				drawBuffers.put(i, GL_NONE);
				hasDepth = true;
			}else drawBuffers.put(i, attachments[i]);

			if(attachments[i] == GL_NONE) continue;

			if(frameBuffer == 0){
				frameBuffer = glGenFramebuffers();
				glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer);
			}

			glFramebufferTexture2D(GL_FRAMEBUFFER, attachments[i], textureTarget, textures.get(i), 0);
		}

		if(frameBuffer == 0) return;

		if(!hasDepth){
			renderBuffer = glGenRenderbuffers();
			glBindRenderbuffer(GL_RENDERBUFFER, renderBuffer);
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
			glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBuffer);
		}

		glDrawBuffers(drawBuffers);

		if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE){
			System.err.println("Framebuffer creation failed!");
			assert (false);
		}

		glBindFramebuffer(GL_FRAMEBUFFER, 0);
	}
	
	public int getWidth(){
		return this.width;
	}
	
	public int getHeight(){
		return this.height;
	}

	private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException{
		// always perform the default de-serialization first
		aInputStream.defaultReadObject();
		this.textures = Util.createIntBuffer(numTextures);
		
		ByteBuffer buffer = Util.createByteBuffer(height * width * 4);
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				int pixel = pixels[y * width + x];

				buffer.put((byte) ((pixel >> 16) & 0xFF));
				buffer.put((byte) ((pixel >> 8) & 0xFF));
				buffer.put((byte) ((pixel) & 0xFF));

				if(hasAlpha) buffer.put((byte) ((pixel >> 24) & 0xFF));
				else buffer.put((byte) (0xFF));
			}
		}
		buffer.flip();
		ByteBuffer[] buffs = {buffer};
		initTextures(buffs, filters, internalFormat, format, clamp);
		initRenderTargets(attachments);
	}

	/**
	 * This is the default implementation of writeObject. Customise if
	 * necessary.
	 */
	private void writeObject(ObjectOutputStream aOutputStream) throws IOException{
		// perform the default serialization for all non-transient, non-static
		// fields
		aOutputStream.defaultWriteObject();

	}
	
	public int[] getPixels(){
		return pixels;
	}

}
