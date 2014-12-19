package Util;

import java.io.*;


public class readText {

    private StringBuffer content;

    /**
     *
     * @param f a text file
     * @return a string contain the content the File f
     * @throws IOException
     */
    public String readFile(File f) throws IOException {
        String word;
        content=new StringBuffer();
        BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(f),"GBK"));
        while((word=reader.readLine())!=null){
            content.append(word);
        }
        reader.close();
        //System.out.println(content);
        return content.toString();

    }
    public static void main(String args[]) throws IOException {
        readText r=new readText();
       String s= r.readFile(new File("C:\\Users\\stay\\Documents\\3.txt"));
        System.out.print(s);
    }
}


