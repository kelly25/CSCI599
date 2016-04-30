/**
 * Created by Kelly on 16/4/17.
 */

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.Triple;
import org.apache.tika.Tika;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CoreNER {
    private static ArrayList<String> filelist = new ArrayList<>();
    //Set<String> person_set=new HashSet<>();
    static Set<String> person = new HashSet<>();
    static Set<String> organization = new HashSet<>();
    static Set<String> location = new HashSet<>();
    static Set<String> date = new HashSet<>();
    static Set<String> time = new HashSet<>();
    static Set<String> percent = new HashSet<>();
    static Set<String> money = new HashSet<>();
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
    static void getFiles(String filepath) {
        File root = new File(filepath);
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.getName().equals(".DS_Store"))
                continue;
            if (file.isDirectory())
                getFiles(file.getAbsolutePath());
            filelist.add(file.getAbsolutePath());
        }

    }
    public static void main(String[] args) throws Exception {

        String serializedClassifier = "/Users/Kelly/Downloads/stanford-ner-2015-12-09/classifiers/english.muc.7class.distsim.crf.ser.gz";

        if (args.length > 0) {
            serializedClassifier = args[0];
        }

        AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);

    /* For either a file to annotate or for the hardcoded text example, this
       demo file shows several ways to process the input, for teaching purposes.
    */


      /* For the file, it shows (1) how to run NER on a String, (2) how
         to get the entities in the String with character offsets, and
         (3) how to run NER on a whole file (without loading it into a String).
      */

        getFiles("/Users/Kelly/Documents/rawdata/test/");
        for (int i = 0; i < filelist.size(); i++) {
            String path = filelist.get(i);
            String fileContents = IOUtils.slurpFile(path);
            //String fileContents=getText(path);
            List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(fileContents);
            for (Triple<String, Integer, Integer> item : list) {
                System.out.println(item.first() + ": " + fileContents.substring(item.second(), item.third()));
                switch (item.first) {
                    case "PERSON":
                        person.add(fileContents.substring(item.second(), item.third()));
                        break;
                    case "ORGANIZATION":
                        organization.add(fileContents.substring(item.second(), item.third()));
                        break;
                    case "LOCATION":
                        location.add(fileContents.substring(item.second(), item.third()));
                        break;
                    case "MONEY":
                        money.add(fileContents.substring(item.second(), item.third()));
                        break;
                    case "PERCENT":
                        percent.add(fileContents.substring(item.second(), item.third()));
                        break;
                    case "DATE":
                        date.add(fileContents.substring(item.second(), item.third()));
                        break;
                    case "TIME":
                        time.add(fileContents.substring(item.second(), item.third()));
                        break;
                }
            }
        }

        System.out.println("hello");


    }
}
