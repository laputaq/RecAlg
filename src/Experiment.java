import java.io.*;
import java.util.*;

public class Experiment {

    private static String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public static void main(String[] args) {
        locationMigration("train");
        locationMigration("test");
    }

    // 输出某一群组随时间的推移而导致的位置的变化
    private static void locationMigration(String fileName) {
        System.out.println(fileName);
        int g_num = 20, max, times, target = 0;
        ArrayList<Integer>[] groupLocs = getGroupLocTimes(fileName);
        HashMap<Integer, Integer> locTimes;
        for (int i = 0; i < g_num; i++) {
            max = 0;
            locTimes = new HashMap<>();
            assert groupLocs != null;
            for (int item : groupLocs[i]) {
                times = locTimes.getOrDefault(item, 0);
                locTimes.put(item, times + 1);
                if (locTimes.get(item) > max) {
                    max = locTimes.get(item);
                    target = item;
                }
            }
            System.out.printf("group: %d, target: %d, max: %d, size: %d, rate: %f\n", i, target, max, groupLocs[i].size(), 1.0 * max / groupLocs[i].size());
            for (int key : locTimes.keySet()) {
                System.out.print(key + ": " + locTimes.get(key) + '\t');
            }
            System.out.println("\n-----------------------");
        }
        System.out.println();
    }

    private static ArrayList<Integer>[] getGroupLocTimes(String fileName) {
        File eventFile = new File("dataset/meetup/events.csv");
        File trainFile = new File("dataset/meetup/" + fileName + ".csv");
        int e_num = 20917, g_num = 20;
        int[] eventLocs = new int[e_num];
        ArrayList<Integer>[] grouplocs = new ArrayList[g_num];
        BufferedReader event = null;
        BufferedReader train = null;
        String line;
        try {
            event = new BufferedReader(new FileReader(eventFile));
            train = new BufferedReader(new FileReader(trainFile));

            event.readLine();
            while ((line = event.readLine()) != null) {
                String[] tmp = line.split(",");
                int e_id = Integer.parseInt(tmp[0]);
                int l_id = Integer.parseInt(tmp[1]);
                eventLocs[e_id] = l_id;
            }

            train.readLine();
            while ((line = train.readLine()) != null) {
                String[] tmp = line.split(",");
                int g_id = Integer.parseInt(tmp[0]);
                int e_id = Integer.parseInt(tmp[1]);
                if (grouplocs[g_id] == null) {
                    grouplocs[g_id] = new ArrayList<>();
                }
                grouplocs[g_id].add(eventLocs[e_id]);
            }
            return grouplocs;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (event != null) {
                try {
                    event.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (train != null) {
                try {
                    train.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private static int getWeek(int time) {
        Calendar.getInstance().setTimeInMillis(1000L * time);
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
    }
}
