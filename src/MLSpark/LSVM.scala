package MLSpark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.classification.{LogisticRegressionWithLBFGS, SVMModel, SVMWithSGD}
import org.apache.spark.mllib.evaluation.{MulticlassMetrics, BinaryClassificationMetrics}
import org.apache.spark.mllib.util.MLUtils

object LSVM {
  def main(args: Array[String]) {


    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.apache.eclipse.jetty.server").setLevel(Level.OFF)
    val conf = new SparkConf().setAppName("TfIdfTest").setMaster("local")
    val sc = new SparkContext(conf)
    // Load training data in LIBSVM format.
    //(1.读取样本数据)
    val data = MLUtils.loadLibSVMFile(sc, "hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/SVM-train").repartition(20)

    // Split data into training (60%) and test (40%).
    //(2.样本数据划分训练样本与测试样本)
    val splits = data.randomSplit(Array(0.7, 0.3), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)

    val numTraining = training.count()
    val numTest = test.count()
    println("Training:"+ numTraining+"  test:"+numTest)



   //build SVM model,and set training parameter(3.新建SVM模型，并设置训练参数)
    val numIterations = 1000
    val stepSize = 1
    val miniBatchFraction = 1.0
    val model = SVMWithSGD.train(training,numIterations,stepSize,miniBatchFraction)

    //testing the semple(4.对测试样本进行测试)

    val prediction = model.predict(test.map(_.features))

    val predictionAndLabel = prediction.zip(test.map(_.label))

    //5.计算测试误差

    val metrics = new MulticlassMetrics(predictionAndLabel)
    val precision = metrics.precision
    println("Precision:"+precision)

















//    val numClasses = 2
//    val categoricalFeaturesInfo = Map[Int, Int]()
//    val impurity = "gini"
//    val maxDepth = 5
//    val maxBins = 32
//
//    val model = DecisionTree.trainClassifier(training, numClasses, categoricalFeaturesInfo,
//      impurity, maxDepth, maxBins)
//
//    // Evaluate model on test instances and compute test error
//    val labelAndPreds = test.map { point =>
//      val prediction = model.predict(point.features)
//      (point.label, prediction)
//    }
//
//    val testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / test.count()
//    println("Test Error = " + testErr)
//    println("Learned classification tree model:\n" + model.toDebugString)
//
//    //    test.foreach(println)
//
//    sc.parallelize(List(testErr)).saveAsTextFile("hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/SVM_result")


  }
}