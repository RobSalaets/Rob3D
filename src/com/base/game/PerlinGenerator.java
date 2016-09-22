package com.base.game;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import com.base.engine.core.Vector3f;
import com.base.engine.physics.HeightMapCollider;

public class PerlinGenerator{

	private int perturbs[];
	private double gradVecs[];
	private double gradVecs4d[];
	private int size;

	private Random rand;

	public PerlinGenerator(int width, int seed){
		this.size = width;
		rand = new Random(seed);

		/** Calc perturbationIndices */
		perturbs = new int[256];
		ArrayList<Integer> numbsTo256 = new ArrayList<Integer>();
		for(int i = 0; i < 256; i++)
			numbsTo256.add(i);
		for(int i = 0; i < 256; i++){
			int randIndex = rand.nextInt(numbsTo256.size());
			perturbs[i] = numbsTo256.get(randIndex);
			numbsTo256.remove(randIndex);
		}

		/** Calc gradients */
		gradVecs = new double[2 * 256];

		for(int i = 0; i < 256; i++){

			double xGrad;
			double yGrad;
			do{
				xGrad = rand.nextDouble();
				yGrad = rand.nextDouble();
				xGrad = xGrad * 2 - 1;
				yGrad = yGrad * 2 - 1;
			}while((xGrad * xGrad + yGrad * yGrad) < 1);
			// normalize
			double length = (double) Math.sqrt(xGrad * xGrad + yGrad * yGrad);
			xGrad /= length;
			yGrad /= length;

			gradVecs[i * 2 + 0] = xGrad;
			gradVecs[i * 2 + 1] = yGrad;
		}
		
		gradVecs4d = new double[4 * 256];

		for(int i = 0; i < 256; i++){

			double xGrad;
			double yGrad;
			double zGrad;
			double wGrad;
			do{
				xGrad = rand.nextDouble();
				yGrad = rand.nextDouble();
				xGrad = xGrad * 2 - 1;
				yGrad = yGrad * 2 - 1;
				zGrad = rand.nextDouble();
				wGrad = rand.nextDouble();
				zGrad = zGrad * 2 - 1;
				wGrad = wGrad * 2 - 1;
			}while((xGrad * xGrad + yGrad * yGrad + zGrad * zGrad + wGrad * wGrad) < 1);
			// normalize
			double length = (double) Math.sqrt(xGrad * xGrad + yGrad * yGrad + zGrad * zGrad + wGrad * wGrad);
			xGrad /= length;
			yGrad /= length;
			zGrad /= length;
			wGrad /= length;

			gradVecs4d[i * 4 + 0] = xGrad;
			gradVecs4d[i * 4 + 1] = yGrad;
			gradVecs4d[i * 4 + 2] = zGrad;
			gradVecs4d[i * 4 + 3] = wGrad;
		}
	}

	public float[] noise(int pixelsPerCell, float amplitude, float[] noise){
		if(noise.length != size * size){
			System.err.println("Invalid noise array");
			System.exit(1);
		}
		for(double px = 0; px < size; px++){
			for(double py = 0; py < size; py++){
				double x = px / (double) pixelsPerCell;
				double y = py / (double) pixelsPerCell;
				int x0 = (int) x;
				int y0 = (int) y;
				int x1 = x0 + 1;
				int y1 = y0 + 1;

				int i = x0;
				int j = y0;
				double[] grad0 = new double[] { gradVecs[perturbs[(perturbs[i & 0xFF] + j) & 0xFF] * 2 + 0], gradVecs[perturbs[(perturbs[i & 0xFF] + j) & 0xFF] * 2 + 1] };
				double dot0 = grad0[0] * (x - i) + grad0[1] * (y - j);
				i = x0;
				j = y1;
				double[] grad1 = new double[] { gradVecs[perturbs[(perturbs[i & 0xFF] + j) & 0xFF] * 2 + 0], gradVecs[perturbs[(perturbs[i & 0xFF] + j) & 0xFF] * 2 + 1] };
				double dot1 = grad1[0] * (x - i) + grad1[1] * (y - j);
				double pol1 = interpol(dot0, dot1, y - y0);

				i = x1;
				j = y1;
				double[] grad2 = new double[] { gradVecs[perturbs[(perturbs[i & 0xFF] + j) & 0xFF] * 2 + 0], gradVecs[perturbs[(perturbs[i & 0xFF] + j) & 0xFF] * 2 + 1] };
				double dot2 = grad2[0] * (x - i) + grad2[1] * (y - j);

				i = x1;
				j = y0;
				double[] grad3 = new double[] { gradVecs[perturbs[(perturbs[i & 0xFF] + j) & 0xFF] * 2 + 0], gradVecs[perturbs[(perturbs[i & 0xFF] + j) & 0xFF] * 2 + 1] };
				double dot3 = grad3[0] * (x - i) + grad3[1] * (y - j);
				double pol2 = interpol(dot3, dot2, y - y0);

				double pol3 = interpol(pol1, pol2, x - x0);
				noise[(int) (px + py * size)] = (float) (noise[(int) (px + py * size)] + pol3 * amplitude);
			}
		}
		return noise;
	}
	
