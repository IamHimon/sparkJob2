package hm_ML


import java.util

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
object get_svm_data_spark {

  //got stop word list
  def stopwordList(rDD: RDD[String]): util.List[String] = {

    val result = new util.ArrayList[String]()

    rDD.collect().foreach(e => {
      result.add(e)
    })
    return result
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

  /*
* transform 1048576,[96163,650202,693856],[8.242560621923047,12.899221076089338,7.330876572328241]
*into  96163:8.242560621923047 650202:12.899221076089338 693856:7.330876572328241
*
* */
  def tfidfToSVM(string: String): String = {
    val str = string.substring(8, string.length)
    val array = str.split("],")
    val a1: String = array(0).replaceAll("\\[|\\]", "")
    val a2: String = array(1).replaceAll("\\[|\\]", "")
    val arr1: Array[String] = a1.split(",")
    val arr2: Array[String] = a2.split(",")
    var svm: String = ""
    var i = 0
    for (i <- 0 to arr1.size - 1) {
      svm = svm + arr1(i) + ":" + arr2(i) + " "
    }
    return svm
  }


  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("getSVMData").setMaster("local")

    val sc = new SparkContext(conf)

    val rdd = sc.textFile("hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/training")
    //read out stopword.dic from HDFS
    val rdd2 = sc.textFile("hdfs://192.168.131.192:9000///user/humeng/MLWork/ML_files/stopword")
    val stopword = stopwordList(rdd2)

    val document = rdd.map(f => {
      val field = f.split("\t")
      (field(0), field(1), field(2))
    })

    //(messageID,flag)
    val flag = document.map(f => {
      (f._1, f._2)
    }).collect().toMap
    //(messageID,segTermListString)
    val flag_seg = document.map(f => {
      (f._1,ansjSegmenterToString(f._3, stopword))
    }) //.foreach(println _)

    //    flag_seg.repartition(1).saveAsTextFile("hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/ansj_flag_seg_result")

    //getTFIDF,load documents(one per line)


    val documents: RDD[Seq[String]] = flag_seg.map(f => {
      val field = f._2.split(" ")
      field.toSeq
      //      (f._1,field.toSeq)
    })

    val hashingTF = new HashingTF()
    val tf: RDD[Vector] = hashingTF.transform(documents)

    tf.cache()
    val idf = new IDF().fit(tf)
    val tfidf: RDD[Vector] = idf.transform(tf)


    val result1 = tfidf.map(f =>{
      val temp = f.toString
      tfidfToSVM(temp.substring(1,temp.size-2))
    }).foreach(println _)

//    var i = 0
//    val result = tfidf.map(f => {
//      val temp = f.toString
//      i = i + 1
//      flag.get(i.toString).get + " " + tfidfToSVM(temp.substring(1, temp.size - 2))
//    }).foreach(println _)
//    result.repartition(1).saveAsTextFile("hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/svm_data")


    sc.stop()

  }


}
