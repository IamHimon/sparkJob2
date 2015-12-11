package hm.readFiles

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ListBuffer

/**
 * Created by HM on 2015/10/9.
 */
object joinInSpark {


  def joinForMachine(rdd1: RDD[String], rdd2: RDD[String], resultPath: String): Unit = {

    val news = rdd1.map(line => {
      val fileds = line.split("::")
      (fileds(0), fileds(1)) //<newsPath,processedNews>
    })

    val listN = news.toArray()

    val companyNames = rdd2.map(line => {
      val fileds = line.split(",")
      (fileds(0), fileds(2)) //<companyID,simplifiedName>
    })


    val result2 = companyNames.map(f => {
      val list = new ListBuffer[String]()
      for (n <- listN) {
        if (n._2.contains(f._2)) {
          //          print("news:" + n._2 + "name:" + f._2)
          list.append(n._1)
        }
      }
      (f._1, list)
    }).filter(f => f._2.size != 0)

    result2.foreach(f => println(f))
    result2.saveAsTextFile(resultPath)

  }

  def joinForMan(rdd1: RDD[String], rdd2: RDD[String], resultPath: String): Unit = {

    val news = rdd1.map(line => {
      val fileds = line.split("::")
      (fileds(2), fileds(1)) //<completeNews,processedNews>
    })

    val listN = news.toArray()

    val companyNames = rdd2.map(line => {
      val fileds = line.split(",")
      (fileds(1), fileds(2)) //<completeName,simplifiedName>
    })


    val result = companyNames.map(f => {
      val list = new ListBuffer[String]()
      for (n <- listN) {
        if (n._2.contains(f._2)) {
          //          print("news:" + n._2 + "name:" + f._2)
          list.append(n._1)
        }
      }
      (f._1, list)
    }).filter(f => f._2.size != 0)

    result.foreach(f => println(f))
    result.saveAsTextFile(resultPath)
  }

  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("readNews")
      .set("spark.driver.maxResultSize", "4g")
    .set("spark.driver.memory","4g")
    val sc = new SparkContext(conf)
    val rdd1 = sc.textFile("hdfs://192.168.131.192:9000/user/humeng/ada/files/news2.txt")
    val rdd2 = sc.textFile("hdfs://192.168.131.192:9000/user/humeng/ada/files/lastResult.txt")

    //    joinForMan(rdd1,rdd2,"hdfs://192.168.131.192:9000/user/humeng/ada/files/joinResultForMan1.txt")
    //    joinForMachine(rdd1,rdd2,"hdfs://192.168.131.192:9000/user/humeng/ada/files/joinResultForMachine1.txt")

    joinForMan(rdd1, rdd2, "hdfs://192.168.131.192:9000/user/humeng/ada/files/joinResultForMan2.txt")
    joinForMachine(rdd1, rdd2, "hdfs://192.168.131.192:9000/user/humeng/ada/files/joinResultForMachine2.txt")


    sc.stop()
  }
}
