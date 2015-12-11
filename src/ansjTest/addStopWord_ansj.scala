package ansjTest

import java.io.File
import java.nio.charset.Charset
import java.nio.file.{Paths, Files}
import java.util

import org.ansj.recognition.NatureRecognition
import org.ansj.splitWord.analysis.ToAnalysis
import org.ansj.util.FilterModifWord

import scala.collection.mutable.ListBuffer

/**
 * Created by HM on 2015/12/8.
 */
object addStopWord_ansj {

  //segmenter:input:String, output:List[String],filter chinese punctuation marks
  def ansjSegmenterToString(string: String): String = {


    val list = ListBuffer[String]()
    val result = ToAnalysis.parse(string.replaceAll("\\pP", ""))
    var i = 0
    while (i < result.size()) {
      list.append(result.get(i).getName)
      i += 1
    }
    val tempList = list.toList

    def listToString(list: List[String]): String = {
      var resultStr: String = ""
      list.foreach(f => {
        resultStr = resultStr + f + " "
      })
      return resultStr
    }

    return listToString(tempList)
  }


  def stopwordList(): util.List[String] = {

//    val list = new util.ArrayList[String]()

    val line = Files.readAllLines(Paths.get("dic/stopword.txt"), Charset.forName("utf-8"))
//    for(l <- line){
//      println(l)
//    }

//    println(line)

//    val stopword:util.List[String] = util.Arrays.asList("并且","但是")

    return line
  }


  def main(args: Array[String]) {

//    println(stopwordList())

    val str = "停用词过滤了.并且修正词性为用户自定义的词性.但是你必须设置停用词性词性词典"
    //    System.out.println(ansjSegmenterToString(str))

//    val list = new util.ArrayList[String]()
//    list.add("并且")
//    list.add("但是")
    FilterModifWord.insertStopWords(stopwordList())


    //    //加入停用词
    //    FilterModifWord.insertStopWord("并且")
    //    FilterModifWord.insertStopWord("但是")
    //
    //    //加入过滤词性词性
    //    FilterModifWord.insertStopNatures("v")

    var parse = ToAnalysis.parse(str)
    new NatureRecognition(parse).recognition
//        println(parse)
//
    //修正词性并且过滤停用
    parse = FilterModifWord.modifResult(parse)

    println(parse)
  }

}
