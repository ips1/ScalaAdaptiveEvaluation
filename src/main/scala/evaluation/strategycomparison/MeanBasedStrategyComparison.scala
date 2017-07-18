package evaluation.strategycomparison

import commonutils.TestConfiguration
import evaluation.strategycomparison.support.{Methods, TestRunResult}

import scalaadaptive.api.Adaptive
import scalaadaptive.api.options.Selection
import scalaadaptive.core.configuration.blocks.selection.{TTestMeanBasedStrategy, UTestMeanBasedStrategy}
import scalaadaptive.core.functions.identifiers.ClosureIdentifier

/**
  * Created by Petr Kubat on 6/24/17.
  */
object MeanBasedStrategyComparison {
  val methods = new Methods

  def performTest(runCount: Int, arg: Int, slowerBy: Double): TestRunResult = {
    val slowerHelper = methods.createSlowerLinear(slowerBy)
    val normalHelper = methods.createNormalLinear

    val slowerFun = () => slowerHelper(arg)
    val normalFun = () => normalHelper(arg)

    val slowerName = slowerFun.getClass.getTypeName

    import scalaadaptive.api.Implicits._
    val fun = slowerFun or normalFun selectUsing Selection.MeanBased

    Seq.range(0, runCount).foreach(i => {
      val startTime = System.nanoTime
      val res = fun()
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
        with TTestMeanBasedStrategy {
        override val alpha: Double = 0.05
      },
      new TestConfiguration
        with TTestMeanBasedStrategy {
        override val alpha: Double = 0.25
      },
      new TestConfiguration
        with UTestMeanBasedStrategy {
        override val alpha: Double = 0.05
      },
      new TestConfiguration
        with UTestMeanBasedStrategy {
        override val alpha: Double = 0.25
      }
    )

    val errorFactors = List(0.01, 0.1, 0.2, 0.5, 1.0)

    val testCount = 10
    val runCount = 200
    val arg = 200000

    val resByError = errorFactors.map(err => {
      println(s"--- NEW ERROR: $err ---")
      val resByError = configs.map(cfg => {
        println("--- NEW CFG ---")
        val results = Seq.range(0, testCount).map(i => {
          Adaptive.initialize(cfg)
          val res = performTest(runCount, arg, err)
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
