package hm_ML

import org.apache.spark.mllib.feature.{IDF, HashingTF}
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.linalg.Vector

/**
 * Created by HM on 2015/12/7.
 */
object getTFIDF {
  def main(args: Array[String]) {

    val conf = new SparkConf().setAppName("getTFIDF").setMaster("local")
    val sc = new SparkContext(conf)

    //load documents(one per line)
    val documents: RDD[Seq[String]] = sc.textFile("D:\\MLWork\\data\\normal-seg-result.txt").map(_.split(" ").toSeq)

    val hashingTF = new HashingTF()
    val tf: RDD[Vector] = hashingTF.transform(documents)

    tf.cache()
    val idf = new IDF().fit(tf)
    val tfidf: RDD[Vector] = idf.transform(tf)

    tfidf.foreach { e =>
      println(e)
      println("newsIndex:"+e.apply(0)+"  termIndex"+e.apply(1)+"  TFIDF:"+e.apply(2))

    }
    //    tfidf.saveAsTextFile("hdfs://192.168.131.192:9000//user/humeng/MLWork/tfidf_normal-seg-result")



    sc.stop()

  }

}
