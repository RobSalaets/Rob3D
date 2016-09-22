package com.base.engine.core.persistence;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.base.engine.core.GameObject;
import com.base.engine.rendering.Material;

public class SerializeUtil{
	
	public static void serializeScene(String sceneDirectoryName, ArrayList<GameObject> objects){
		ArrayList<Object> scene = new ArrayList<>();
		scene.addAll(objects);
		scene.add(0, scene.size());
		new File("./res/scene/" + sceneDirectoryName).mkdirs();
		serialize("./res/scene/" + sceneDirectoryName + "/scene.ser", scene);
	}
	
	public static void serializeMaterials(String sceneDirectoryName, ArrayList<Material> materials){
		ArrayList<Object> mats = new ArrayList<>();
		mats.add(0, materials.size());
		new File("./res/scene/" + sceneDirectoryName).mkdirs();
		serialize("./res/scene/" + sceneDirectoryName + "/materials.ser", mats);
	}


	public static void loadScene(String sceneDir, ArrayList<GameObject> scene){
		deserialize("./res/scene/" +  sceneDir + "/scene.ser", scene);
	}
	
	public static void loadMaterials(String sceneDir, ArrayList<Material> materials){
		deserialize("./res/scene/" +  sceneDir + "/materials.ser", materials);
	}
	
	private static void serialize(String fileName, ArrayList<Object> objects){
		FileOutputStream objectsFile;
		try{
			objectsFile = new FileOutputStream(fileName);
			ObjectOutputStream out = new ObjectOutputStream(objectsFile);
			for(int i = 0; i < objects.size(); i++){
				out.writeObject(objects.get(i));
			}
			objectsFile.close();
			out.close();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private static <T> void deserialize(String fileName, ArrayList<T> receiver){
		try{
			FileInputStream objectsFile = new FileInputStream(fileName);
			ObjectInputStream in = new ObjectInputStream(objectsFile);
			int size = (int) in.readObject();
			
			for(int i = 0; i < size; i++){
				T o = null;
				o = (T) in.readObject();
				receiver.add(o);
			}
			objectsFile.close();
			in.close();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}catch (ClassNotFoundException e){
			e.printStackTrace();
		}
	}
}
