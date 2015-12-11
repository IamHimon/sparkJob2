package MLSpark

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.tree.model.DecisionTreeModel
import org.apache.spark.mllib.util.MLUtils

object libSVMClassifier {
  def main(args: Array[String]) {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.apache.eclipse.jetty.server").setLevel(Level.OFF)
    val conf = new SparkConf().setMaster("local").setAppName("www")
    val sc = new SparkContext(conf)
    // Load and parse the data file.
    val trainData = MLUtils.loadLibSVMFile(sc, "G://MSGDATA/svm/train.txt")
    val testData = MLUtils.loadLibSVMFile(sc, "G://MSGDATA/svm/test.txt")

    // Train a DecisionTree model.
    //  Empty categoricalFeaturesInfo indicates all features are continuous.
    val numClasses = 2
    val categoricalFeaturesInfo = Map[Int, Int]()
    val impurity = "gini"
    val maxDepth = 5
    val maxBins = 32

    val model = DecisionTree.trainClassifier(trainData, numClasses, categoricalFeaturesInfo,
      impurity, maxDepth, maxBins)

    // Evaluate model on test instances and compute test error
    val labelAndPreds = testData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }

    trainData.foreach(println)
    //labelAndPreds.foreach(println)

    //println("Learned classification tree model:\n" + model.toDebugString)

    // Save and load model
   // model.save(sc, "myModelPath")
   // val sameModel = DecisionTreeModel.load(sc, "myModelPath")
  }
}
