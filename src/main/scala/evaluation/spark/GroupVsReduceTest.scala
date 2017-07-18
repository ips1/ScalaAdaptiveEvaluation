package evaluation.spark

import java.io.PrintWriter

import org.apache.spark.sql.SparkSession
import evaluation.spark.data.ParallelRddDataGenerator
import evaluation.spark.queries.GroupVsReduceQuery

import scalaadaptive.api.Adaptive
import scalaadaptive.core.configuration.defaults.DefaultConfiguration

/**
  * Created by pk250187 on 6/25/17.
  *
  * The Spark test for a simple GroupBy and ReduceBy test
  *
  * The environment should be set by changing the .master("local[1]")
  *
  * The combined query will be executed testCount times on the data of size either args[0] or dataSize. At the end of
  * the test, the analytics data of the combined query are printed out.
  *
  */
object GroupVsReduceTest {
  val dataSize = 5000
  val testCount = 1000

  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .master("local[1]")
      .appName("GroupVsReduce Test")
      .getOrCreate()

    val size = if (args.length > 0) args(0).toInt else dataSize

    Adaptive.initialize(new DefaultConfiguration)

    val data = new ParallelRddDataGenerator().generateData(size, spark)

    val query = new GroupVsReduceQuery

    for (idx <- 1 to testCount) {
      val startTime = System.nanoTime
      val count = query.query(data)
      val endTime = System.nanoTime

      val duration = endTime-startTime
      println(s"Iteration $idx: $duration ms")
    }

    query.query.getAnalyticsData.foreach(d => d.saveData(new PrintWriter(System.out)))

    spark.stop()
  }
}
