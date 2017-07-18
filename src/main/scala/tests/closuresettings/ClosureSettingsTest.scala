package tests.closuresettings

import java.time.Duration

import tests.{TestMethods, TestRunner}

import scalaadaptive.core.configuration.defaults.DefaultConfiguration
import scalaadaptive.api.Adaptive
import scalaadaptive.core.configuration.blocks.history.CachedStatisticsHistory

/**
  * Created by Petr Kubat on 5/8/17.
  */
object ClosureSettingsTest {
  import scalaadaptive.api.Implicits._

  val testMethods = new TestMethods()
  val closureFunc = testMethods.slowMethod _ or testMethods.fastMethod asClosures true
  val nonClosureFunc = testMethods.slowMethod _ or testMethods.fastMethod asClosures false
  val defaultFunc = testMethods.slowMethod _ or testMethods.fastMethod

  val runner = new TestRunner()

  def main(args: Array[String]): Unit = {
    Adaptive.initialize(new DefaultConfiguration)

    runner.runIncrementalTest(l => closureFunc(l))
    runner.runIncrementalTest(l => nonClosureFunc(l))
    runner.runIncrementalTest(l => defaultFunc(l))
  }
}
