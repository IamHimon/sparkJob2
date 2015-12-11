package MLSpark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint

/**
 * Created by Administrator on 2015/11/22.
 */
object _TestBayes {
  def main(args: Array[String]) {


  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
  Logger.getLogger("org.apache.eclipse.jetty.server").setLevel(Level.OFF)
  val conf = new SparkConf().setMaster("local").setAppName("www")
  val sc = new SparkContext(conf)
  val data = sc.textFile("G://rd/bayes.txt")
  val parsedData = data.map { line =>
    val parts = line.split(',')
    LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
  }
  // Split data into training (60%) and test (40%).
  val splits = parsedData.randomSplit(Array(0.6, 0.4), seed = 11L)
  val training = splits(0)
  val test = splits(1)

  val model = NaiveBayes.train(training)

  val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
  val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()
  println(accuracy);
    predictionAndLabel.collect.foreach(println)
  // Save and load model
 // model.save(sc, "myModelPath")
 // val sameModel = NaiveBayesModel.load(sc, "myModelPath")
}
}