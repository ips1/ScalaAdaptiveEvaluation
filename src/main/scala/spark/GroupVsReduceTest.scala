package spark

import java.io.PrintWriter

import org.apache.spark.sql.SparkSession
import spark.data.ParallelRddDataGenerator
import spark.queries.GroupVsReduceQuery

import scalaadaptive.api.Adaptive
import scalaadaptive.core.configuration.defaults.DefaultConfiguration

/**
  * Created by pk250187 on 6/25/17.
  */
object GroupVsReduceTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .master("local[8]")
      .appName("GroupVsReduce Test")
      .getOrCreate()

    val size = if (args.length > 0) args(0).toInt else 5000

    Adaptive.initialize(new DefaultConfiguration)

    val data = new ParallelRddDataGenerator().generateData(size, spark)

    val query = new GroupVsReduceQuery

    val testCount = 1000

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
