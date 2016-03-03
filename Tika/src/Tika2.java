/**
 * Created by Kelly on 16/2/10.
 */

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Tika2 {
    public static int H = 50;
    public static int T = 50;

    /*@SuppressWarnings("unchecked")
    public static void populateJsonTempObj(JSONObject temp, double magnitude, double corr_strength){
        temp.put("magnitude",       magnitude);
        temp.put("corr_strength", corr_strength);
    }*/
    @SuppressWarnings("unchecked")
    public static void addJsonArray(JSONArray jsonArray, JSONObject obj) {
        jsonArray.add(obj);
    }

    @SuppressWarnings("unchecked")
    public static void putKeyArray_JsonObj(JSONObject json_obj, String key, JSONArray jsonArray) {
        json_obj.put(key, jsonArray);
    }

    @SuppressWarnings("unchecked")
    public static void putKeyValue_JsonObj(JSONObject json_obj, String key, long no_of_files) {
        json_obj.put(key, no_of_files);
    }

    public static void main(String[] args) throws IOException {
        //how to ignore the .DS_STORE
        //the byte value is negative, how to transform to positive
        Tika2 t = new Tika2();
        KellyFileManager manager = new KellyFileManager();
        String dir = "/Volumes/My Passport/dataSet/";
        File testfile = new File(dir);
        String[] filelist = testfile.list();
        for (int e = 0; e < filelist.length; e++) {
            if (filelist[e].equals(".DS_Store"))
                continue;
            String dir1 = dir + filelist[e];
            //String dir1="/Users/Kelly/Downloads/tika-master/fileClassify/CSCI599/image_gif";
            ArrayList<String> res = t.readDir(dir1);
            double[][] oldHeaderFP = new double[H][256];
            double[][] oldTrailerFP = new double[T][256];
            for (int q = 0; q < H; q++) {
                for (int w = 0; w < 256; w++) {
                    oldHeaderFP[q][w] = 0;
                    oldTrailerFP[q][w] = 0;
                }
            }
            /*String old_path = "/Users/Frank/Downloads/50/" + filelist[e] + ".json";
            File old_json_file = new File(old_path);
            if (old_json_file.exists()) {
                t.loadFingerPrint(old_path, oldHeaderFP, oldTrailerFP);
            }*/

            String json_path = "/Users/Kelly/Documents/USC/2016Spring/CSCI599/50/" + filelist[e] + ".json";
            //String json_path = "/Users/Kelly/Downloads/tika-master/fileClassify/CSCI599/50/" + filelist[e]+".json";
            //t.createFingerPrint(json_path, H);
            int num_of_file = 0;
            for (int i = 0; i < res.size(); i++) {
                String file = res.get(i);
                File tmp = new File(file);
                if (tmp.getName().equals(".DS_Store")) {
                    continue;
                }

                if (manager.checkExistence(tmp.getName())||tmp.length()<2*H) {
                    continue;
                }
                HashMap<Integer, byte[]> map_res = new HashMap<>();
                map_res = t.getTwoProfiles(file);
                byte[] header_byte = map_res.get(0);
                byte[] trailer_byte = map_res.get(1);
                int[][] header = t.getHeaderPrint(header_byte);
                int[][] trailer = t.getTrailerPrint(trailer_byte);
                double[][] newHeaderFP = t.buildNewHeaderFP(header, oldHeaderFP, num_of_file);
                double[][] newTrailerFP = t.buildNewTrailerFP(trailer, oldTrailerFP, num_of_file);
                oldHeaderFP = newHeaderFP;
                oldTrailerFP = newTrailerFP;
                num_of_file++;
            }
            t.updateFingerPrint(json_path, (long) num_of_file, oldHeaderFP, oldTrailerFP, H);
        }
        String path1 = System.getProperty("user.dir") + "/fileMap.json";
        String path2 = System.getProperty("user.dir") + "/dupMap.json";
        t.createJsonMap(path1, path2, manager.fileMap, manager.duplicateFileMap);
        //System.out.println("Hello");
    }

    private ArrayList<String> readDir(String dirpath) {
        ArrayList<String> list = new ArrayList<String>();
        File root = new File(dirpath);
        File[] files = root.listFiles();
        for (File file : files) {
            list.add(file.getAbsolutePath());
        }
        return list;
    }

    private HashMap<Integer, byte[]> getTwoProfiles(String filepath) {
        File file = new File(filepath);
        HashMap<Integer, byte[]> map = new HashMap<>();
        try {
            if (file != null) {
                byte[] header = new byte[H];
                byte[] trailer = new byte[T];
                long len = file.length();
                RandomAccessFile raf = new RandomAccessFile(file, "r");
                if (len >= H) {
                    raf.seek(0);
                    raf.read(header, 0, H);
                    raf.seek(file.length() - T);
                    raf.read(trailer, 0, T);
                } else {
                    raf.seek(0);
                    raf.read(header, 0, (int) len);
                    raf.seek((int) len);
                    raf.seek(0);
                    raf.read(trailer, 0, (int) len);
                    for (int t = (int) len; t < H; t++) {
                        header[t] = -1;
                        trailer[t] = -1;
                    }
                }
                map.put(0, header);
                map.put(1, trailer);
                raf.close();
                return map;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private int[][] getHeaderPrint(byte[] bytes) {
        int[][] header = new int[H][256];
        int len = bytes.length;
        for (int i = 0; i < H && i < len; i++) {
            int tmp = (int) bytes[i] & 0xFF;
            header[i][tmp] = 1;
        }
        if (len < H) {
            for (int i = len; i < H; i++) {
                for (int j = 0; j < 256; j++) {
                    header[i][j] = -1;
                }

            }
        }
        return header;
    }

    private int[][] getTrailerPrint(byte[] bytes) {
        int[][] trailer = new int[T][256];
        int len = bytes.length;
        for (int i = bytes.length - T; i < bytes.length; i++) {
            if (i < 0) {
                for (int j = 0; j < 256; j++) {
                    trailer[i][j] = -1;
                }
                continue;
            }
            int tmp = (int) bytes[i] & 0xFF;
            ;
            trailer[i - len + T][tmp] = 1;
        }
        return trailer;
    }

    private double[][] buildNewHeaderFP(int[][] header, double[][] oldHeaderFP, int file_num) {
        double[][] newHeaderFP = new double[H][256];
        for (int i = 0; i < H; i++) {
            for (int j = 0; j < 256; j++) {

                newHeaderFP[i][j] = (oldHeaderFP[i][j] * file_num + header[i][j]) / (file_num + 1);
            }
        }
        return newHeaderFP;
    }

    private double[][] buildNewTrailerFP(int[][] trailer, double[][] oldTrailerFP, int file_num) {
        double[][] newTrailerFP = new double[T][256];
        for (int i = 0; i < T; i++) {
            for (int j = 0; j < 256; j++) {
                newTrailerFP[i][j] = (oldTrailerFP[i][j] * file_num + trailer[i][j]) / (file_num + 1);
            }
        }
        return newTrailerFP;
    }

    /*private double compareWithFingerprint(double[][]oldFingerPrint,String path)
    {
        HashMap<Integer,byte[]> map_res=new HashMap<>();
        map_res=getTwoProfiles(path);
        byte[] header_byte=map_res.get(0);
        byte[] trailer_byte=map_res.get(1);
        int[][] header=getHeaderPrint(header_byte);
        int[][] trailer=getTrailerPrint(trailer_byte);
    }*/
    private void updateFingerPrint(String jsonFilePath, long no_of_files, double headerdata[][], double trailerdata[][], int bit_num) throws IOException {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray1 = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        for (int i = 0; i < bit_num; i++) {
            JSONArray tmp1 = new JSONArray();
            JSONArray tmp2 = new JSONArray();
            for (int j = 0; j < 256; j++) {
                tmp1.add(headerdata[i][j]);
                tmp2.add(trailerdata[i][j]);
            }
            jsonArray1.add(tmp1);
            jsonArray2.add(tmp2);
        }
        putKeyArray_JsonObj(jsonObject, "headerdata", jsonArray1);
        putKeyArray_JsonObj(jsonObject, "trailerdata", jsonArray2);
        putKeyValue_JsonObj(jsonObject, "no_of_files", no_of_files);
        FileWriter fileWriter = new FileWriter(jsonFilePath);
        fileWriter.write(jsonObject.toJSONString());
        fileWriter.flush();
        fileWriter.close();
    }

    private long loadFingerPrint(String jsonFilePath, double[][] header, double[][] trailer) throws IOException {
        JSONParser parser = new JSONParser();
        long no_of_files = 0;
        try {
            FileReader fileReader = new FileReader(jsonFilePath);
            Object obj = parser.parse(fileReader);
            JSONObject jsonObject = (JSONObject) obj;
            no_of_files = (Long) jsonObject.get("no_of_files");
            JSONArray headerdata = (JSONArray) jsonObject.get("headerdata");
            JSONArray trailerdata = (JSONArray) jsonObject.get("trailerdata");
            //headerdata.
            Iterator<?> iterator1 = headerdata.iterator();
            int i = 0, j = 0;
            while (iterator1.hasNext()) {
                JSONArray tmp_array = (JSONArray) (iterator1.next());
                //double[] tmp_arr=(double[])tmp_array.toArray();
                for (int k = 0; k < 256; k++) {
                    header[i][k] = (double) tmp_array.get(k);
                }
                i++;
            }
            Iterator<?> iterator2 = trailerdata.iterator();
            while (iterator2.hasNext()) {
                JSONArray tmp_array = (JSONArray) (iterator2.next());
                //double[] tmp_arr=(double[])tmp_array.toArray();
                for (int k = 0; k < 256; k++) {
                    header[j][k] = (double) tmp_array.get(k);
                }
                j++;
            }
            fileReader.close();
        } catch (ParseException pe) {
            System.out.print("ParseException at position:" + pe.getPosition());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return no_of_files;
    }
    private void createJsonMap(String path1, String path2, HashMap<String, String> map, HashMap<String, Integer> map2) {
        JSONObject jsonObj = new JSONObject();
        for (String key : map.keySet()) {
            jsonObj.put(key, map.get(key));
        }
        JSONObject jsonObj2 = new JSONObject();
        for (String key : map2.keySet()) {
            jsonObj2.put(key, map2.get(key));
        }
        //System.out.print(jsonObj.toJSONString());

        //System.err.print(jsonObj2.toJSONString());
        try {
            FileWriter fWrite = new FileWriter(path1);
            FileWriter fWrite1 = new FileWriter(path2);
            fWrite.write(jsonObj.toJSONString());

            fWrite1.write(jsonObj2.toJSONString());
            fWrite.flush();
            fWrite.close();
            fWrite1.flush();
            fWrite1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private double getAssuranceHeaderLevel(double header[][])
    {
        double max=Integer.MIN_VALUE;
        for(int i=0;i<H;i++)
        {
            for(int j=0;j<256;j++)
            {
                if(max<header[i][j])
                {
                    max=header[i][j];
                }
            }
        }
        return max;
    }
    private double getAssuranceTrailerLevel(double trailer[][])
    {
        double max=Integer.MIN_VALUE;
        for(int i=0;i<H;i++)
        {
            for(int j=0;j<256;j++)
            {
                if(max<trailer[i][j])
                {
                    max=trailer[i][j];
                }
            }
        }
        return max;
    }
    //private double getHeaderScore(String path,)

}
