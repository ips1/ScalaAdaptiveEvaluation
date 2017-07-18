package evaluation.strategycomparison

import commonutils.{Sorter, TestConfiguration}

import scala.util.Random
import scalaadaptive.api.Adaptive
import scalaadaptive.api.functions.AdaptiveFunction1
import scalaadaptive.api.options.Selection.Selection
import scalaadaptive.api.options.{Selection, Storage}
import scalaadaptive.core.configuration.Configuration
import scalaadaptive.core.configuration.blocks.history.{CachedRegressionHistory, CachedStatisticsHistory}
import scalaadaptive.core.configuration.blocks.selection._

/**
  * Created by Petr Kubat on 7/1/17.
  *
  * Overhead measurement test. Uses combined sorting algorithm to run on a sequence of runCount arrays of 0 - maxDataSize
  * elements.
  *
  * The output (written to standard output) is in CSV format, multiple tables are separated by an empty column.
  * There is one table for each configuration and its rows have the following format:
  * s"${r._1}, ${r._2.functionTime}, ${r._2.selectOverhead}, ${r._2.policyOverhead}"
  *
  * where _1 is the sequence number of the run. There is one row for each sampleFrequency runs. The time and overhead
  * values are from one run, nothing is aggregated.
  *
  */
object OverheadMeasure {
  val runCount = 20000
  val maxDataSize = 2000
  val sampleFrequency = 50

  class RunMeasure(val totalTime: Long, val functionTime: Long, val selectOverhead: Long) {
    val totalOverhead: Long = totalTime - functionTime
    val policyOverhead: Long = totalOverhead - selectOverhead
  }

  private def measureExecTime(fun: () => Unit): Long = {
    val start = System.nanoTime
    fun()
    System.nanoTime - start
  }

  private def performTestStep(input: Array[Int], sort: AdaptiveFunction1[Array[Int], Array[Int]]): RunMeasure = {
    val externalTime = measureExecTime(() => sort(input))
    val lastRecord = sort.getAnalyticsData.get.getAllRunInfo.last
    new RunMeasure(externalTime, lastRecord.runTime, lastRecord.overheadTime)
  }

  def runTest(conf: (Configuration, Selection)): (Seq[String], Double) = {
    Adaptive.initialize(conf._1)
    import scalaadaptive.api.Implicits._
    val sorter = new Sorter()
    val customSort = sorter.quickSort _ or sorter.selectionSort by (_.length) storeUsing Storage.Global selectUsing conf._2

    val inputs = Seq.range(0, runCount).map(i => {
      val size = Random.nextInt(maxDataSize)
      (i, Seq.fill(size)(Random.nextInt).toArray)
    })

    val results = inputs.map(in => (in._1, performTestStep(in._2, customSort)))

    val toPrint = results
      .filter(r => r._1 % sampleFrequency == 0)
      .map(r => s"${r._1}, ${r._2.functionTime}, ${r._2.selectOverhead}, ${r._2.policyOverhead}")



    val totalOverhead = results.map(_._2.selectOverhead).sum
    val avgOverhead = (totalOverhead.toDouble / results.size) / (1000 * 1000)

    (toPrint, avgOverhead)
  }

  def main(args: Array[String]): Unit = {
    val configs = List(
      (new TestConfiguration
      with TTestMeanBasedStrategy
      with CachedStatisticsHistory, Selection.MeanBased),
      (new TestConfiguration
        with TTestMeanBasedStrategy, Selection.MeanBased),
      (new TestConfiguration
        with UTestMeanBasedStrategy, Selection.MeanBased),
      (new TestConfiguration
        with LinearRegressionInputBasedStrategy
        with CachedRegressionHistory, Selection.InputBased),
      (new TestConfiguration
        with LinearRegressionInputBasedStrategy, Selection.InputBased),
      (new TestConfiguration
        with WindowBoundRegressionInputBasedStrategy, Selection.InputBased),
      (new TestConfiguration
        with WindowBoundTTestInputBasedStrategy, Selection.InputBased),
      (new TestConfiguration
        with LoessInterpolationInputBasedStrategy, Selection.InputBased)
    )

    val results = configs.map(cfg => runTest(cfg))

    val strings = results.map(_._1).toArray

    val recordCount = strings.head.size

    Seq.range(0, recordCount).foreach(i => {
      val lineParts = strings.map(s => s(i))
      val line = lineParts.mkString(",,")
      println(line)
    })

    println()

    val avgs = results.map(_._2.toString).mkString(",")
    println(avgs)
  }
}
