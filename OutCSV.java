import java.io.*;
import java.text.ParseException;
import java.util.*;

import net.arnx.jsonic.JSON;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
/**
 * Created by Kelly on 16/5/1.
 */
public class OutCSV {
    public static boolean exportCsv(File file, Map<String,Integer> dataList){
        boolean isSucess=false;

        FileOutputStream out=null;
        OutputStreamWriter osw=null;
        BufferedWriter bw=null;
        try {
            out = new FileOutputStream(file);
            osw = new OutputStreamWriter(out);
            bw =new BufferedWriter(osw);
            if(dataList!=null && !dataList.isEmpty()){
                for(String key : dataList.keySet()){
                    int tmp=dataList.get(key);
                    String x=key+","+tmp;
                    //bw.append(key).append(",").append("\r");
                    bw.append(x).append("\r");
                }
            }
            isSucess=true;
        } catch (Exception e) {
            isSucess=false;
        }finally{
            if(bw!=null){
                try {
                    bw.close();
                    bw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(osw!=null){
                try {
                    osw.close();
                    osw=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out!=null){
                try {
                    out.close();
                    out=null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isSucess;
    }
    public static void main(String[] args)throws Exception
    {
        Map<String,Integer> map=new HashMap<>();
        List<String> list=new ArrayList<>();
        String output="/Users/Kelly/Desktop/res.csv";
        File file=new File(output);
        JSONParser parser=new JSONParser();
        try{
            FileReader fileReader = new FileReader("/Users/Kelly/Documents/USC/2016Spring/CSCI599/assgin3/output.json");
            Object obj = parser.parse(fileReader);
            JSONObject data = (JSONObject) obj;
            for(Object key:data.keySet())
            {
                JSONObject tmp=(JSONObject)data.get((String)key);
                if(tmp.get("Content-Type")==null)
                    continue;
                String str=(String)tmp.get("body")+"-"+(String)tmp.get("request_type")+"-"+(String)tmp.get("Content-Type");
                str=str.replace(',',';');
                if(map.containsKey(str)) {
                    map.put(str,map.get(str)+1);
                }
                else
                {
                    map.put(str, 1);
                }
                //list.add(str);
                System.out.println(tmp);

            }
            exportCsv(file,map);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
