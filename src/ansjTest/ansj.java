package ansjTest;


import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;

import java.util.*;

/**
 * Created by HM on 2015/12/5.
 */
public class ansj {


    public static void main(String[] args) {

        List<String> list = new ArrayList();
        list.add("的");
        list.add("了");

        FilterModifWord.insertStopWords(list);

//        FilterModifWord.insertStopWord("的");
//        FilterModifWord.insertStopWord("了");
//        FilterModifWord.insertStopNatures("v") ;


        String str = "小米科技有限公司";
        String str2= "上公交车了发现手机在班上充电的又提前下车回来拿";

        List<Term> parse = ToAnalysis.parse(str2);
        new NatureRecognition(parse).recognition() ;
        System.out.println(FilterModifWord.modifResult(parse));
//        System.out.println(FilterModifWord.modifResult(NlpAnalysis.parse(str2)));
    }
}
