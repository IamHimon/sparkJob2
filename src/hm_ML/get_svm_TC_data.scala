package hm_ML

import java.util

import org.ansj.splitWord.analysis.ToAnalysis
import org.ansj.util.FilterModifWord
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ListBuffer

/**
 * Created by HM on 2015/12/8.
 */
object get_svm_TC_data {


  //input:String, output:List[String]
  def ansjSegmenterToList(string: String, stopword: util.List[String]): List[String] = {
    //    val line = Files.readAllLines(Paths.get("dic/stopword.txt"), Charset.forName("utf-8"))
    FilterModifWord.insertStopWords(stopword)

    val list = ListBuffer[String]()
    //filter stop words
    val result = FilterModifWord.modifResult(ToAnalysis.parse(string.replaceAll("\\pP", "")))
    var i = 0
    while (i < result.size()) {
      list.append(result.get(i).getName)
      i += 1
    }
    return list.toList
  }

  //segmenter:input:String, output:List[String],filter chinese punctuation marks
  def ansjSegmenterToString(string: String, stopword: util.List[String]): String = {

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

    val result = new util.ArrayList[String]()

    rDD.collect().foreach(e => {
      result.add(e)
    })
    return result
  }


  //build map[segWord,TF]
  def buildTF(rdd: RDD[String], stopword: util.List[String]): Map[String, Double] = {

    var wordTF = Map[String, Double]()
    var segNameList = List[String]()

    val compName = rdd.map(f => {
      val field = f.split("\t")
      val list = ansjSegmenterToList(field(2), stopword)
      (list)
    }).collect()
    segNameList = compName.reduce(_.:::(_))
    //compute the word's TF
    //保留小数点后6位小数
    def computeTF(word: String, list: List[String]): Double = {
      val TF = list.count(_.equals(word)).toDouble / list.size.toDouble
      return f"$TF%1.6f".toDouble
    }

    segNameList.foreach(f => {
      wordTF += (f -> computeTF(f, segNameList))
    })

    return wordTF
  }

  //huild map[segWord,TC]
  def buildTC(rdd: RDD[String], stopword: util.List[String]): Map[String, Int] = {
    var wordTC = Map[String, Int]()
    var segNameList = List[String]()

    val compName = rdd.map(f => {
      val field = f.split("\t")
      val list = ansjSegmenterToList(field(2), stopword)
      (list)
    }).collect()
    segNameList = compName.reduce(_.:::(_))

    segNameList.foreach(f => {
      wordTC += (f -> segNameList.count(_.equals(f)))
    })

    return wordTC

  }

  def getTCListPerMessage(string: String, map: Map[String, Int], stopword: util.List[String]): String = {
    val list = ansjSegmenterToList(string, stopword)
    var result = ""
    list.foreach(f =>{
      result = result+f+":"+map.get(f).get+" "
    })
    return result
  }


  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("getsvmTCdata").setMaster("local")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/training")//.repartition(20)
    //read out stopword.dic from HDFS
    val rdd2 = sc.textFile("hdfs://192.168.131.192:9000///user/humeng/MLWork/ML_files/stopword")
    val stopword = stopwordList(rdd2)

    val message = rdd.map(f => {
      val field = f.split("\t")
      val list = ansjSegmenterToList(field(2), stopword)
      list
    })

    val segTermList = message.reduce(_.:::(_))//.foreach(println _)

//    println(segTermList.size)


    val map = segTermList.map(f => {
      (f,segTermList.count(_.equals(f)))
    })
    val result = sc.makeRDD(map)
    result.repartition(1).saveAsTextFile("hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/TC_data")


//    map.foreach(println _)



//    segTermList.foreach(println _)


//    //huild wordTC[segTerm,termCount]
//    val wordTC = buildTC(rdd, stopword)//.foreach(println _)
//
//    val svm = rdd.map(f => {
//      val field = f.split("\t")
//      field(1) + " " + getTCListPerMessage(field(2),wordTC,stopword)
//    })//.foreach(println _)
//
//    svm.repartition(1).saveAsTextFile("hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/svm_TC_data")





    sc.stop()

  }

}
