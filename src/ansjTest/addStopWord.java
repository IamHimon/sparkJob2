package ansjTest;

import org.ansj.domain.Term;
import org.ansj.recognition.NatureRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.FilterModifWord;

import java.util.List;

/**
 * Created by HM on 2015/12/8.
 */
public class addStopWord {


    public static void main(String[] args) {

        String str = "上公交车了发现手机在班上充电的又提前下车回来拿";
        System.out.println("before filter:"+ToAnalysis.parse(str));




        //加入停用词
        FilterModifWord.insertStopWord("并且") ;
        FilterModifWord.insertStopWord("的");

        //加入过滤词性词性
        FilterModifWord.insertStopNatures("v") ;

        List<Term> parse = ToAnalysis.parse(str);
//        new NatureRecognition(parse).recognition() ;
//        System.out.println(parse);

        //修正词性并且过滤stop word
        parse = FilterModifWord.modifResult(parse);




        System.out.println("after filter:"+parse);

    }
}
