package ansjTest

import java.util.regex.Pattern

import org.ansj.splitWord.analysis.ToAnalysis
import org.ansj.util.FilterModifWord

import scala.collection.mutable.ListBuffer

/**
 * Created by HM on 2015/12/5.
 */
object test {
  //judge if the str is chinese
  def isChinese(str: String): Boolean = {
    var result = true
    //Chinese character Encoding range：[\u4E00-\uFA29]|[\uE7C7-\uE7F3]
    //match chinese character which length is between 2 and 5.
    val pattern = Pattern.compile("^([\\u4E00-\\uFA29]|[\\uE7C7-\\uE7F3]){1,5}$")
    val matcher = pattern.matcher(str)
    if (!matcher.find())
      result = false
    return result
  }


  //input:String, output:List[String]
  def ansjSegmenter(str: String): List[String] = {

    val stopword:java.util.List[String] = null
    stopword.add("的")
    stopword.add("finalResult")
    println(stopword)
    FilterModifWord.insertStopWords(stopword)

    val list = ListBuffer[String]()
    val result = ToAnalysis.parse(str)
    val finalResult = FilterModifWord.modifResult(result)
    var i = 0
    while (i < finalResult.size()) {
      list.append(finalResult.get(i).getName)
      i += 1
    }
    val R = list.toList.filter(f => isChinese(f))
    return R
  }

  def listToString(list: List[String]): String = {
    var resultStr: String = ""
    list.foreach(f => {
      resultStr = resultStr + f + " "
    })
    return resultStr
  }

  //


  /*
  * 1048576,[96163,650202,693856],[8.242560621923047,12.899221076089338,7.330876572328241]
  * transform to:  96163:8.242560621923047 650202:12.899221076089338 693856:7.330876572328241
  *
  * */
  def tfidfToSVM(string: String): String = {
    val str = string.substring(8, string.length)
    val array = str.split("],")
//    println(array(0)+"|"+array(1))
    val a1: String = array(0).replaceAll("\\[|\\]", "")
    val a2: String = array(1).replaceAll("\\[|\\]", "")
//    println(a1+"|"+a2)
    val arr1: Array[String] = a1.split(",")
    val arr2: Array[String] = a2.split(",")


    var svm:String = ""
    var i = 0

    for(i <- 0 to arr1.size-1){
//      val temp = arr1(i)+":"+arr2(i)+" "
//      println(temp)
      svm = svm+arr1(i)+":"+arr2(i)+" "

    }
    return svm

  }



  def main(args: Array[String]) {

        val str = "您好：我是大庆麦凯乐四楼玛丝菲尔素的，现在咱家x.x节大力搞活动了、、、部分商品x折、活动力度很大的、活动仅限x.x号以前噢、美女姐姐"
    //    val result = str.replaceAll("\\pP","")
    //    println(result)
    //    println(ansjSegmenter(str))
        println(listToString(ansjSegmenter(str)))

//        val list = ("你","好","吗")
    //    println(list)
    //    println(listToString(list))

//    val tfidf = "1048576,[96163,650202,693856],[8.242560621923047,12.899221076089338,7.330876572328241]"
//
//    val svm = tfidfToSVM(tfidf)
//    println(svm)

  }

}
