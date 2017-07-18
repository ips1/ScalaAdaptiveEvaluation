package tests.meanbasedtest

import tests.TestMethods

import scalaadaptive.api.Adaptive
import scalaadaptive.api.options.Selection
import scalaadaptive.core.configuration.blocks.history.CachedStatisticsHistory
import scalaadaptive.core.configuration.defaults.DefaultConfiguration

/**
  * Created by Petr Kubat on 6/6/17.
  */
object TTestShouldSelect2 {
  def main(args: Array[String]): Unit = {
    val methods = new TestMethods

    Adaptive.initialize(new DefaultConfiguration with CachedStatisticsHistory)

    import scalaadaptive.api.Implicits._
    val function = methods.fastMethod _ or methods.slowMethod or methods.anotherSlowMethod selectUsing Selection.MeanBased

    val testRuns = 200
    Seq.range(0, testRuns).foreach(i => {
      println("Running:")
      function(List(1))
    })
  }
}
