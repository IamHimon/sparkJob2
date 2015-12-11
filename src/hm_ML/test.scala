package hm_ML

import org.apache.spark.{SparkContext, SparkConf}


/**
 * Created by HM on 2015/12/8.
 */
object test {

  def test1: Unit ={
    val map = Map("a"->1,"b"->2,"c"->3)
    val str = "abc"
    val list = List("a","b","c")

    var result = ""

    list.foreach(e => {
      result = result+e+":"+map.get(e).get+" "
    })

    println(result)
  }

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("test").setMaster("local")
    val sc = new SparkContext(conf)

    val rdd = sc.textFile("hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/flag_termlist_data")

    rdd.foreach(println _)

    sc.stop()

  }

}
