package ansjTest

import java.util.regex.Pattern

import org.ansj.splitWord.analysis.ToAnalysis
import org.apache.spark.{SparkContext, SparkConf}

import scala.collection.mutable.ListBuffer

/**
 * Created by HM on 2015/12/5.
 */
object ansjInSpark {

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
  def ansjSegmenter(str:String):List[String]={
    val list = ListBuffer[String]()
    val result = ToAnalysis.parse(str)
    var i = 0
    while(i < result.size()){
      list.append(result.get(i).getName)
      i += 1
    }
    return list.toList
  }


  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("IIJoin_StanfordSeg").setMaster("local")
    val sc = new SparkContext(conf)

    val rdd1 = sc.textFile("hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/training")
    val rdd2 = sc.textFile("hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/testing")
    val rdd3 = sc.textFile("hdfs://192.168.131.192:9000/user/humeng/project1/companyNamesTest3")

    val result1 = rdd1.map(f => {
      val field = f.split("\t")
      if(field.length ==3){
        (field(0),field(1),ansjSegmenter(field(2)))
      }else{
        (field(0),field(1),"k")
      }
    }).foreach(println _)


//    val result2 = rdd2.map(f => {
//      val field = f.split("\t")
//      if(field.length ==2){
//        (field(0),ansjSegmenter(field(1)))
//      }else{
//        (field(0),"k")
//      }
//    }).foreach(println _)




    sc.stop()

  }

}
