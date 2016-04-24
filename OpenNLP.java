import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.apache.tika.Tika;

/**
 * Created by Kelly on 16/4/16.
 */
public class OpenNLP {
    private static ArrayList<String> filelist = new ArrayList<>();
    //Set<String> person_set=new HashSet<>();
    static Set<String> person = new HashSet<>();
    static Set<String> organization = new HashSet<>();
    static Set<String> location = new HashSet<>();
    static Set<String> date = new HashSet<>();
    static Set<String> time = new HashSet<>();
    static Set<String> percent = new HashSet<>();
    static Set<String> money = new HashSet<>();
    private static String type;

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

    private static String readFile(String filepath) {
        StringBuilder sb = new StringBuilder();
        File file = new File(filepath);
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new FileReader(file));
            String data = null;
            while ((data = bf.readLine()) != null)
                sb.append(data);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bf.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return sb.toString();
    }

    private static String getText(String filepath) {
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

    private static String[] getSentences(String content) throws Exception {
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

    private static String[][] getTokens(String[] sentences) throws Exception {
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

    private static void getNameEntities(String[][] tokens, String modelpath, String model_type) throws Exception {
        //String type="";
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
                switch (model_type) {
                    case "PERSON":
                        person.add(sb.toString());
                        break;
                    case "ORGANIZATION":
                        organization.add(sb.toString());
                        break;
                    case "LOCATION":
                        location.add(sb.toString());
                        break;
                    case "MONEY":
                        money.add(sb.toString());
                        break;
                    case "PERCENT":
                        percent.add(sb.toString());
                        break;
                    case "DATE":
                        date.add(sb.toString());
                        break;
                    case "TIME":
                        time.add(sb.toString());
                        break;
                }
                System.out.println(sb.toString());
                //res.add(tokens[i][j]);
            }
            if (nameSpans != null && nameSpans.length != 0)
                type = nameSpans[0].getType();

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

    public static void main(String args[]) throws Exception {
        //String text=getText("/Users/Kelly/Documents/rawdata/kelly/application_atom+xml/09D447735AD3E4FB27E7C1646328DFF481D1220512B99B10AD9C4A5C7152E903");
        //String filepath="/Users/Kelly/Documents/test2.txt";
        String person_model = "/Users/Kelly/Downloads/en-ner-person.bin";
        String date_model = "/Users/Kelly/Downloads/en-ner-date.bin";
        String location_model = "/Users/Kelly/Downloads/en-ner-location.bin";
        String money_model = "/Users/Kelly/Downloads/en-ner-money.bin";
        String organization_model = "/Users/Kelly/Downloads/en-ner-organization.bin";
        String percentage_model = "/Users/Kelly/Downloads/en-ner-percentage.bin";
        String time_model = "/Users/Kelly/Downloads/en-ner-time.bin";
        getFiles("/Users/Kelly/Documents/rawdata/kelly/application_xml/");
        //List< List<String>> res=new ArrayList<>();
        for (int i = 0; i < filelist.size(); i++) {
            //String content=readFile(filelist.get(i));
            String content = getText(filelist.get(i));
            if (content == null || content.length() == 0)
                continue;
            String[] sentences = getSentences(content);
            if (sentences == null || sentences.length == 0)
                continue;
            String[][] tokens = getTokens(sentences);
            if (tokens == null || tokens.length == 0)
                continue;
            getNameEntities(tokens, person_model, "PERSON");
            getNameEntities(tokens, date_model, "DATE");
            getNameEntities(tokens, location_model, "LOCATION");
            getNameEntities(tokens, money_model, "MONEY");
            getNameEntities(tokens, organization_model, "ORGANIZATION");
            getNameEntities(tokens, percentage_model, "PERCENTAGE");
            getNameEntities(tokens, time_model, "TIME");
           /* for(int j=0;j<tmp.size();j++)
            {
                person_set.add(tmp.get(j));
            }*/
            //res.add(tmp);
            //person_set.add()
        }
        System.out.println("---------------------------------------------------------------------------------");
        /*InputStream modelIn=new FileInputStream("/Users/Kelly/Downloads/en-ner-person.bin");
        InputStream modelInSen=new FileInputStream("/Users/Kelly/Downloads/en-sent.bin");
        InputStream modelInToken=new FileInputStream("/Users/Kelly/Downloads/en-token.bin");
        try{
            TokenNameFinderModel model=new TokenNameFinderModel(modelIn);
            SentenceModel model_sen=new SentenceModel(modelInSen);
            TokenizerModel model_token=new TokenizerModel(modelInToken);
            NameFinderME nameFinder=new NameFinderME(model);
            SentenceDetectorME sentenceDetector=new SentenceDetectorME(model_sen);
            String filepath="/Users/Kelly/Documents/test.txt";
            String sentences[]=sentenceDetector.sentDetect(readFile(filepath));
            Tokenizer tokenizer=new TokenizerME(model_token);
            String tokens[][]=new String[sentences.length][];
            List<String> list=new ArrayList<>();
            for(int i=0;i< sentences.length;i++)
            {
                tokens[i]=tokenizer.tokenize(sentences[i]);
                Span nameSpans[]=nameFinder.find(tokens[i]);
                if(nameSpans==null || nameSpans.length==0) continue;
                int start=nameSpans[0].getStart();
                int end=nameSpans[0].getEnd();
                for(int j=start;j<end;j++)
                    list.add(tokens[i][j]);
                System.out.println("Type is :"+nameSpans[0].getType());

            }
            System.out.println();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        finally {
            if(modelInSen!=null)
            {
                try{
                    modelInSen.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }*/
    }
}