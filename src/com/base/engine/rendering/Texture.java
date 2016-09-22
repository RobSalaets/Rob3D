package com.base.engine.rendering;

import static org.lwjgl.opengl.GL11.GL_LINEAR_MIPMAP_LINEAR;
import static org.lwjgl.opengl.GL11.GL_NONE;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.base.engine.rendering.resourceManagement.TextureData;

public class Texture implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7408345167032929303L;
	private static HashMap<String, TextureData> resourceMap = new HashMap<String, TextureData>();
	private TextureData textureData;
	private String fileName;

	public Texture(String fileName, int textureTarget, float filter, int internalFormat, int format, boolean clamp, int attachment){
		this.fileName = fileName;
		TextureData oldResource = resourceMap.get(fileName);

		if(oldResource != null){
			textureData = oldResource;
			textureData.addReference();
		}else{
			textureData = loadTexture(fileName, textureTarget, filter, internalFormat, format, clamp, new int[]{attachment});
			resourceMap.put(fileName, textureData);
		}
	}
	

	//pixels en haslAlpha => ByteBuffer[] hack
	public Texture(int width, int height, int[] pixels, boolean hasAlpha, int textureTarget, float filter, int internalFormat, int format, boolean clamp, int attachment){
		fileName = "";
		int[] att = { attachment };
		int[] form = { format };
		int[] intform = { internalFormat };
		float[] filt = { filter };
		
		//ByteBuffer[] buffs = {data};

		textureData = new TextureData(textureTarget, width, height, 1, pixels, hasAlpha, filt, intform, form, clamp, att);
	}
	
	public Texture(int width, int height, int[] pixels, boolean hasAlpha, int textureTarget, float filter, int internalFormat, int format, boolean clamp, int attachment[]){
		fileName = "";
		int[] form = { format };
		int[] intform = { internalFormat };
		float[] filt = { filter };
		
		//ByteBuffer[] buffs = {data};

		textureData = new TextureData(textureTarget, width, height, 1, pixels, hasAlpha, filt, intform, form, clamp, attachment);
	}

	public Texture(String fileName){
		this(fileName, GL_TEXTURE_2D, GL_LINEAR_MIPMAP_LINEAR, GL_RGBA, GL_RGBA, false, GL_NONE);
	}

	@Override
	protected void finalize(){
		if(textureData.removeReference() && !fileName.isEmpty()){
			resourceMap.remove(fileName);
		}
	}

	public void bind(int samplerSlot){
		assert (samplerSlot >= 0 && samplerSlot <= 31);
		glActiveTexture(GL_TEXTURE0 + samplerSlot);
		textureData.bind(0);
	}

	public void bind(){
		bind(0);
	}

	public void bindAsRenderTarget(){
		textureData.bindAsRenderTarget();
	}

	protected static TextureData loadTexture(String fileName, int textureTarget, float filter, int internalFormat, int format, boolean clamp, int[] attachment){

		try{

			BufferedImage image = ImageIO.read(new File("./res/textures/" + fileName));
			int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());

//			ByteBuffer buffer = Util.createByteBuffer(image.getHeight() * image.getWidth() * 4);

			boolean hasAlpha = image.getColorModel().hasAlpha();

//			for(int y = 0; y < image.getHeight(); y++){
//				for(int x = 0; x < image.getWidth(); x++){
//					int pixel = pixels[y * image.getWidth() + x];
//
//					buffer.put((byte) ((pixel >> 16) & 0xFF));
//					buffer.put((byte) ((pixel >> 8) & 0xFF));
//					buffer.put((byte) ((pixel) & 0xFF));
//
//					if(hasAlpha) buffer.put((byte) ((pixel >> 24) & 0xFF));
//					else buffer.put((byte) (0xFF));
//				}
//			}
//			buffer.flip();
			int[] form = { format };
			int[] intform = { internalFormat };
			float[] filt = { filter };
//			ByteBuffer[] buffs = {buffer};
			
			TextureData res = new TextureData(textureTarget, image.getWidth(), image.getHeight(), 1, pixels, hasAlpha, filt, intform, form, clamp, attachment);

			return res;
		}catch (Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	
	private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException{
		// always perform the default de-serialization first
		aInputStream.defaultReadObject();
		TextureData oldResource = resourceMap.get(fileName);

		if(oldResource != null){
			textureData = oldResource;
			textureData.addReference();
		}else{
			resourceMap.put(fileName, textureData);
		}
	}

	public String getFileName(){
		return fileName;
	}

	public float getWidth(){
		return textureData.getWidth();
	}
	
	public float getHeight(){
		return textureData.getHeight();
	}
	
	public int[] getPixels(){
		return textureData.getPixels();
	}
}
