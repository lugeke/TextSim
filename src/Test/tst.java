package Test;


import Util.stopwords;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;

public class tst {
    public static  void  main(String args[]) throws IOException {

        String text="今天的天气真好";

        StringReader reader=new StringReader(text);
        IKSegmenter ik=new IKSegmenter(reader,true);
        Lexeme lex=null;
        stopwords.init();
        while((lex=ik.next())!=null){
            String w=lex.getLexemeText();
            if(!stopwords.words.contains(w))
            System.out.print(lex.getLexemeText()+"*");
        }
        reader.close();
        System.out.println();
    }
}