	public float[] noise4DToTileable(int pixelsPerCell, float radiusFactor, float amplitude, float[] noise){
		if(noise.length != size * size){
			System.err.println("Invalid noise array");
			System.exit(1);
		}
		for(double px = 0; px < size; px++){
			for(double py = 0; py < size; py++){
				double radius = size / 2;
				double x = (radiusFactor * radius + radiusFactor * radius * Math.cos(2.0 * Math.PI / size * px)) / (double) pixelsPerCell;
				double y = (radiusFactor * radius + radiusFactor * radius * Math.sin(2.0 * Math.PI / size * px)) / (double) pixelsPerCell;
				
				double z = (radiusFactor * radius + radiusFactor * radius * Math.cos(2.0 * Math.PI / size * py)) / (double) pixelsPerCell;
				double w = (radiusFactor * radius + radiusFactor * radius * Math.sin(2.0 * Math.PI / size * py)) / (double) pixelsPerCell;
				double value = getNoise4DAt(x, y, z, w) * amplitude;
				noise[(int) (px + py * size)] = (float) (noise[(int) (px + py * size)] + value);		
			}		
		}
		return noise;
	}
	
	private double getNoise4DAt(double x, double y, double z, double w){
		
		int x0 = (int) x;
		int y0 = (int) y;
		int z0 = (int) z;
		int w0 = (int) w;
		int x1 = x0 + 1;
		int y1 = y0 + 1;
		int z1 = z0 + 1;
		int w1 = w0 + 1;//   0  1  2  3  4  5  6  7  8  9  10 11 12 13 14 15
		int[] ia = new int[]{x0,x1,x1,x0,x0,x1,x1,x0,x0,x1,x1,x0,x0,x1,x1,x0};
		int[] ja = new int[]{y0,y0,y1,y1,y0,y0,y1,y1,y0,y0,y1,y1,y0,y0,y1,y1};
		int[] ka = new int[]{z0,z0,z0,z0,z1,z1,z1,z1,z0,z0,z0,z0,z1,z1,z1,z1};
		int[] la = new int[]{w0,w0,w0,w0,w0,w0,w0,w0,w1,w1,w1,w1,w1,w1,w1,w1};
		
		double[] dots = new double[16];
		
		for(int index = 0; index < 16; index++){
			int i = ia[index];
			int j = ja[index];
			int k = ka[index];
			int l = la[index];
			double[] grad = new double[]{ gradVecs4d[perturbs[(perturbs[(perturbs[(perturbs[i & 0xFF] + j) & 0xFF] + k) & 0xFF] + l) & 0xFF] * 2 + 0],
									   gradVecs4d[perturbs[(perturbs[(perturbs[(perturbs[i & 0xFF] + j) & 0xFF] + k) & 0xFF] + l) & 0xFF] * 2 + 1],
									   gradVecs4d[perturbs[(perturbs[(perturbs[(perturbs[i & 0xFF] + j) & 0xFF] + k) & 0xFF] + l) & 0xFF] * 2 + 2],
									   gradVecs4d[perturbs[(perturbs[(perturbs[(perturbs[i & 0xFF] + j) & 0xFF] + k) & 0xFF] + l) & 0xFF] * 2 + 3]
			};
			dots[index] = grad[0] * (x - i) + grad[1] * (y - j) + grad[2] * (z - k) + grad[3] * (w - l);
		}
		double inter01 = interpol(dots[0], dots[1], x - x0);
		double inter23 = interpol(dots[3], dots[2], x - x0);
		double inter0123 = interpol(inter01, inter23, y - y0);
		double inter45 = interpol(dots[4], dots[5], x - x0);
		double inter67 = interpol(dots[7], dots[6], x - x0);
		double inter4567 = interpol(inter45, inter67, y - y0);
		double inter01234567 = interpol(inter0123, inter4567, z - z0);
		
		double inter89 = interpol(dots[8], dots[9], x - x0);
		double inter1011 = interpol(dots[11], dots[10], x - x0);
		double inter891011 = interpol(inter89, inter1011, y - y0);
		double inter1213 = interpol(dots[12], dots[13], x - x0);
		double inter1415 = interpol(dots[15], dots[14], x - x0);
		double inter12131415 = interpol(inter1213, inter1415, y - y0);
		double inter89101112131415 = interpol(inter891011, inter12131415, z - z0);
		return interpol(inter01234567, inter89101112131415, w - w0);
	}

