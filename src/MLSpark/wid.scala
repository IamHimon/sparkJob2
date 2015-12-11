package MLSpark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2015/11/26.
 */
object wid {
  /*
      每个类别所含单词数量
   */
  def getWordNumByClass(sc:SparkContext): RDD[Int] ={
    val trainData = sc.textFile("G:\\MSGDATA\\rs/t.txt")
    trainData.map{line =>
      val kind = line.substring(0,1)
      val msg = line.substring(1,line.length)
      (kind,msg)
    }.reduceByKey(_+_).map(_._2.split(" ").length)
  }

  /*
      每个类别短信行数
   */
  def getTupleData(sc:SparkContext): RDD[(String,String)] ={
    val trainData = sc.textFile("G:\\MSGDATA\\rs/t.txt")
    trainData.map{line =>
      val kind = line.substring(0,1)
      val msg = line.substring(1,line.length)
      (kind,msg)
    }
  }


  def main(args: Array[String]) {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.apache.eclipse.jetty.server").setLevel(Level.OFF)
    val conf = new SparkConf().setMaster("local[4]").setAppName("www")
    val sc = new SparkContext(conf)
   // getTupleData(sc).reduceByKey(_+_).map(_._2.split(" ").length).foreach(println)
   // getTupleData(sc).reduceByKey(_+","+_).map(_._2.split(",").length).foreach(println)

    val testData = sc.textFile("G:\\MSGDATA\\rs/t2.txt")
    val data = testData.map{line =>
      val mid = line.substring(0,7)
      val msgWord = line.substring(7,line.length).split(" ").toList
      (mid, msgWord)
    }.map{line =>
      (line._1.trim -> line._2)
    }.collect().toMap
    println(data.get("800006"))
   // data.foreach(println)
    //trainData.foreach(println)
  }
}
