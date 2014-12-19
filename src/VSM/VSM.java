package VSM;


import Util.readText;
import Util.stopwords;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;


public class VSM {

    private List<File> fileList;//文档集合
    private Set<String> termSet;//term集合
    private Map<String,Map<String,Integer>> wordSet;//每个文档对应的分词集合
    private Map<String,List<Double>> tfidf;//文档向量

    public static String path;
    public static   List<CalResult> result;//结果集
    private Map<String,Integer> count;//每个文档的term计数

    public VSM(List<File> l){
        this.fileList=l;
        path=l.get(0).getParent();
        System.out.println("path->"+path);
    }

    int find(int x){
        int r=x;
        while (r!=set[r])
            r=set[r];
        return r;
    }
    void merge(int a,int b){
        int fa=find(a);
        int fb=find(b);
        if(fa!=fb){
            set[fa]=fb;
        }
    }
    int set[];
    void init(){
        int n=fileList.size();
         set=new int [n];
        for(int i=0;i<n;++i){
            set[i]=i;
        }
    }

    void outputSet(){
        Map<Integer,Set<Integer>> map= new HashMap<>();
        for(int i=0;i<set.length;++i){
            int p=find(i);
            if(map.containsKey(p)){
                map.get(p).add(i);
            }else{
                Set<Integer> set1=new HashSet<>();
                set1.add(i);
                map.put(p,set1);
            }
        }
        System.out.println("map->count=" + map.size());
        map.forEach((k,v)->{
            if(v.size()>1){
                System.out.println("set" + k + ":" + v);
            }
        });
        System.out.println(Arrays.toString(set));
    }
    /**
     * split the word in scentences
     */
    public void  wordSplit(){
        termSet=new HashSet<>();
        wordSet =new HashMap<String,Map<String,Integer>>();
        stopwords.init();
        readText r = new readText();
        count=new HashMap<>();
        int cnt=0;
        for(File f:fileList) {

            String text = null;
            wordSet.put(f.getName(),new HashMap<>());
            try {
                text = r.readFile(f);
               // System.out.println("text->"+text);
                StringReader reader = new StringReader(text);
                IKSegmenter ik = new IKSegmenter(reader, true);

                Lexeme lex = null;

                Map<String,Integer> map=wordSet.get(f.getName());
                cnt=0;
                while ((lex = ik.next()) != null) {
                    String w = lex.getLexemeText();
                    if (!stopwords.words.contains(w)){
                        //System.out.print(lex.getLexemeText() + "*");
                        termSet.add(w);
                        cnt++;
                        if(map.get(w)==null){
                            map.put(w,1);
                        }else{
                            int i=map.get(w);
                            map.replace(w,i+1);
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
           // System.out.print("cnt="+cnt);
            count.put(f.getName(),cnt);
        }
    }


    public void termSelection(){
        termSet.forEach(t->{
            int cnt=0;
            for(Map.Entry<String,Map<String,Integer>> entry:wordSet.entrySet()){
                if(entry.getValue().get(t)!=null){
                    cnt++;
                }
            }
            if(cnt<0.1*fileList.size()){
                termSet.remove(t);
            }
        });
    }
    /**
     * According to the  settings, count the tf or tfidf value
     */
    public void countTfidf() throws IOException {
        List<Double> ti;
        tfidf=new HashMap<>();
        int D=fileList.size();
        //read set file
        File setfile=new File("set.txt");
        readText rt=new readText();
        String set=rt.readFile(setfile);

        for(File f:fileList) {
            ti = new ArrayList<Double>();
            Iterator<String> it=termSet.iterator();
            Map<String ,Integer > map=wordSet.get(f.getName());
            while (it.hasNext()) {
                String term = it.next();
                //System.out.print("term:"+term);
                if(map.get(term)==null){
                    ti.add(0.00);
                }else{
                    double  tf=map.get(term)/(double)count.get(f.getName());
                    double dft=0;
                   /* for (Map.Entry<String, String> entry : map.entrySet()) {
                        System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
                    }*/
                    for(Map.Entry<String,Map<String,Integer>> entry: wordSet.entrySet()){
                        if(entry.getValue().get(term)!=null){
                            dft+=1;
                        }
                    }

                    if(set.charAt(2)=='0')//
                    ti.add( tf );  //tf
                    else
                    ti.add(tf*Math.log(D/dft));// tfidf
                }

            }
            tfidf.put(f.getName(),ti);

        }
    }


    /**
     * calculate the similarity using cosine method
     */
    public void SIMcos(){

        init();
        File f=new File("set.txt");
        double threshold=30;
        int choice=0;
        readText rt=new readText();
        try {
            String s=rt.readFile(f);
            String list[]=s.split("-");
            threshold=Double.parseDouble(list[0]);
            System.out.println("threshold="+threshold);
            choice=Integer.parseInt(list[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int n=fileList.size();
        result=new ArrayList<>();
        for(int i=0;i<n-1;++i)
            for(int j=i+1;j<n;++j){
                String a=fileList.get(i).getName();
                String b=fileList.get(j).getName();
                List<Double> veca=tfidf.get(a);
                List<Double> vecb=tfidf.get(b);
                int count=veca.size();
                double t,ta,tb;
                t=ta=tb=0;
                for(int k=0;k<count;++k){
                    double vecak=veca.get(k);
                    double vecbk=vecb.get(k);
                    t+=vecak*vecbk;
                    ta+=vecak*vecak;
                    tb+=vecbk*vecbk;
                }
                double sim=t/ Math.sqrt(ta)/ Math.sqrt(tb);
                //System.out.println(sim);
                String r=null;
                sim=(double)Math.round(sim*100)/100;
                if(sim*100>=threshold) {
                    r = "抄袭";
                    merge(i,j);
                }
                else {
                    r = "正常";
                }
                result.add(new CalResult(a,b,sim,r));

            }
        outputSet();
        //System.out.println(result);
       // output();
    }


    //for test only
    void output(){

        System.out.println("FileList->" + fileList);
        System.out.println("TermSet" + termSet);
        System.out.println("Count:"+count);
        for(Map.Entry<String,Map<String,Integer>> entry:wordSet.entrySet()){
            System.out.print("file:"+ entry.getKey());
            //Map<String,Integer> map=entry.getValue();
            for(Map.Entry<String,Integer> entry1:entry.getValue().entrySet() ){
                System.out.print(" word:"+entry1.getKey()+",cnt="+entry1.getValue());
            }
            System.out.println();
        }
        for(Map.Entry<String,List<Double>> entry:tfidf.entrySet()){//tfidf
            System.out.println("file:"+entry.getKey()+",vec= "+entry.getValue());
        }

    }


}
