package MLSpark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.feature.HashingTF
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.{SparkContext, SparkConf}

/**
 * Created by Administrator on 2015/11/15.
 */
object TestBayes {
def main(args:Array[String]): Unit ={
  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  Logger.getLogger("org.apache.eclipse.jetty.server").setLevel(Level.OFF)
 val conf = new SparkConf().setMaster("local").setAppName("www")
  val sc = new SparkContext(conf)
  val hashingTF = new HashingTF()
  val trainData = sc.textFile("G://MSGDATA/rs/train.txt")
  val testData = sc.textFile("G://MSGDATA/rs/tesPythont.txt")

  val parsedTrainData = trainData.map { line =>
    val parts = line.substring(0,1)
    LabeledPoint(parts.toDouble, hashingTF.transform(line.substring(1,line.length).split(" ")))
  }

 val parsedTestData = testData.map { line =>
  val parts = line.substring(0,7)
  LabeledPoint(parts.toDouble, hashingTF.transform(line.substring(7,line.length).trim.split(" ")))
 }
 //parsedTestData.foreach(println)
  // Split data into training (60%) and test (40%).
//  val splits = parsedData.randomSplit(Array(0.6, 0.4), seed = 11L)
//  val training = splits(0)
//  val test = splits(1)


    val model = NaiveBayes.train(parsedTrainData)
   val predictionAndLabel = parsedTestData.map(p => (model.predict(p.features), p.label))
   predictionAndLabel.saveAsTextFile("hdfs://192.168.131.192:9000/user/liuguangfu/data/msg7")



//  val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()
//  println("accuracy==>>>"+accuracy)
   // model.save(sc, "myModelPath")
  //val sameModel = NaiveBayesModel.load(sc, "myModelPath")

//  val model = NaiveBayes.train(training, lambda = 1.0, modelType = "multinomial")
//
//  val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
//  val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()
//
//  // Save and load model
//  model.save(sc, "myModelPath")
//  val sameModel = NaiveBayesModel.load(sc, "myModelPath")


 sc.stop()
}
}
