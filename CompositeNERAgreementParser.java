import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.sequences.DocumentReaderAndWriter;
import edu.stanford.nlp.util.Triple;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.apache.tika.Tika;
import org.apache.tika.parser.ner.nltk.NLTKNERecogniser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by Kelly on 16/4/30.
 */
public class CompositeNERAgreementParser {
    private static ArrayList<String> filelist = new ArrayList<>();
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
    private  String getText(String filepath) {
        Tika tika = new Tika();
        String text = "";
        try {
            //get text content
            text = tika.parseToString(new File(filepath));
        } catch (Exception e) {
            System.out.println("error filepath is :" + filepath);
            e.printStackTrace();
        }
        return text;
    }
    private  String[] getSentences(String content) throws Exception {
        InputStream modelInSen = new FileInputStream("/Users/Kelly/Downloads/en-sent.bin");
        String sentences[] = null;
        try {
            SentenceModel model_sen = new SentenceModel(modelInSen);
            SentenceDetectorME sentenceDetector = new SentenceDetectorME(model_sen);
            sentences = sentenceDetector.sentDetect(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (modelInSen != null) {
                try {
                    modelInSen.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return sentences;
    }

    private  String[][] getTokens(String[] sentences) throws Exception {
        InputStream modelInToken = new FileInputStream("/Users/Kelly/Downloads/en-token.bin");
        String tokens[][] = new String[sentences.length][];
        try {
            TokenizerModel model_token = new TokenizerModel(modelInToken);
            Tokenizer tokenizer = new TokenizerME(model_token);
            for (int i = 0; i < sentences.length; i++) {
                tokens[i] = tokenizer.tokenize(sentences[i]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (modelInToken != null) {
                try {
                    modelInToken.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return tokens;
    }
    private static void getNameEntities(String[][] tokens, String modelpath,Map<String,Integer> map) throws Exception {
        //String type="";
       // Map<String,Integer> open_map=new HashMap<>();
        InputStream modelIn = new FileInputStream(modelpath);
        try {
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            NameFinderME nameFinder = new NameFinderME(model);
            Span nameSpans[] = null;
            for (int i = 0; i < tokens.length; i++) {
                nameSpans = nameFinder.find(tokens[i]);
                if (nameSpans == null || nameSpans.length == 0) continue;
                int start = nameSpans[0].getStart();
                int end = nameSpans[0].getEnd();
                StringBuilder sb = new StringBuilder();
                for (int j = start; j < end; j++) {
                    sb.append(tokens[i][j] + " ");
                    //System.out.println(tokens[i][j]);
                }
                if(map.containsKey(sb.toString()))
                    map.put(sb.toString().trim(),map.get(sb.toString())+1);
                else
                    map.put(sb.toString().trim(),1);
                System.out.println(sb.toString());
                //res.add(tokens[i][j]);
            }
            /*if (nameSpans != null && nameSpans.length != 0)
                type = nameSpans[0].getType();*/

            //res.add(type);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        //return res;
    }

    private Map<String,Integer> getopennlp(String text)throws Exception
    {
        Map<String,Integer> open_map=new HashMap<>();
        String person_model = "/Users/Kelly/Downloads/en-ner-person.bin";
        String date_model = "/Users/Kelly/Downloads/en-ner-date.bin";
        String location_model = "/Users/Kelly/Downloads/en-ner-location.bin";
        String money_model = "/Users/Kelly/Downloads/en-ner-money.bin";
        String organization_model = "/Users/Kelly/Downloads/en-ner-organization.bin";
        String percentage_model = "/Users/Kelly/Downloads/en-ner-percentage.bin";
        String time_model = "/Users/Kelly/Downloads/en-ner-time.bin";
        Map<String,Integer> map=new HashMap<>();
        if (text == null || text.length() == 0)
            return null;
        String[] sentences =getSentences(text);
        if (sentences == null || sentences.length == 0)
            return null;
        String[][] tokens = getTokens(sentences);
        if (tokens == null || tokens.length == 0)
            return null;
        getNameEntities(tokens, person_model,open_map);
        getNameEntities(tokens, date_model,open_map);
        getNameEntities(tokens, location_model,open_map);
        getNameEntities(tokens, money_model,open_map);
        getNameEntities(tokens, organization_model,open_map);
        getNameEntities(tokens, percentage_model,open_map);
        getNameEntities(tokens, time_model,open_map);
        return open_map;
    }
    private Map<String,Integer> getnltk(String text) throws Exception
    {
        Map<String,Integer> nltk_map=new HashMap<>();
        NLTKNERecogniser nltkneRecogniser=new NLTKNERecogniser();
        Map<String,Set<String>> map=nltkneRecogniser.recognise(text);
        Set<String> tmp=map.get("NAMES");
        Iterator<String> it = tmp.iterator();
        while (it.hasNext()) {
            String str = it.next();
            if(nltk_map.containsKey(str))
                nltk_map.put(str.trim(),nltk_map.get(str)+1);
            else
                nltk_map.put(str.trim(),1);
        }
        return nltk_map;
    }
    private Map<String,Integer> getcorenlp(String text)throws Exception
    {
        String serializedClassifier = "/Users/Kelly/Downloads/stanford-ner-2015-12-09/classifiers/english.muc.7class.distsim.crf.ser.gz";
        AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);
        Map<String,Integer> corenlp_map=new HashMap<>();
        List<Triple<String, Integer, Integer>> list = classifier.classifyToCharacterOffsets(text);
        for (Triple<String, Integer, Integer> item : list) {
            String tmp=text.substring(item.second(), item.third());
            if(corenlp_map.containsKey(tmp))
                corenlp_map.put(tmp.trim(),corenlp_map.get(tmp)+1);
            else
                corenlp_map.put(tmp.trim(),1);
        }
        return corenlp_map;
    }
    public Map<String,Integer> getCompositeAgreement(String filepath)throws Exception{
        String text=getText(filepath);
        if(text==null||text.length()==0)
            return null;
        Map<String,Integer> corenlp=getcorenlp(text);
        Map<String,Integer> opennlp=getopennlp(text);
        Map<String,Integer> nltk=getnltk(text);
        Map<String,Integer> res=new HashMap<>();
        for(String tmp:opennlp.keySet())
        {
            if(nltk.containsKey(tmp))
            {
                if(corenlp.containsKey(tmp))
                {
                    int a=nltk.get(tmp);
                    int b=opennlp.get(tmp);
                    int c=corenlp.get(tmp);
                    int min=a<b?(a<c?a:c):(b<c?b:c);
                    res.put(tmp.trim(),min);
                }
            }
        }
        return res;
    }
    private static Map<String,Integer> combineMap(Map<String,Integer> map1,Map<String,Integer> map2)
    {
        /*Map<String,Integer> res=new HashMap<>();
        for(String tmp:map1.keySet())
        {
            if(map2.containsKey(tmp))
            {
                res.put(tmp,map1.get(tmp)+map2.get(tmp));
                map2.remove(tmp);
            }
            else
                res.put(tmp,map1.get(tmp));
        }
        for(String tmp:map2.keySet())
        {
            res.put(tmp,map2.get(tmp));
        }
        return res;*/
        for(String tmp:map2.keySet())
        {
            if(map1.containsKey(tmp))
                map1.put(tmp,map1.get(tmp)+map2.get(tmp));
            else
                map1.put(tmp,map2.get(tmp));
        }
        return map1;
    }
    private static PriorityQueue<String> getMaxElement(Map<String,Integer> map,int num)
    {
        Comparator<String> Order=new Comparator<String>() {
            //@Override
            public int compare(String key1,String key2) {
                int val1=map.get(key1);
                int val2=map.get(key2);
                if(val1>val2)
                    return 1;
                else if(val1<val2)
                    return -1;
                else
                    return 0;
            }
        };
        PriorityQueue<String> pq=new PriorityQueue<String>(num,Order);
        for(String key:map.keySet())
        {
            pq.add(key);
        }
        for(int i=0;i<map.size()-num;i++)
        {
            pq.remove();
        }
        return pq;
    }
    public static void main(String[] args)throws Exception{
        /*Map<String,Integer> map1=new HashMap<>();
        //Map<String,Integer> map2=new HashMap<>();
        map1.put("A",2);
        map1.put("B",1);
        map1.put("C",3);
        map1.put("D",0);
        PriorityQueue<String> pq1=getMaxElement(map1,2);
        for(int i=0;i<map1.size()-2;i++)
        {
            pq1.remove();
        }
        pq1.remove();*/
        getFiles("/Users/Kelly/Documents/test/");
        CompositeNERAgreementParser compositeNERAgreementParser=new CompositeNERAgreementParser();
        Map<String,Integer> res=compositeNERAgreementParser.getCompositeAgreement(filelist.get(0));
        for(int i=1;i<filelist.size();i++)
        {
            Map<String,Integer> tmp=compositeNERAgreementParser.getCompositeAgreement(filelist.get(i));
            compositeNERAgreementParser.combineMap(res,tmp);
        }
        PriorityQueue<String> pq=compositeNERAgreementParser.getMaxElement(res,10);
        System.out.print("hello");
    }
}
