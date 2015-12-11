package hm_ML

import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.classification.SVMWithSGD
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkConf, SparkContext}

object runSVM {
  def main(args: Array[String]) {


    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.apache.eclipse.jetty.server").setLevel(Level.OFF)
    val conf = new SparkConf().setAppName("TfIdfTest").setMaster("local")
    val sc = new SparkContext(conf)


    // Load training data in LIBSVM format.
    //(1.读取样本数据)
    val data = MLUtils.loadLibSVMFile(sc, "hdfs://192.168.131.192:9000//user/humeng/MLWork/ML_files/flag_termlist_data").repartition(20)
    // Split data into training (60%) and test (40%).
    //(2.样本数据划分训练样本与测试样本)
    val splits = data.randomSplit(Array(0.7, 0.3), seed = 11L)
    val training = splits(0).cache()
    val test = splits(1)
    val numTraining = training.count()
    val numTest = test.count()
    println("Training:"+ numTraining+"  test:"+numTest)
   //build SVM model,and set training parameter(3.新建SVM模型，并设置训练参数)
    val numIterations = 1000 //迭代次数，默认为100
    val stepSize = 1  //迭代步长，默认为1.0
    val miniBatchFraction = 1.0 //每次迭代参与计算的样本比例，默认为1.0
//    val initialWeights = 0 //初始权重，默认为0向量
    val model = SVMWithSGD.train(training,numIterations,stepSize,miniBatchFraction)
    //testing the semple(4.对测试样本进行测试)
    val prediction = model.predict(test.map(_.features))
    val predictionAndLabel = prediction.zip(test.map(_.label))
    //5.计算测试误差
    val metrics = new MulticlassMetrics(predictionAndLabel)
    val precision = metrics.precision
    println("Precision:"+precision)

  }
}