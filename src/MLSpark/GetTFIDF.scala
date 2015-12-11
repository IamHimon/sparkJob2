package MLSpark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.feature.{HashingTF, IDF}
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

object GetTFIDF {


  def main(args: Array[String]) {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.apache.eclipse.jetty.server").setLevel(Level.OFF)
    val conf = new SparkConf().setAppName("TfIdfTest").setMaster("local")
    val sc = new SparkContext(conf)

    // Load documents (one per line).
    //val documents: RDD[Seq[String]] = sc.textFile("G://edge.txt").map(_.split(" ").toSeq)

    val testData = sc.textFile("G://MSGDATA/rs/test.txt")
    val trainRdd = sc.textFile("G://MSGDATA/rs/train.txt")



    //val doc: RDD[Seq[String]] = sc.textFile("G://edge.txt").map(_.split(" ").toSeq) //获取rdd
    val doc= sc.textFile("G://edge.txt")

    val tfRdd = trainRdd.map(e =>e.substring(1,e.length).split("\\s+").toSeq)
    //tfRdd.foreach(println)
    val hashingTF = new HashingTF()
    val tf :RDD[Vector]= hashingTF.transform(tfRdd)
    tf.cache()
    val idf = new IDF().fit(tf)
    //val rs= idf.transform(tf).collect
    val rs= idf.transform(tf).saveAsTextFile("hdfs://192.168.131.192:9000/user/liuguangfu/data/libvm1")

//
//    rs.foreach{e =>
//      println(e)
//      e.toString.split(",").foreach(println)
//
//    }
//    val parseData :RDD[LabeledPoint]= trainData.map{line =>
//      LabeledPoint(line.substring(0,1).toDouble,rs.collect().toVector)
//    }
    val numClasses = 2
    val categoricalFeaturesInfo = Map[Int, Int]()
    val impurity = "gini"
    val maxDepth = 5
    val maxBins = 32
    println("over1111")
   // parseData.foreach(println)
//    val model = DecisionTree.trainClassifier(parseData, numClasses, categoricalFeaturesInfo,
//      impurity, maxDepth, maxBins)
  }
  println("over")
}
