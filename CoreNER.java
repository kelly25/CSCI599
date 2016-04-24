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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CoreNER {
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
        Set<String> person = new HashSet<>();
        Set<String> organization = new HashSet<>();
        Set<String> location = new HashSet<>();
        Set<String> date = new HashSet<>();
        Set<String> time = new HashSet<>();
        Set<String> percent = new HashSet<>();
        Set<String> money = new HashSet<>();
        String path = "/Users/Kelly/Documents/test.txt";
        String fileContents = IOUtils.slurpFile(path);
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
        System.out.println("hello");


    }
}
