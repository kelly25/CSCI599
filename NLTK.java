/**
 * Created by Kelly on 16/4/18.
 */
import org.apache.tika.Tika;
import org.apache.tika.parser.ner.nltk.NLTKNERecogniser;

import java.util.Map;
import java.util.Set;

public class NLTK {

    public static void main(String[] args)
    {
        NLTKNERecogniser nltkneRecogniser=new NLTKNERecogniser();
        Map<String,Set<String>> map=nltkneRecogniser.recognise("Pierre Vinken , 61 years old , will join the board as a nonexecutive director Nov. 29 .\n" +
                "Mr . Vinken is chairman of Elsevier N.V. , the Dutch publishing group .\n" +
                "Rudolph Agnew , 55 years old and former chairman of Consolidated Gold Fields PLC , was named\n" +
                "    a director of this British industrial conglomerate .");
        System.out.println("hello");

    }
}
