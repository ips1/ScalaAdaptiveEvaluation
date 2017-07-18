package evaluation.strategycomparison

import commonutils.TestConfiguration
import evaluation.strategycomparison.support.{Methods, TestRunResult}

import scala.util.Random
import scalaadaptive.api.Adaptive
import scalaadaptive.api.options.Selection
import scalaadaptive.core.configuration.blocks.selection.{LinearRegressionInputBasedStrategy, LoessInterpolationInputBasedStrategy, WindowBoundRegressionInputBasedStrategy, WindowBoundTTestInputBasedStrategy}
import scalaadaptive.core.functions.identifiers.ClosureIdentifier

/**
  * Created by Petr Kubat on 6/24/17.
  */
object InputBasedStrategyComparison {
  val methods = new Methods

  def performTest(testData: Iterable[Int], slowerBy: Double): TestRunResult = {
    val slowerFun = methods.createSlowerLinear(slowerBy)
    val normalFun = methods.createNormalLinear

    val slowerName = slowerFun.getClass.getTypeName

    import scalaadaptive.api.Implicits._
    val fun = slowerFun or normalFun by (i => i) selectUsing Selection.InputBased

    testData.foreach(n => {
      val startTime = System.nanoTime
      val res = fun(n)
      val duration = System.nanoTime - startTime
    })

    val wrongSelected = fun.getAnalyticsData.get.getAllRunInfo.count(r => r.selectedFunction match {
      case ClosureIdentifier(name) => name == slowerName
      case _ => false
    })

    new TestRunResult(fun.getAnalyticsData.get, wrongSelected)
  }

  def main(args: Array[String]): Unit = {
    val configs = List(
      new TestConfiguration
        with WindowBoundTTestInputBasedStrategy {
        override val alpha: Double = 0.05
      },
      new TestConfiguration
        with LinearRegressionInputBasedStrategy {
        override val alpha: Double = 0.05
      },
      new TestConfiguration
        with WindowBoundRegressionInputBasedStrategy {
        override val alpha: Double = 0.05
      },
      new TestConfiguration
        with WindowBoundTTestInputBasedStrategy {
        override val alpha: Double = 0.25
      },
      new TestConfiguration
        with LinearRegressionInputBasedStrategy {
        override val alpha: Double = 0.25
      },
      new TestConfiguration
        with WindowBoundRegressionInputBasedStrategy {
        override val alpha: Double = 0.25
      },
      new TestConfiguration
        with WindowBoundTTestInputBasedStrategy {
        override val alpha: Double = 1
      },
      new TestConfiguration
        with LinearRegressionInputBasedStrategy {
        override val alpha: Double = 1
      },
      new TestConfiguration
        with WindowBoundRegressionInputBasedStrategy {
        override val alpha: Double = 1
      },
      new TestConfiguration
        with LoessInterpolationInputBasedStrategy
    )

    val errorFactors = List(0.01, 0.1, 0.2, 0.5, 1.0, 3.0)
    //val errorFactors = List(0.5, 1.0, 3.0)

    val testCount = 100
    val runCount = 200
    val minVal = 100000
    val maxVal = 500000

    val inputs = Seq.fill(testCount)(Seq.fill(runCount)(Random.nextInt(maxVal - minVal) + minVal).toList)

    val resByError = errorFactors.map(err => {
      println(s"--- NEW ERROR: $err ---")
      val resByError = configs.map(cfg => {
        println("--- NEW CFG ---")
        val results = inputs.map(data => {
          Adaptive.initialize(cfg)
          val res = performTest(data, err)
          println(res.wrongSelected)
          res
        })

        results.map(_.wrongSelected)
      })
      resByError
    })

    Seq.range(0, testCount).foreach(i => {
      val lineParts = resByError.map(byErr => {
        val innerResults = byErr.map(l => l(i))
        innerResults.mkString(",")
      })
      val line = lineParts.mkString(",,")
      println(line)
    })
  }
}
