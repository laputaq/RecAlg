import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataFormat {
	public static Map<String,Integer> readMap(String fileName){
		Map<String,Integer> map = new HashMap<String,Integer>();
		BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = null;
            
            while ((line = reader.readLine()) != null) {
            	String[] temp = line.split(",");
            	int nid = Integer.parseInt(temp[0]);
//            	int oid = Integer.parseInt(temp[1]);
            	map.put(temp[1], nid);
            	
            }
            reader.close();
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } 
	}
	
	public static void writeGroups(String userMapFile, String oldGroupFile, String newGroupFile){
		Map<String,Integer> umap = readMap(userMapFile); 
		BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(oldGroupFile));
            FileWriter fw = new FileWriter(newGroupFile);
            String line = null;
            
            while ((line = reader.readLine()) != null) {
            	String[] temp = line.split(",");
            	for(int i=0;i<temp.length;i++){
            		if(i<temp.length-1)
            			fw.write(umap.get(temp[i])+",");
            		else fw.write(umap.get(temp[i])+"\n");
            	}
            	
            }
            reader.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}
	
	public static void writeTrain(String userMapFile, String eventMapFile, String trainFile, String newTrainFile){
		Map<String,Integer> umap = readMap(userMapFile); 
		Map<String,Integer> emap = readMap(eventMapFile); 
		BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(trainFile));
            FileWriter fw = new FileWriter(newTrainFile);
            String line = null;
            
            while ((line = reader.readLine()) != null) {
            	String[] temp = line.split(",");
            	fw.write(umap.get(temp[0])+",");
            	fw.write(emap.get(temp[1])+"\n");
            }
            reader.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}
	
	public static int[][] readGroups(String groupFile){
		List<int[]> list = new ArrayList<int[]>();
		BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(groupFile));
//            FileWriter fw = new FileWriter(newTrainFile);
            String line = null;
            
            while ((line = reader.readLine()) != null) {
            	String[] temp = line.split(",");
            	int[] group = new int[temp.length];
            	for(int i=0;i<temp.length;i++)
            		group[i] = Integer.parseInt(temp[i]);
            	list.add(group);
            }
            reader.close();
            return list.toArray(new int[][] {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } 
	}
	
	public static Map<Integer,List<Integer>> readTrain(String trainFile){
		Map<Integer,List<Integer>> train = new HashMap<Integer,List<Integer>>();
		BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(trainFile));
//            FileWriter fw = new FileWriter(newTrainFile);
            String line = null;
            
            while ((line = reader.readLine()) != null) {
            	String[] temp = line.split(",");
            	Integer gid = Integer.parseInt(temp[0]);
            	Integer eid = Integer.parseInt(temp[1]);
            	if(train.containsKey(gid))
            		train.get(gid).add(eid);
            	else{
            		List<Integer> list = new ArrayList<Integer>();
            		list.add(eid);
            		train.put(gid, list);
            	}
            }
            reader.close();
            return train;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } 
	}
	
	public static void writeGroupTrain(String trainFile, String groupFile, String groupTrainFile){
		int[][] groups = readGroups(groupFile);
		Map<Integer,List<Integer>> train = readTrain(trainFile);
		Map<Integer,Set<Integer>> gtrain = new HashMap<Integer,Set<Integer>>();
		for(int g=0;g<groups.length;g++){
			int[] group = groups[g];
			Set<Integer> set = new HashSet<Integer>(train.get(group[0]));
			for(int i=1;i<group.length;i++){
				set.retainAll(train.get(group[i]));
			}
			gtrain.put(g, set);
		}
		
		try {
            FileWriter fw = new FileWriter(groupTrainFile);
            for(Map.Entry<Integer, Set<Integer>> entry : gtrain.entrySet()){
            	for(int eid : entry.getValue()){
            		fw.write(entry.getKey()+","+eid+"\n");
            	}
            }
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
		
	}
	
	
	public static void writeGeo(String emapfile, String eventsfile, String geofile){
		Map<String,Integer> emap = readMap(emapfile); 
		BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(eventsfile));
            FileWriter fw = new FileWriter(geofile);
            String line = null;
            
            while ((line = reader.readLine()) != null) {
            	String[] temp = line.split(",");
            	fw.write(emap.get(temp[0])+","+temp[2]+","+temp[3]+"\n");
            }
            reader.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
	}
	
	public static void main(String[] args) {
		String dataset = "Doubanevent/";
		String umapfile = "E:/paper/conference/workspace/GLFM/dataset/"+dataset+"user_map.csv";
		String emapfile = "E:/paper/conference/workspace/GLFM/dataset/"+dataset+"event_map.csv";
		String oldtrainfile = "E:/paper/conference/workspace/GLFM/dataset/"+dataset+"train.csv";
		String oldtestfile = "E:/paper/conference/workspace/GLFM/dataset/"+dataset+"test.csv";
		String newtestfile = "dataset/douban/test.csv";
		String newtrainfile = "dataset/douban/train.csv";
		String groupfile = "E:/paper/conference/workspace/GLFM/dataset/"+dataset+"groups_2.csv";
		String newgroupfile = "dataset/douban/groups_2.csv";
//		writeGroups(umapfile,groupfile,newgroupfile);
//		writeTrain(umapfile,emapfile,oldtrainfile,newtrainfile);
		String gtrainfile = "dataset/douban/gtrain_2.csv";
		String gtestfile = "dataset/douban/gtest_2.csv";
		writeGroupTrain(newtrainfile,newgroupfile,gtrainfile);
		writeGroupTrain(newtestfile,newgroupfile,gtestfile);
//		String locationfile = "dataset/douban/locations.csv";
//		String eventsfile = "E:/paper/conference/workspace/GLFM/dataset/"+dataset+"events.csv";
//		writeGeo(emapfile,eventsfile,locationfile);
	}
}
