package spark

import java.io.PrintWriter

import org.apache.spark.sql.SparkSession
import spark.apiadaptors.{DataHolder, DatasetData}
import spark.data.{DataProvider, Record}
import spark.queries.{GroupPairsByIdQuery, MultipleRecordsGroupingQuery, Query}

import scalaadaptive.api.Adaptive
import scalaadaptive.core.configuration.defaults.DefaultConfiguration

/**
  * Created by Petr Kubat on 7/18/17.
  */
class RddVsDatasetTestCommons[TDataType] {
  def createSparkSession(): SparkSession =
    SparkSession
      .builder
      .master("local[8]")
      .appName("RddVsDatasetTest Test")
      .config("spark.sql.adaptive.enabled", value = true)
      .getOrCreate()

  def runTest(data: DataHolder[TDataType], query: Query[TDataType], spark: SparkSession, iterationNo: Int): Unit = {
    val startTime = System.nanoTime
    val count = query.query(data)
    val endTime = System.nanoTime

    val duration = endTime - startTime
    println(s"Iteration $iterationNo: $duration ms")
  }

  def execute(args: Array[String], dataProvider: DataProvider[TDataType], queryProvider: (SparkSession) => Query[TDataType]): Unit = {
    val spark = createSparkSession()
    val size = if (args.length > 0) args(0).toInt else 200000

    Adaptive.initialize(new DefaultConfiguration)
    val selector = new GroupPairsByIdQuery

    val testCount = 100
    val data = dataProvider.generateData(size, spark)
    val query = queryProvider(spark)

    Seq.range(0, testCount).foreach(i => {
      runTest(data, query, spark, i)
    })

    spark.stop()

    import scalaadaptive.api.Implicits._
    query.query.getAnalyticsData.foreach(d => d.saveData(new PrintWriter(System.out)))
  }
}
