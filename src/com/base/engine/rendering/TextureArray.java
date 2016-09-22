package com.base.engine.rendering;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;
import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NEAREST_MIPMAP_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_BASE_LEVEL;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL;
import static org.lwjgl.opengl.GL12.glTexImage3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import com.base.engine.core.Util;

public class TextureArray{
	
	private transient IntBuffer textureArray;

	private String[] fileNames;
	private float filter;
	private int internalFormat;
	private int format;
	private boolean clamp;
	
	public TextureArray(String[] fileNames, float filter, int internalFormat, int format, boolean clamp){
		this.fileNames = fileNames;
		this.filter = filter;
		this.internalFormat = internalFormat;
		this.format = format;
		this.clamp = clamp;
		textureArray = Util.createIntBuffer(1);
		loadTextures(fileNames, filter, internalFormat, format, clamp);
	}
	
	public void bind(int samplerSlot){
		assert (samplerSlot >= 0 && samplerSlot <= 31);
		glActiveTexture(GL_TEXTURE0 + samplerSlot);
		glBindTexture(GL_TEXTURE_2D_ARRAY, textureArray.get(0));
	}
	
	private void loadTextures(String[] fileNames, float filter, int internalFormat, int format, boolean clamp){
		try{
			int width = -1;
			int height = -1;
			ByteBuffer[] textureData = new ByteBuffer[fileNames.length];
			for(int i = 0; i < fileNames.length; i++){
				BufferedImage image = ImageIO.read(new File("./res/textures/" + fileNames[i]));
				int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
	
				boolean hasAlpha = image.getColorModel().hasAlpha();
				ByteBuffer buffer = Util.createByteBuffer(image.getHeight() * image.getWidth() * 4);
				for(int y = 0; y < image.getHeight(); y++){
					for(int x = 0; x < image.getWidth(); x++){
						int pixel = pixels[y * image.getWidth() + x];

						buffer.put((byte) ((pixel >> 16) & 0xFF));
						buffer.put((byte) ((pixel >> 8) & 0xFF));
						buffer.put((byte) ((pixel) & 0xFF));

						if(hasAlpha) buffer.put((byte) ((pixel >> 24) & 0xFF));
						else buffer.put((byte) (0xFF));
						
					}
				}
				buffer.flip();
				textureData[i] = buffer;
				if(width == -1){
					width = image.getWidth();
					height = image.getHeight();
				}else{
					if(image.getHeight() != height || image.getWidth() != width){
						System.err.println("image sizes do not match");
						throw new Exception();
					}		
				}
			}
			//TextureData res = new TextureData(image.getWidth(), image.getHeight(), 1, pixels, hasAlpha, filt, intform, form, clamp, att);
			
			glGenTextures(textureArray);
			
			glBindTexture(GL_TEXTURE_2D_ARRAY, textureArray.get(0));

			glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, filter);
			glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, filter);

			if(clamp){
				glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP);
				glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP);
			}else{
				glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT);
				glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT);
			}

			//glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, internalFormat, width, height, fileNames.length, 0, format, GL_UNSIGNED_BYTE, textureData[0]);
			
//			glTexStorage3D(GL_TEXTURE_2D_ARRAY, 8, internalFormat, width, height, fileNames.length);
//			for(int i = 0; i < fileNames.length; i++){
//				glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, width, height, 1, format, GL_UNSIGNED_BYTE, textureData[i]);
//			}
			ByteBuffer totalData = Util.createByteBuffer(textureData.length * textureData[0].limit());
			for(int i = 0; i < fileNames.length; i++){
				totalData.put(textureData[i]);
			}
			totalData.flip();
			glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, internalFormat, width, height, fileNames.length, 0, format, GL_UNSIGNED_BYTE, totalData);

			
			if (filter == GL_NEAREST_MIPMAP_NEAREST ||
					filter == GL_NEAREST_MIPMAP_LINEAR ||
					filter == GL_LINEAR_MIPMAP_NEAREST ||
					filter == GL_LINEAR_MIPMAP_LINEAR)
			{
				glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_BASE_LEVEL, 0);
				glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAX_LEVEL, 8);
				glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
						//int maxAnisotropy = 0;
						//glGetFloat(GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT, maxAnisotropy);
						//System.out.println(maxAnisotropy);
				glTexParameterf(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAX_ANISOTROPY_EXT, 8.0f); //to enable anisotropic filtering, the value is the max number of pixels the graphics card may sample to approximate the shape 
			}else{
				glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_BASE_LEVEL, 0);
				glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAX_LEVEL, 0);
			}
			
			
		}catch (Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	@Override
	protected void finalize(){
		glDeleteTextures(textureArray);
	}
	
	private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException{
		// always perform the default de-serialization first
		aInputStream.defaultReadObject();
		this.textureArray = Util.createIntBuffer(0);
		loadTextures(fileNames, filter, internalFormat, format, clamp);
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
}
