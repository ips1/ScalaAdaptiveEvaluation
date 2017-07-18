package tests.delayed

import java.io.PrintWriter

import scalaadaptive.core.configuration.defaults.DefaultConfiguration
import scalaadaptive.api.options.{Selection, Storage}
import scalaadaptive.api.Adaptive
import scalaadaptive.core.configuration.blocks.history.CachedStatisticsHistory
import scalaadaptive.api.policies.PauseSelectionAfterStreakPolicy

/**
  * Created by Petr Kubat on 4/23/17.
  */
object DelayedTest {
  import scalaadaptive.api.Implicits._
  def getFastConfig(): DelayedConfig = FastConfig()
  def getSlowConfig(): DelayedConfig = SlowConfig()
  val getConfig: () => DelayedConfig = (getFastConfig _ or getSlowConfig
    selectUsing Selection.MeanBased
    storeUsing Storage.Global
    withPolicy new PauseSelectionAfterStreakPolicy(30, 200))

  def run(config: DelayedConfig) = {
    config match {
      case SlowConfig() => Thread.sleep(5)
      case FastConfig() => Thread.sleep(1)
    }
  }

  def test() = {
    val (config, measure) = getConfig^()
    for (x <- 1 to 10) {
      measure(() => run(config))
    }
  }

  def main(args: Array[String]): Unit = {
    Adaptive.initialize(new DefaultConfiguration with CachedStatisticsHistory)

    for (x <- 1 to 20) {
      test()
    }

    getConfig.getAnalyticsData.foreach(d => d.saveData(new PrintWriter(System.out)))
  }
}
