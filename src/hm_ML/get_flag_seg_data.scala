package hm_ML

import java.nio.charset.Charset
import java.nio.file.{Paths, Files}
import java.util
import java.util.regex.Pattern

import org.ansj.splitWord.analysis.ToAnalysis
import org.ansj.util.FilterModifWord
import org.apache.spark.mllib.feature.{IDF, HashingTF}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.mutable.ListBuffer

/**
 * Created by HM on 2015/12/7.
 */
object get_flag_seg_data {




  //segmenter:input:String, output:List[String],filter chinese punctuation marks
  def ansjSegmenterToString(string: String,stopword:util.List[String]): String = {

//    val line = Files.readAllLines(Paths.get("dic/stopword.txt"), Charset.forName("utf-8"))
    FilterModifWord.insertStopWords(stopword)

    val list = ListBuffer[String]()
//    val result = ToAnalysis.parse(string.replaceAll("\\pP", ""))
    //filter stop words
    val result = FilterModifWord.modifResult(ToAnalysis.parse(string.replaceAll("\\pP", "")))
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



  //got stop word list
  def stopwordList(rDD: RDD[String]): util.List[String] = {
    val result =  new util.ArrayList[String]()
    rDD.collect().foreach(e =>{
      result.add(e)
    })
    return result
  }


  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("getFTData")//.setMaster("local")

    val sc = new SparkContext(conf)

    val rdd = sc.textFile("hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/training")
    //read out stopword.dic from HDFS
    val rdd2 = sc.textFile("hdfs://192.168.131.192:9000///user/humeng/MLWork/ML_files/stopword")

    val stopword = stopwordList(rdd2)

    val document = rdd.map(f => {
      val field = f.split("\t")
      field(1)+"\t"+ansjSegmenterToString(field(2),stopword)
    })

    document.repartition(1).saveAsTextFile("hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/flag-termList")


    sc.stop()

  }

}