	public float[] smoothen(float[] heightMap){
		for(int i = 0; i < size; ++i){
			for(int j = 0; j < size; ++j){
				float total = 0.0f;
				for(int u = -1; u <= 1; u++){
					for(int v = -1; v <= 1; v++){
						int x = (i + u) % size;
						x = x < 0 ? size + x : x;
						int y = (j + v) % size;
						y = y < 0 ? size + y : y;
						total += heightMap[x + y * size];
					}
				}
				heightMap[i + j * size] = total / 9.0f;
			}
		}
		return heightMap;
	}

	public float[] erode(float[] heightMap, int iterations, float tallus){
		for(int iter = 0; iter < iterations; iter++){
			for(int i = 0; i < size ; ++i){
				for(int j = 0; j < size ; ++j){
					float d_max = 0;
					byte[] match = new byte[2];
					for(int u = -1; u <= 1; u++){
						for(int v = -1; v <= 1; v++){
							int x = (i + u) % size;
							x = x < 0 ? size + x : x;
							int y = (j + v) % size;
							y = y < 0 ? size + y : y;
							float d_i = heightMap[i + j * size] - heightMap[x + (y) * size];
							if(d_i > d_max){
								d_max = d_i;
								match[0] = (byte) u;
								match[1] = (byte) v;
							}
						}
					}
					if(0 < d_max && d_max <= tallus){
						float d_h = 0.5f * d_max;
						heightMap[i + j * size] -= d_h;
						int x = (i + match[0]) % size;
						x = x < 0 ? size + x : x;
						int y = (j + match[1]) % size;
						y = y < 0 ? size + y : y;
						heightMap[x + (y) * size] += d_h;
					}
				}
			}
		}
		return heightMap;
	}

	public float[] perturb(float[] heightMap, float pFactor, int pNoise){

		float[] perturbNoiseX = this.noise(pNoise, 1, new float[size * size]);
		float[] perturbNoiseY = this.noise(pNoise, 1, new float[size * size]);
		float[] temp = new float[size * size];
		int u, v;
		for(int i = 0; i < size; ++i){
			for(int j = 0; j < size; ++j){
				u = (int) (i + perturbNoiseX[i + j * size] * pFactor * (float)size);
				v = (int) (j + perturbNoiseY[i + j * size] * pFactor * (float)size);
				u = u > size -1 ? size -1 : u;
				u = u < 0? 0 : u;
				v = v > size -1 ? size -1 : v;
				v = v < 0? 0 : v;
				temp[i + j * size] = heightMap[u + v * size];
			}
		}
		heightMap = temp;
		return heightMap;
	}

	private double interpol(double a0, double a1, double w){
		return a0 + func(w) * (a1 - a0);
	}

	private double func(double f){
		return 3 * f * f - 2 * f * f * f;
	}
	
