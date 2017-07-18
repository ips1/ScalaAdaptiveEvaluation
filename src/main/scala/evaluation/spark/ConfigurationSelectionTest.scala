package evaluation.spark

import java.io.PrintWriter

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SparkSession.Builder
import evaluation.spark.data.RecordDatasetGenerator
import evaluation.spark.queries.ReduceByKeyQuery

import scalaadaptive.api.Adaptive
import scalaadaptive.api.functions.InvocationToken
import scalaadaptive.api.options.Selection
import scalaadaptive.core.configuration.defaults.DefaultConfiguration

/**
  * Created by pk250187 on 6/6/17.
  *
  * The ScalaAdaptive test to choose between multiple SparkSQL adaptive configurations.
  *
  */
object ConfigurationSelectionTest {
  def createBaseSparkSession(): Builder =
    SparkSession
      .builder
      .master("local[1]")
      .appName("ConfigurationSelection Test")

  def createSparkSession(config: SparkAdaptiveConfiguration): SparkSession =
    createBaseSparkSession()
      .config("evaluation.spark.sql.adaptive.enabled", config.adaptiveEnabled)
      .config("evaluation.spark.sql.adaptive.shuffle.targetPostShuffleInputSize", config.targetPostShuffleInputSize)
      .config("evaluation.spark.sql.adaptive.minNumPostShufflePartitions", config.minNumPostShufflePartitions)
      .getOrCreate()

  def createSparkSession(): SparkSession =
    createBaseSparkSession()
      .config("evaluation.spark.sql.adaptive.enabled", true)
      .getOrCreate()

  def firstManualSettings(): SparkSession = {
    val config = new SparkAdaptiveConfiguration(true, 512 * 1024 * 1024, -1)
    createSparkSession(config)
  }

  def secondManualSettings(): SparkSession = {
    val config = new SparkAdaptiveConfiguration(true, 2 * 1024 * 1024, 200)
    createSparkSession(config)
  }

  def default(): SparkSession = {
    createSparkSession()
  }

  def disabled(): SparkSession = {
    val config = new SparkAdaptiveConfiguration(false, 512 * 1024 * 1024, -1)
    createSparkSession(config)
  }

  val dataGenerator = new RecordDatasetGenerator()

  def runTest(size: Int, spark: SparkSession, runCount: Int, token: InvocationToken): Unit = {
    val data = dataGenerator.generateData(size, spark)

    val query = new ReduceByKeyQuery(spark.sqlContext)

    for (i <- 1 to runCount) {
      val startTime = System.nanoTime
      val count = token(() => query.executeDataset(data))
      val endTime = System.nanoTime

      val duration = endTime-startTime
      println(s"Iteration $i: $duration ms")
    }
  }

  def main(args: Array[String]) {

    val size = if (args.length > 0) args(0).toInt else 10000

    Adaptive.initialize(new DefaultConfiguration)

    import scalaadaptive.api.Implicits._
    val createSession = firstManualSettings _ or secondManualSettings or disabled or default selectUsing Selection.MeanBased

    val testCount = 12
    val runCount = 10

    Seq.range(0, testCount).foreach(i => {
      val (spark, token) = createSession^()
      runTest(size, spark, runCount, token)
      spark.stop()
    })

    createSession.getAnalyticsData.foreach(d => d.saveData(new PrintWriter(System.out)))
  }
}
