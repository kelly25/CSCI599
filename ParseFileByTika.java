import org.apache.tika.Tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.json.simple.parser.ParseException;

/**
 * Created by Kelly on 16/4/5.
 */
public class ParseFileByTika {
    static String getText(String filepath)
    {
        Tika tika=new Tika();
        String text="";
        try{
            //get text content
            text=tika.parseToString(new File("/Users/Kelly/Documents/E4ADA91966BD523EB8EA6DD9C2F18B1AE37A3F2C782F1AEA51CDDD72575BD9D6"));
            System.out.println(text);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return text;
    }
    static Map<String,String> getMetadata(String filepath) throws Exception
    {
        Map<String,String> map=new HashMap<>();
        File file=new File(filepath);
        //Parser method parameters
        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        try{
            FileInputStream inputstream = new FileInputStream(file);
            ParseContext context = new ParseContext();

            parser.parse(inputstream, handler, metadata, context);
            System.out.println(handler.toString());

            //getting the list of all meta data elements
            String[] metadataNames = metadata.names();
            for(String name : metadataNames) {
                System.out.println(name + ": " + metadata.get(name));
                map.put(name,metadata.get(name));
            }
        }catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return map;
    }
    public static void main(String args[])
    {


    }
}