	public String createImageFile(float[] heightMap, boolean forTerrain){
		int[] pixels = new int[heightMap.length];
		for(int i = 0; i < heightMap.length; i++){
			byte c = (byte) ((byte)((heightMap[i]/2f + .5f) * 255) & 0xFF);
			byte b = c;
			if(forTerrain){
				float v = heightMap[i]/2f + .5f;
				b = (byte) ((byte)((v*v*v)* 255) & 0xFF);
//				float yd, xd;
//				if(i == 0 || i + 1 >= heightMap.length)
//					yd = 0;
//				else
//					yd = heightMap[i + 1] - heightMap[i - 1];
//				if(i - size < 0 || i + size >= heightMap.length)
//					xd = 0;
//				else
//					xd = heightMap[i + size] - heightMap[i - size];
//				b = (byte) (Math.floor(8000 * Math.max(Math.abs(xd),Math.abs(yd))/ 64f)*64f);
			}
			pixels[i] = (int)((b&0x0ff)<<16)|((c&0x0ff)<<8)|((0xFF)<<24)|(c&0x0ff);
		}
		BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		result.setRGB(0, 0, size, size, pixels, 0, size);
		
		try{
			String file = "noise" + System.currentTimeMillis() / 1000 + ".png";
			ImageIO.write(result, "png", new File("./res/textures/" + file));
			System.out.println(file);
			return file;
		}catch (IOException e){
			e.printStackTrace();
		}
		return "";
	}
	
	public String createNormalMap(float[] heightMap, float unitScale, float heightScale){
		ArrayList<ArrayList<Vector3f>> normals = new ArrayList<>();
		for(int i = 0; i < heightMap.length; i++){
			normals.add(new ArrayList<Vector3f>());
		}
		
		for(int i = 0; i < heightMap.length; i++){
			if((i) % (size) != size - 1 && i / size != size - 1){
				Vector3f v1 = new Vector3f((i % size) * unitScale, heightMap[i+size] * heightScale, (i / size + 1) * unitScale).sub(new Vector3f((i % size) * unitScale, heightMap[i] * heightScale, i / size * unitScale));
				Vector3f v2 = new Vector3f((i % size + 1) * unitScale, heightMap[i+1] * heightScale, (i / size) * unitScale).sub(new Vector3f((i % size) * unitScale, heightMap[i] * heightScale, i / size * unitScale));
				Vector3f normal = v1.cross(v2).normalized();
				normals.get(i).add(normal);
				normals.get(i + size).add(normal);
				normals.get(i + size + 1).add(normal);
				Vector3f v3 = new Vector3f((i % size + 1) * unitScale, heightMap[i + size + 1] * heightScale, (i / size + 1) * unitScale).sub(new Vector3f((i % size+1) * unitScale, heightMap[i+1] * heightScale, i / size * unitScale));
				Vector3f v4 = new Vector3f((i % size) * unitScale, heightMap[i] * heightScale, i / size * unitScale).sub(new Vector3f((i % size+1) * unitScale, heightMap[i+1] * heightScale, i / size * unitScale));
				Vector3f normal2 = v4.cross(v3).normalized();
				normals.get(i).add(normal2);
				normals.get(i + size + 1).add(normal2);
				normals.get(i + 1).add(normal2);
				if(normal.getY() < 0 || normal2.getY() < 0){
					System.err.println("normals down");
					normal.print();
					normal2.print();
					System.exit(1);
				}
			}
		}
		Vector3f[] actualNormals = new Vector3f[heightMap.length];
		for(int i = 0; i < normals.size(); i++){
			Vector3f avg = Vector3f.zeroVector;
			for(int j = 0; j < normals.get(i).size(); j++){
				avg = avg.add(normals.get(i).get(j));
			}
			actualNormals[i] = avg.normalized();
			
		}
		int[] pixels = new int[actualNormals.length];
		for(int i = 0; i < actualNormals.length; i++){
			byte r = (byte) ((byte) ((actualNormals[i].getX()/2 + .5f)*255) & 0xFF);
			byte g = (byte) ((byte) ((actualNormals[i].getZ()/2 + .5f)*255) & 0xFF);
			byte b = (byte) ((byte) ((actualNormals[i].getY()/2 + .5f)*255) & 0xFF);
//			System.out.println(r + " : "+ g + " : " + b);
//			actualNormals[i].print();
//			System.out.println(Integer.toHexString(pixels[i]));
			pixels[i] = ((r&0xFF)<<16)|((g&0xFF)<<8)|((0xFF)<<24)|(b&0xFF);
		}
		BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		result.setRGB(0, 0, size, size, pixels, 0, size);
		
		try{
			String file = "normal" + System.currentTimeMillis() / 1000 + ".png";
			ImageIO.write(result, "png", new File("./res/textures/" + file));
			System.out.println(file);
			return file;
		}catch (IOException e){
			e.printStackTrace();
		}
		return "";
	}
}
