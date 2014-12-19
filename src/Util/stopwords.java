package Util;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class stopwords {

    public static Set<String> words=new HashSet<String>();

    /**
     * initial the stop-words
     */
    public  static void  init(){
        File file=new File("stopword.dic");
        try {
            String word=null;
            BufferedReader reader=new BufferedReader(new FileReader(file));
            while((word=reader.readLine())!=null){
                words.add(word);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param text a string
     * @return a list with word
     */
    public static List<String>splitWord(String text){
        List<String > l= new ArrayList<>();
        StringReader reader=new StringReader(text);
        IKSegmenter ik=new IKSegmenter(reader,true);
        Lexeme lex=null;
        init();
        try {
            while((lex=ik.next())!=null){
                String w=lex.getLexemeText();
                if(!words.contains(w)){
                    l.add(w);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return l;
    }
}
