package evaluation.sorting

import commonutils.{Sorter, TestConfiguration, TestSetup}

import scala.util.Random
import scalaadaptive.api.Adaptive
import scalaadaptive.api.grouping.GroupId
import scalaadaptive.api.options.Selection
import scalaadaptive.api.policies.PauseSelectionAfterStreakPolicy
import scalaadaptive.core.configuration.blocks.history.{CachedGroupHistory, CachedRegressionHistory, CachedStatisticsHistory}
import scalaadaptive.core.configuration.blocks.selection.{LinearRegressionInputBasedStrategy, LoessInterpolationInputBasedStrategy, WindowBoundRegressionInputBasedStrategy, WindowBoundTTestInputBasedStrategy}

/**
  * Created by Petr Kubat on 7/3/17.
  *
  * The sorting test. Combines quick sort and selection sort algorithms.
  *
  * Generates a sequence of runCount arrays with 0 - maxDataSize elements. Then, for each configuration
  * specified, sorts all the sequences using both simple sorting methods and the combined one.
  *
  * The outputs for each configuration are the run times in the following format:
  * s"${res.inputSize}, ${res.combinedTime}, ${res.quickTime}, ${res.selectionTime}"
  *
  * sorted by inputSize for simpler plotting.
  *
  */
object SortingTest {
  val runCount = 500
  val maxDataSize = 5000
  val sorter = new Sorter

  private def measureExecTime(fun: () => Unit): Long = {
    val start = System.nanoTime
    fun()
    System.nanoTime - start
  }

  class Result(val inputSize: Int,
               val combinedTime: Long,
               val quickTime: Long,
               val selectionTime: Long)


  private def run(data: Seq[(Int, Array[Int])]): Seq[Result] = {
    import scalaadaptive.api.Implicits._

    val customSort = (
      sorter.quickSort _ or sorter.selectionSort
        by (_.length) groupBy (d => GroupId(Math.log(d.length.toDouble).toInt))
        selectUsing Selection.InputBased
        withPolicy new PauseSelectionAfterStreakPolicy(20, 20)
      )

    data.map(in => {
      val combinedTime = measureExecTime(() => customSort(in._2))
      val quickTime = measureExecTime(() => sorter.quickSort(in._2))
      val selectionTime = measureExecTime(() => sorter.selectionSort(in._2))
      val lastRecord = customSort.getAnalyticsData.get.getAllRunInfo.last
      new Result(in._2.length, combinedTime, quickTime, selectionTime)
    })
  }

  private def runTests(setups: Seq[TestSetup], inputs: Seq[(Int, Array[Int])]): Unit = {
    val results = setups.map(s => {
      Adaptive.initialize(s.config)
      val result = run(inputs)
      (s.name, result)
    })

    results.foreach(res => {
      println(s"Results of ${res._1}")
      res._2.sortBy(_.inputSize).foreach(printResult)
    })

    results.foreach(res => {
      println(s"Sum of ${res._1}")
      printSum(res._2)
    })
  }

  private def printResult(res: Result) =
    println(s"${res.inputSize}, ${res.combinedTime}, ${res.quickTime}, ${res.selectionTime}")

  private def printSum(res: Seq[Result]) =
    println(s"${res.map(_.combinedTime).sum}, ${res.map(_.quickTime).sum}, ${res.map(_.selectionTime).sum}")

  def main(args: Array[String]): Unit = {

    val inputs = Seq.range(0, runCount).map(i => {
      val size = Random.nextInt(maxDataSize)
      (i, Seq.fill(size)(Random.nextInt).toArray)
    })

    val setups = List(
      new TestSetup("LOESS", new TestConfiguration
        with LoessInterpolationInputBasedStrategy
        with CachedGroupHistory),
      new TestSetup("LR", new TestConfiguration
        with LinearRegressionInputBasedStrategy
        with CachedRegressionHistory),
      new TestSetup("WBLR", new TestConfiguration
        with WindowBoundRegressionInputBasedStrategy
        with CachedRegressionHistory),
      new TestSetup("WBTT", new TestConfiguration
        with WindowBoundTTestInputBasedStrategy
        with CachedStatisticsHistory)
    )


    // Dummy run to fill caches etc.
    runTests(setups, inputs)

    // Real run
    runTests(setups, inputs)
  }
}
