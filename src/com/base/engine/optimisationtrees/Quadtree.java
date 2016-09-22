package com.base.engine.optimisationtrees;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.base.engine.core.Vector2f;

public class Quadtree{
	
	private static final Vector2f[] diagDirectionVectors = new Vector2f[]{new Vector2f(0.5f,0.5f),new Vector2f(0.5f,-0.5f),new Vector2f(-0.5f,-0.5f),new Vector2f(-0.5f,0.5f)};

	public static int checks;
	
	private QuadNode root;
	private ArrayList<QuadTreeQuery> qtqs;
	private int maxPerCell;
	private Vector2f center;
	private float range;
	private float maxQueryDepth;
	private float halfRange;
	
	
	public Quadtree(Vector2f center, float range, int maxPerCell){
		this.root = new QuadNode(null, center, range/2f, maxPerCell);
		this.qtqs = new ArrayList<QuadTreeQuery>();
		this.maxPerCell = maxPerCell;
		this.center = center;
		this.range = range;
		this.halfRange = range/2f;
		
		checks = 0;
	}
	
	public void addPoint(Vector2f point, Object data){
		if(Math.abs(point.getX() - center.getX()) > halfRange || Math.abs(point.getY() - center.getY()) > halfRange){
			System.err.println("Quadtree error: input point is out of bounds");
			throw new IllegalArgumentException();
		}
		findSpot(root, point, data);
	}
	
	public void addPoints(Vector2f[] points, Object[] data){
		if(points.length != data.length){
			System.err.println("Quadtree error: array lengths do not match");
			throw new IllegalArgumentException();
		}
		for(int i = 0; i < points.length; i++)
			addPoint(points[i], data[i]);
	}
	
	public void addPoints(ArrayList<Vector2f> points, ArrayList<Object> data){
		if(points.size() != data.size()){
			System.err.println("Quadtree error: list sizes do not match");
			throw new IllegalArgumentException();
		}
		for(int i = 0; i < points.size(); i++)
			addPoint(points.get(i), data.get(i));
	}
	
	private void findSpot(QuadNode node, Vector2f point, Object data){
		int quadrant = -1;
		if(point.getX() > node.center.getX()){
			if(point.getY() > node.center.getY())
				quadrant = 0;
			else
				quadrant = 1;
		}else{
			if(point.getY() > node.center.getY())
				quadrant = 3;
			else
				quadrant = 2;
		}
		if((node.isLeaf && node.occupants < maxPerCell) ){
			node.data[node.occupants] = data;
			node.points[node.occupants] = point;
			node.occupants++;
		}else if(node.isLeaf){
			//case overcrowded
			int nbOccupants = node.occupants;
			for(int i = 0; i < 4; i++)
				node.children[i] = new QuadNode(node, node.center.add(diagDirectionVectors[i].mul(node.halfRange)), node.halfRange/2f, maxPerCell);
			node.isLeaf = false;
			node.occupants = 0;
			findSpot(node.children[quadrant], point, data);
			for(int i = 0; i < nbOccupants; i++){
				findSpot(node, node.points[i], node.data[i]);
			}
		}else{
			//non-leaf
			findSpot(node.children[quadrant], point, data);
		}
	}
	
	public void addQuadTreeQuery(QuadTreeQuery query){
		qtqs.add(query);
	}
	
	/**
	 * Query gets called on the nodes in the quadtree. Query returns:
	 * Null: node and its childrens data is not included in results
	 * non-null and reached maxquerydepth: all data of leaf-children added to results
	 * non-null and leaf: data added to results
	 * */
	public ArrayList<Object> getQueryResults(int queryIndex, float maxQueryDepth, Object o){
		this.maxQueryDepth = maxQueryDepth;
		return recursiveQueryCheck(root, o,qtqs.get(queryIndex), new ArrayList<Object>());
	}
	
	private ArrayList<Object> recursiveQueryCheck(QuadNode node, Object o, QuadTreeQuery query, ArrayList<Object> results){
		
		Object checkResult = query.check(node, o);
		if(checkResult == null){
			
		}else if(node.isLeaf){
			if(node.occupants > 0)
				results.add(checkResult);
		}else if(node.halfRange <= maxQueryDepth){
			node.addAllQueryResults(results, query);
		}else{
			recursiveQueryCheck(node.children[0], o, query, results);
			recursiveQueryCheck(node.children[1], o, query, results);
			recursiveQueryCheck(node.children[2], o, query, results);
			recursiveQueryCheck(node.children[3], o, query, results);
		}
		
		return results;
	}
	
	public void treeToPNG(){
		
		int[] pixels = new int[(int) (range * range)];
		for(int i = 0; i < pixels.length; i++){
			pixels[i] = (int)((0x0ff)<<16)|((0x0ff)<<8)|((0xFF)<<24)|(0x0ff);
		}
		
		recursiveDraw(root, pixels);
		BufferedImage result = new BufferedImage((int)range, (int)range, BufferedImage.TYPE_INT_ARGB);
		result.setRGB(0, 0, (int)range, (int)range, pixels, 0, (int)range);
		
		try{
			String file = "quadtree" + System.currentTimeMillis() / 1000 + ".png";
			ImageIO.write(result, "png", new File("./res/textures/" + file));
			System.out.println(file);
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private void recursiveDraw(QuadNode node, int[] pixels){
		
		pixels[((int)node.center.getX()  + (int)(node.center.getY()) * (int)range) % pixels.length] = (int)((0x044)<<16)|((0x044)<<8)|((0xFF)<<24)|(0x044);
		for(int i = 1; i < node.halfRange; i++){
			pixels[((int)node.center.getX() + i + (int)(node.center.getY()) * (int)range) % pixels.length] = (int)((0x044)<<16)|((0x044)<<8)|((0xFF)<<24)|(0x044);
			pixels[((int)node.center.getX() - i + (int)(node.center.getY()) * (int)range) % pixels.length] = (int)((0x044)<<16)|((0x044)<<8)|((0xFF)<<24)|(0x044);
			pixels[((int)node.center.getX() + (int)(node.center.getY() + i) * (int)range) % pixels.length] = (int)((0x044)<<16)|((0x044)<<8)|((0xFF)<<24)|(0x044);
			pixels[((int)node.center.getX() + (int)(node.center.getY() - i) * (int)range) % pixels.length] = (int)((0x044)<<16)|((0x044)<<8)|((0xFF)<<24)|(0x044);
		}
		if(!node.isLeaf){
			recursiveDraw(node.children[0], pixels);	
			recursiveDraw(node.children[1], pixels);	
			recursiveDraw(node.children[2], pixels);	
			recursiveDraw(node.children[3], pixels);	
		}
	
	}
	
	public void print(){
		recursivePrint(root);
	}
	
	private void recursivePrint(QuadNode node){
		System.out.println("Node with halfRange " + node.halfRange);
		if(node.isLeaf)
			System.out.println("node occupants " + node.occupants);
		node.center.print();
		if(!node.isLeaf){
			recursivePrint(node.children[0]);	
			recursivePrint(node.children[1]);	
			recursivePrint(node.children[2]);	
			recursivePrint(node.children[3]);	
		}
	}
	
}
