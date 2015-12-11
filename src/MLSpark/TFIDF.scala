package MLSpark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.mllib.feature.IDF
object TFIDF {


  def main(args: Array[String]) {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.apache.eclipse.jetty.server").setLevel(Level.OFF)
    val conf = new SparkConf().setAppName("TfIdfTest").setMaster("local")
    val sc = new SparkContext(conf)

    // Load documents (one per line).
    //val documents: RDD[Seq[String]] = sc.textFile("G://edge.txt").map(_.split(" ").toSeq)

   // val testData = sc.textFile("G://MSGDATA/rs/test.txt")
   val testData = MLUtils.loadLibSVMFile(sc, "G://MSGDATA/svm/test.txt")
    val trainData = sc.textFile("G://MSGDATA/rs/t.txt")

    //val doc: RDD[Seq[String]] = sc.textFile("G://edge.txt").map(_.split(" ").toSeq) //获取rdd
    val doc= sc.textFile("G://edge.txt")

//    val tfRdd = trainData.map { line =>
//      val kind = line.substring(0,1)
//      val wordList = line.substring(1,line.length).split("\\s+").toSeq
//      Vector(kind,wordList)
//    }
   // tfRdd.foreach(println)
    //val tfRdd: RDD[Seq[String]] = sc.textFile("G://MSGDATA/rs/t2.txt").map(e =>e.substring(1,e.length).split(" ").toSeq) //获取rdd
   val tfRdd = sc.textFile("G://MSGDATA/rs/t2.txt") //获取rdd
    val hashingTF = new HashingTF()
   // tfRdd.foreach(println)
    val data = tfRdd.map{line =>
      val field = line.split(" ")
     // LabeledPoint(field.head.toDouble,hashingTF.transform(field.tail))
     (field.head.toDouble,hashingTF.transform(field.tail))
    }
   // data.foreach(println)
    //println(data.values)

//    val tf :RDD[Vector]= hashingTF.transform(tfRdd)
//    tf.cache()
//    tf.foreach(println)
      val idf = new IDF().fit(data.values)

    //将tf向量转换成tf-idf向量
    val num_idf_pairs = data.mapValues(v => idf.transform(v))
    num_idf_pairs.foreach(println)
    val parseData = num_idf_pairs.map{line =>
      LabeledPoint(line._1,line._2)

    }
   // val rs= idf.transform(data.values)
    //  rs.foreach(println)
    //rs.map(line => line.toString.substring(9,line.toString.length)).saveAsTextFile("hdfs://192.168.131.192:9000/user/liuguangfu/data/libsvm2")

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


    val model = DecisionTree.trainClassifier(parseData, numClasses, categoricalFeaturesInfo,
         impurity, maxDepth, maxBins)

    val labelAndPreds = testData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }

    labelAndPreds.foreach(println)
  }
  println("over")
}
