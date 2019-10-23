import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IO {
	public static void writeMatrix(double[][] phi, String fileName){
		if(phi==null){
			System.out.printf("parameter is null for %s\n",fileName);
			return;
		}
		
		try{
		FileWriter fw = new FileWriter(fileName);
		for(int i=0;i<phi.length;i++){
			for(int j=0;j<phi[i].length;j++)
				fw.write(phi[i][j]+" ");
			fw.write("\n");
		}
		fw.close();
		}catch (IOException e) {
            e.printStackTrace();
		}
	}
	
	public static Map<Integer,int[]> readRecList(String fileName){
		Map<Integer,int[]> reclist = new HashMap<Integer,int[]>();
		File file = new File(fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			int i;
			while ((line = reader.readLine()) != null) {
				 String[] record = line.split(",");
				 int[] array = new int[record.length-1];
				 for(i=0;i<array.length;i++)
					 array[i] = Integer.parseInt(record[i+1]);
				 reclist.put(Integer.parseInt(record[0]), array);
			}
			reader.close();
		} catch (IOException e) {
            e.printStackTrace();
        } finally {
        	try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
		return reclist;
	}
	
	public static void saveRecList(Map<Integer,int[]> map, String fileName){
		if(map==null){
			System.out.printf("parameter is null for %s\n",fileName);
			return;
		}
		try{
			FileWriter fw = new FileWriter(fileName);
			int i;
			for(Map.Entry<Integer, int[]> entry : map.entrySet()){
				int[] array = entry.getValue();
				fw.write(""+entry.getKey());
				for(i=0;i<array.length;i++)
					fw.write(","+array[i]);
				fw.write("\n");
			}
			fw.close();
			}catch (IOException e) {
	            e.printStackTrace();
			}
	}
	public static void writeMap(Map<Integer,String> map, String fileName){
		if(map==null){
			System.out.printf("parameter is null for %s\n",fileName);
			return;
		}
		try{
			FileWriter fw = new FileWriter(fileName);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				fw.write(entry.getKey()+","+entry.getValue()+"\n");
			}
			fw.close();
			}catch (IOException e) {
	            e.printStackTrace();
			}
	}
}
