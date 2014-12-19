package FuzzDect;


import Util.stopwords;
import java.io.*;
import java.util.*;


public class Fuzz {


    public static String contentA,contentB;
    public static List<String> scentencesA,scentencesB;
    public static List<result> resultList;

    static Map<String,Set<String> > synDict;
    public Fuzz(String a,String  b){

        resultList=new ArrayList<>();
        contentA=a;
        contentB=b;
        String sa[]=a.split("。");
        String sb[]=b.split("。");
        scentencesA=new ArrayList<>(Arrays.asList(sa));
        scentencesB=new ArrayList<>(Arrays.asList(sb));
        int sizea=scentencesA.size(),sizeb=scentencesB.size();
        for(int i=0;i<sizea;++i)
            for(int j=0;j<sizeb;++j){
                if(calSim(scentencesA.get(i),scentencesB.get(j))>0.6){
                    resultList.add(new result(i,j));
                }
            }
        System.out.print(resultList);
    }


    /**
     *init synDict
     * @throws IOException
     */
    public static void   init() throws IOException {
        synDict=new HashMap<>();
        String word;
        File f=new File("哈工大信息检索研究中心同义词词林扩展版.txt");
        BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(f),"GBK"));
        while((word=reader.readLine())!=null){
            String s[]=word.split(" ");
            for(int i=1;i<s.length;++i){
                if(!synDict.containsKey(s[i])){
                    Set<String> set=new HashSet<String>();
                    set.add(s[0]);
                    synDict.put(s[i],set);
                }else{
                    //System.out.println("c->"+s[i]);
                    synDict.get(s[i]).add(s[0]);

                }
            }
        }
        reader.close();
    }

    public static class result{

        int i,j;

        public result(int i,int j){
            this.i=i;
            this.j=j;
        }
         public  int  geti() {
            return i;
        }public  int  getj() {
            return j;
        }

        @Override
        public String toString() {
            return "i="+i+",j="+j;
        }
    }

    public double calSim(String a,String b){
        double result=0;
        List<String> la,lb;
        la= stopwords.splitWord(a);
        lb=stopwords.splitWord(b);
        if(la.size()==lb.size()){
           result=sim(la,lb);
        }
        else{
            double resulta=sim(la,lb);
            double resultb=sim(lb,la);
            result = Math.min(resulta,resultb);
        }
        return result;
    }

    /**
     *
     * @param la a scentence vector
     * @param lb
     * @return the similarity of scentence A and B
     */
    double sim(List<String> la,List<String> lb){
        double result=0;
        int sizea=la.size();
        int sizeb=lb.size();
        for(int i=0;i<sizea;++i) {
            double r=1;
            for (int j = 0; j < sizeb; ++j) {
                String wordi=la.get(i);
                String wordj=lb.get(j);
                if(wordi.equals(wordj)){
                    r=0;break;
                }else if(isSynset(wordi,wordj)){
                    r*=0.5;
                }else if(isRelevance(wordi,wordj)){
                    r*=0.8;            }
                else {
                    //r*=1;
                }
            }
            result+=1-r;
        }
        result/=sizea;
        return result;
    }
    public boolean isRelevance(String a,String b){
        if(synDict.get(a)==null||synDict.get(b)==null)return false;
        Set<String> fa=new HashSet<>(synDict.get(a));
        Set<String> fb=new HashSet<>(synDict.get(b));
        fa.retainAll(fb);
        if(fa.size()!=0){
            for(String s:fa){
                if(s.charAt(s.length()-1)=='#')
                    return true;
            }
            return false;
        }else
            return false;
    }

    public boolean isSynset(String a, String b){
        if(synDict.get(a)==null||synDict.get(b)==null)return false;
        Set<String> fa=new HashSet<>(synDict.get(a));
        Set<String> fb=new HashSet<>(synDict.get(b));
        fa.retainAll(fb);
        if(fa.size()!=0){
            for(String s:fa){
                if(s.charAt(s.length()-1)=='=')
                    return true;
            }
            return false;
        }else
            return false;
    }

    public static void  main(String args[]) throws IOException {
        Fuzz.init();
        Fuzz fuzz=new Fuzz("这是一个漂亮的本国人","他是一个好看的国人");

    }

}
