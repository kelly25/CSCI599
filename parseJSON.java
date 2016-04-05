import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * Created by Kelly on 16/4/5.
 */
public class parseJSON {
    public static void main(String args[]) {
        // String json_path="/Users/Kelly/Documents/rawdata/result_json/0A0A4A3D609D2E0160CCCA90E7587BFE50CD2E64CA9EF26E51AEF83FBADC7F08.json";
        JSONParser parser = new JSONParser();
        try {
            FileReader fileReader = new FileReader("/Users/Kelly/Documents/test.json");
            Object obj = parser.parse(fileReader);
            JSONObject data = (JSONObject) obj;
            String[] tmp = new String[4];

            if (data.get("doi") == null) //continue;
                if (data.get("Geographic_NAME") == null) //continue;
                    if (data.get("Geographic_LATITUDE") == null) //continue;
                        if (data.get("Geographic_LONGITUDE") == null) //continue;
                            if (data.get("sweet") == null)//continue;
                                tmp[0] = data.get("Geographic_NAME").toString();
            tmp[1] = data.get("Geographic_LATITUDE").toString();
            tmp[2] = data.get("Geographic_LONGITUDE").toString();
            JSONArray arr = (JSONArray) data.get("sweet");
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < arr.size(); k++)
                sb.append(arr.get(k) + " ");
            tmp[3] = sb.toString();
            //map.put(data.get("doi").toString(), tmp);
        } catch (ParseException pe) {
            System.out.println("ParseException at position: " + pe.getPosition());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
