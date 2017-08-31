package com.base.engine.rendering.meshLoading;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.base.engine.core.Matrix4f;
import com.base.engine.core.Vector2f;
import com.base.engine.core.Vector3f;

public class COLLADAModel{

	private ArrayList<Vector3f> m_positions;
	private ArrayList<Vector2f> m_texCoords;
	private ArrayList<Vector3f> m_normals;
	private ArrayList<OBJIndex> m_indices;

	private ArrayList<int[]> m_jointIndices;
	private ArrayList<float[]> m_jointWeights;
	private ArrayList<Joint> m_joints;
	private Joint root;
	private boolean staticModel;

	public COLLADAModel(String fileName, boolean staticModel){
		m_positions = new ArrayList<Vector3f>();
		m_texCoords = new ArrayList<Vector2f>();
		m_normals = new ArrayList<Vector3f>();
		m_indices = new ArrayList<OBJIndex>();
		m_jointIndices = new ArrayList<int[]>();
		m_jointWeights = new ArrayList<float[]>();
		m_joints = new ArrayList<Joint>();
		this.staticModel = staticModel;

		try{

			File xmlFile = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("library_geometries");
			if(nList.getLength() == 1){
				Element library_geo = (Element) nList.item(0);
				NodeList geometryList = library_geo.getElementsByTagName("geometry");
				for(int iGeometryList = 0; iGeometryList < geometryList.getLength(); iGeometryList++){
					Element mesh = (Element) ((Element) geometryList.item(iGeometryList)).getElementsByTagName("mesh").item(0);
					Element vertices = (Element) (mesh.getElementsByTagName("vertices").item(0));
					String verticesSourceID = ((Element) vertices.getElementsByTagName("input").item(0)).getAttribute("source").substring(1);
					NodeList sources = mesh.getElementsByTagName("source");
					for(int iSourceList = 0; iSourceList < sources.getLength(); iSourceList++){
						if(((Element) sources.item(iSourceList)).getAttribute("id").equals(verticesSourceID)){
							int numVertValues = Integer.parseInt(((Element) ((Element) sources.item(iSourceList)).getElementsByTagName("float_array").item(0)).getAttribute("count"));

							if(numVertValues > 0){
								String[] rows = ((Element) sources.item(iSourceList)).getElementsByTagName("float_array").item(0).getTextContent().split("\n");
								if(rows.length > 1){
									for(int iRows = 0; iRows < rows.length; iRows++){
										String[] vec = rows[iRows].split(" ");
										if(vec.length == 3){
											this.m_positions.add(new Vector3f(Float.parseFloat(vec[0]), Float.parseFloat(vec[1]), Float.parseFloat(vec[2])));
										}
									}
								}else{
									String[] values = ((Element) sources.item(iSourceList)).getElementsByTagName("float_array").item(0).getTextContent().split(" ");
									for(int iStringVals = 0; iStringVals < numVertValues; iStringVals += 3){
										this.m_positions.add(new Vector3f(Float.parseFloat(values[iStringVals]), Float.parseFloat(values[iStringVals + 1]), Float.parseFloat(values[iStringVals + 2])));
									}
								}
							}
						}
					}
					NodeList triangleGroups = mesh.getElementsByTagName("triangles");
					if(triangleGroups.getLength() == 0){
						System.err.println("Geometry not triangulated, unable to read " + fileName);
					}else{
						String normalsSourceID = "";
						String texCoordsSourceID = "";
						for(int iTriangleGroups = 0; iTriangleGroups < triangleGroups.getLength(); iTriangleGroups++){
							int numTriangles = Integer.parseInt(((Element) triangleGroups.item(iTriangleGroups)).getAttribute("count"));
							normalsSourceID = ((Element) ((Element) triangleGroups.item(iTriangleGroups)).getElementsByTagName("input").item(1)).getAttribute("source").substring(1);
							texCoordsSourceID = ((Element) ((Element) triangleGroups.item(iTriangleGroups)).getElementsByTagName("input").item(2)).getAttribute("source").substring(1);
							String textContent = ((Element) triangleGroups.item(iTriangleGroups)).getElementsByTagName("p").item(0).getTextContent();
							String[] triangleIndexData = (textContent.startsWith(" ") ? textContent.substring(1) : textContent).split(" ");

							for(int iTID = 0; iTID < numTriangles * 3; iTID++){
								OBJIndex objIndex = new OBJIndex();
								objIndex.vertexIndex = Integer.parseInt(triangleIndexData[iTID * 3]);
								objIndex.normalIndex = Integer.parseInt(triangleIndexData[iTID * 3 + 1]);
								objIndex.texCoordIndex = Integer.parseInt(triangleIndexData[iTID * 3 + 2]);
								m_indices.add(objIndex);
							}
						}
						for(int iSourceList = 0; iSourceList < sources.getLength(); iSourceList++){
							if(((Element) sources.item(iSourceList)).getAttribute("id").equals(normalsSourceID)){
								int numNormalValues = Integer.parseInt(((Element) ((Element) sources.item(iSourceList)).getElementsByTagName("float_array").item(0)).getAttribute("count"));
								if(numNormalValues > 0){
									String[] rows = ((Element) sources.item(iSourceList)).getElementsByTagName("float_array").item(0).getTextContent().split("\n");
									if(rows.length > 1){
										for(int iRows = 0; iRows < rows.length; iRows++){
											String[] vec = rows[iRows].split(" ");
											if(vec.length == 3){
												this.m_normals.add(new Vector3f(Float.parseFloat(vec[0]), Float.parseFloat(vec[1]), Float.parseFloat(vec[2])));
											}
										}
									}else{
										String[] values = ((Element) sources.item(iSourceList)).getElementsByTagName("float_array").item(0).getTextContent().split(" ");
										for(int iStringVals = 0; iStringVals < numNormalValues; iStringVals += 3){
											this.m_normals.add(new Vector3f(Float.parseFloat(values[iStringVals]), Float.parseFloat(values[iStringVals + 1]), Float.parseFloat(values[iStringVals + 2])));
										}
									}
								}
							}
						}

						for(int iSourceList = 0; iSourceList < sources.getLength(); iSourceList++){
							if(((Element) sources.item(iSourceList)).getAttribute("id").equals(texCoordsSourceID)){
								int numTexcoordValues = Integer.parseInt(((Element) ((Element) sources.item(iSourceList)).getElementsByTagName("float_array").item(0)).getAttribute("count"));
								if(numTexcoordValues > 0){
									String[] rows = ((Element) sources.item(iSourceList)).getElementsByTagName("float_array").item(0).getTextContent().split("\n");
									if(rows.length > 1){
										for(int iRows = 0; iRows < rows.length; iRows++){
											String[] vec = rows[iRows].split(" ");
											if(vec.length == 2){
												this.m_texCoords.add(new Vector2f(Float.parseFloat(vec[0]), Float.parseFloat(vec[1])));
											}
										}
									}else{
										String[] values = ((Element) sources.item(iSourceList)).getElementsByTagName("float_array").item(0).getTextContent().trim().split(" ");
										for(int iStringVals = 0; iStringVals < numTexcoordValues; iStringVals += 3){
											this.m_texCoords.add(new Vector2f(Float.parseFloat(values[iStringVals]), 1.0f - Float.parseFloat(values[iStringVals + 1])));
										}
									}
								}
							}
						}
					}
				}

			}else{
				System.err.println("0 or too much geometry in " + fileName);
				throw new IllegalArgumentException();
			}

			/** Animation Parsing */
			if(!staticModel){
				NodeList libConList = doc.getElementsByTagName("library_controllers");
				assert libConList.getLength() == 1;
				NodeList skinList = ((Element) libConList.item(0)).getElementsByTagName("skin");
				assert skinList.getLength() == 1;
				ArrayList<Matrix4f> invBindMatrices = new ArrayList<Matrix4f>();
				float[] weights = null;
				int numberJoints = -1;
				HashMap<String, Integer> jointNames = null;
				Element skinTag = (Element) skinList.item(0);
				Element jointsTag = (Element) skinTag.getElementsByTagName("joints").item(0);
				Element weightsTag = (Element) skinTag.getElementsByTagName("vertex_weights").item(0);
				String jointsSourceID = "";
				String invBindMatID = "";
				String weightsID = "";
				for(Node n = jointsTag.getFirstChild(); n != null; n = (n.getNextSibling()))
					if(n instanceof Element) switch(((Element) n).getAttribute("semantic").toUpperCase()){
						case "JOINT":
							jointsSourceID = ((Element) n).getAttribute("source").substring(1);
							break;
						case "INV_BIND_MATRIX":
							invBindMatID = ((Element) n).getAttribute("source").substring(1);
							break;
					}
				for(Node n = weightsTag.getElementsByTagName("input").item(0); n != null; n = (n.getNextSibling()))
					if(n instanceof Element && ((Element) n).getAttribute("semantic").equalsIgnoreCase("WEIGHT")) weightsID = ((Element) n).getAttribute("source").substring(1);

				NodeList skinSourceList = skinTag.getElementsByTagName("source");
				for(Node m = skinSourceList.item(0); m != null; m = (m.getNextSibling())){
					if(m instanceof Element){
						Element n = (Element) m;
						if(n.getAttribute("id").equals(jointsSourceID)){
							numberJoints = Integer.parseInt(((Element) n.getElementsByTagName("Name_array").item(0)).getAttribute("count"));
							String textContent = n.getElementsByTagName("Name_array").item(0).getTextContent();
							jointNames = new HashMap<String, Integer>();
							String[] names = textContent.trim().split(" ");
							for(int i = 0; i < names.length; i++)
								jointNames.put(names[i], i);
							assert jointNames.size() == numberJoints;
						}else if(n.getAttribute("id").equals(invBindMatID)){
							int count = Integer.parseInt(((Element) n.getElementsByTagName("float_array").item(0)).getAttribute("count"));
							assert count == numberJoints * 16;
							String textContent = n.getElementsByTagName("float_array").item(0).getTextContent().trim();
							String[] matrices = textContent.split("\n");
							for(int mat = 0; mat < matrices.length; mat++)
								invBindMatrices.add(new Matrix4f().set(matrices[mat].trim().split(" ")));
						}else if(n.getAttribute("id").equals(weightsID)){
							int count = Integer.parseInt(((Element) n.getElementsByTagName("float_array").item(0)).getAttribute("count"));
							String textContent = n.getElementsByTagName("float_array").item(0).getTextContent().trim();
							String[] rows = textContent.split("\n");
							weights = new float[count];
							int c = 0;
							for(int i = 0; i < rows.length; i++){
								String[] elems = rows[i].split(" ");
								for(int j = 0; j < elems.length; j++){
									weights[c] = Float.parseFloat(elems[j]);
									c++;
								}
							}
							assert c == count;
						}
					}
				}

				assert weights != null;
				assert invBindMatrices.size() == numberJoints;
				assert numberJoints > 0;
				String vCountTextContent = weightsTag.getElementsByTagName("vcount").item(0).getTextContent();
				String[] vCountData = vCountTextContent.trim().split(" ");
				String vTextContent = weightsTag.getElementsByTagName("v").item(0).getTextContent();
				String[] vData = vTextContent.trim().split(" ");
				int vIndex = 0;
				for(int i = 0; i < vCountData.length; i++){
					int jointCount = Integer.parseInt(vCountData[i]);
					int[] indicesSet = new int[Rig.MAX_JOINTS];
					float[] weightsSet = new float[Rig.MAX_JOINTS];
					if(jointCount > Rig.MAX_JOINTS){
						int[] itmp = new int[jointCount];
						float[] ftmp = new float[jointCount];
						for(int j = 0; j < jointCount; j++){
							itmp[j] = Integer.parseInt(vData[vIndex + 2 * j]);
							ftmp[j] = weights[Integer.parseInt(vData[vIndex + 2 * j + 1])];
						}
						Arrays.sort(ftmp);
						Arrays.sort(itmp); //wroooooong
						indicesSet = Arrays.copyOfRange(itmp, itmp.length - Rig.MAX_JOINTS, itmp.length);
						weightsSet = Arrays.copyOfRange(ftmp, ftmp.length - Rig.MAX_JOINTS, ftmp.length);
					}else{
						for(int j = 0; j < jointCount; j++){
							indicesSet[j] = Integer.parseInt(vData[vIndex + 2 * j]);
							weightsSet[j] = weights[Integer.parseInt(vData[vIndex + 2 * j + 1])];
						}
					}
					m_jointIndices.add(indicesSet);
					m_jointWeights.add(weightsSet);
					vIndex += jointCount * 2;
				}
				assert m_positions.size() == m_jointWeights.size();

				NodeList libVisSceneList = doc.getElementsByTagName("library_visual_scenes");
				assert libVisSceneList.getLength() == 1;

				int checkJointCount = 0;
				NodeList visSceneNodes = ((Element) libVisSceneList.item(0)).getElementsByTagName("visual_scene").item(0).getChildNodes();
				for(int iNodes = 0; iNodes < visSceneNodes.getLength(); iNodes++){
					if(visSceneNodes.item(iNodes).getNodeName().toUpperCase().equals("NODE") && jointNames.containsKey(((Element) visSceneNodes.item(iNodes)).getAttribute("sid"))){
						Element rootElement = (Element) visSceneNodes.item(iNodes);
						assert rootElement.getAttribute("type").toUpperCase().equals("JOINT");
						assert jointNames.containsKey(rootElement.getAttribute("sid"));
						int id = jointNames.get(rootElement.getAttribute("sid"));
						Joint rootJoint = new Joint(id, new Matrix4f().set(rootElement.getElementsByTagName("matrix").item(0).getTextContent().trim().split(" ")), invBindMatrices.get(id));
						root = rootJoint;
						m_joints.add(rootJoint);
						checkJointCount++;
						checkJointCount += recursiveNodesRun(rootJoint, rootElement, jointNames, invBindMatrices);
					}
				}
				assert checkJointCount == numberJoints;

			}
		}catch (Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}

	private int recursiveNodesRun(Joint parentJoint, Element parentElement, HashMap<String, Integer> jointNames, ArrayList<Matrix4f> invBindMatrices){
		NodeList nodeList = parentElement.getChildNodes();
		int sum = 0;
		if(nodeList.getLength() > 0){
			for(Node n = nodeList.item(0); n != null; n = n.getNextSibling()){
				if(n.getNodeName().toUpperCase().equals("NODE")){
					Element e = (Element) n;
					assert e.getAttribute("type").toUpperCase() == "JOINT";
					assert jointNames.containsKey(e.getAttribute("sid"));
					int id = jointNames.get(e.getAttribute("sid"));
					Joint joint = new Joint(id, new Matrix4f().set(e.getElementsByTagName("matrix").item(0).getTextContent().trim().split(" ")), invBindMatrices.get(id));
					parentJoint.addChild(joint);
					m_joints.add(joint);
					sum += recursiveNodesRun(joint, e, jointNames, invBindMatrices) + 1;
				}
			}
		}
		return sum;
	}

	public IndexedModel toIndexedModel(){
		IndexedModel result = new IndexedModel();
		IndexedModel normalModel = new IndexedModel();
		HashMap<OBJIndex, Integer> resultIndexMap = new HashMap<OBJIndex, Integer>();
		HashMap<Integer, Integer> normalIndexMap = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> indexMap = new HashMap<Integer, Integer>();
		
		ArrayList<int[]> jointIndices = new ArrayList<int[]>();
		ArrayList<float[]> jointWeights = new ArrayList<float[]>();
		
		if(m_normals.size() > m_positions.size()){
			ArrayList<ArrayList<Vector3f>> vertexNormals = new ArrayList<ArrayList<Vector3f>>();
			for(int i = 0; i < m_positions.size(); i++)
				vertexNormals.add(new ArrayList<Vector3f>());
			for(int i = 0; i < m_indices.size(); i++){
				OBJIndex currentIndex = m_indices.get(i);
				vertexNormals.get(currentIndex.vertexIndex).add(m_normals.get(currentIndex.normalIndex));
				m_indices.get(i).normalIndex = currentIndex.vertexIndex;
			}
			m_normals = new ArrayList<Vector3f>();
			for(int i = 0; i < vertexNormals.size(); i++){
				ArrayList<Vector3f> normals = vertexNormals.get(i);
				float length = 0;
				Vector3f avg_normal = new Vector3f();
				for(Vector3f v : normals){
					length += v.length();
					avg_normal = avg_normal.add(v);
				}
				m_normals.add(avg_normal.div(length / normals.size()));
			}
		}
		for(int i = 0; i < m_indices.size(); i++){
			OBJIndex currentIndex = m_indices.get(i);

			Vector3f currentPosition = m_positions.get(currentIndex.vertexIndex);
			Vector2f currentTexCoord = m_texCoords.get(currentIndex.texCoordIndex);
			Vector3f currentNormal = m_normals.get(currentIndex.normalIndex);

			Integer modelVertexIndex = resultIndexMap.get(currentIndex);

			if(modelVertexIndex == null){
				modelVertexIndex = result.getPositions().size();
				resultIndexMap.put(currentIndex, modelVertexIndex);

				result.getPositions().add(currentPosition);
				result.getTexCoords().add(currentTexCoord);
				result.getNormals().add(currentNormal);
				if(!staticModel){
					jointIndices.add(m_jointIndices.get(currentIndex.vertexIndex));
					jointWeights.add(m_jointWeights.get(currentIndex.vertexIndex));
				}
			}

			Integer normalModelIndex = normalIndexMap.get(currentIndex.vertexIndex);

			if(normalModelIndex == null){
				normalModelIndex = normalModel.getPositions().size();
				normalIndexMap.put(currentIndex.vertexIndex, normalModelIndex);

				normalModel.getPositions().add(currentPosition);
				normalModel.getTexCoords().add(currentTexCoord);
				normalModel.getNormals().add(currentNormal);
				normalModel.getTangents().add(new Vector3f(0, 0, 0));
			}

			result.getIndices().add(modelVertexIndex);
			normalModel.getIndices().add(normalModelIndex);
			indexMap.put(modelVertexIndex, normalModelIndex);
		}
		normalModel.calcTangents();

		for(int i = 0; i < result.getPositions().size(); i++)
			result.getTangents().add(normalModel.getTangents().get(indexMap.get(i)));

		if(staticModel)
			return result;
		
		Rig rig = new Rig(root, m_joints);
		
		rig.getJointIndices().addAll(jointIndices);
		rig.getJointWeights().addAll(jointWeights);
		result.setRig(rig);

		return result;
	}
}