package tests.flushtest

import tests.{TestMethods, TestRunner}

import scalaadaptive.core.configuration.defaults.DefaultConfiguration
import scalaadaptive.api.options.Storage
import scalaadaptive.api.Adaptive
import scalaadaptive.core.configuration.blocks.selection.LoessInterpolationInputBasedStrategy
import scalaadaptive.core.configuration.blocks.history.CachedGroupHistory

/**
  * Created by Petr Kubat on 5/14/17.
  */
object FlushTest {
  def main(args: Array[String]): Unit = {
    import scalaadaptive.api.Implicits._

    val runner = new TestRunner()
    val testMethods = new TestMethods()
    Adaptive.initialize(new DefaultConfiguration with LoessInterpolationInputBasedStrategy with CachedGroupHistory)

    runner.runIncrementalTest(l => testMethods.function(l))

    // Flush just one of the methods:
    (testMethods.linearHighConstant _ storeUsing Storage.Persistent).flushHistory()

    runner.runIncrementalTest(l => testMethods.function(l))

    // Flush all of the methods:
    testMethods.function.flushHistory()

    runner.runIncrementalTest(l => testMethods.function(l))

    testMethods.function.flushHistory()
  }
}
