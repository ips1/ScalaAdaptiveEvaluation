package tutorials.configuration

import scalaadaptive.api.Adaptive
import scalaadaptive.core.configuration.blocks.analytics.NoAnalyticsCollection
import scalaadaptive.core.configuration.blocks.logging.ConsoleLogging
import scalaadaptive.core.configuration.defaults.DefaultConfiguration

/**
  * Created by Petr Kubat on 7/17/17.
  */
object Configuration {
  import scalaadaptive.api.Implicits._

  // The general configuration is set by extending DefaultConfiguration with building block mixins
  val configuration = new DefaultConfiguration
    with ConsoleLogging
    with NoAnalyticsCollection {
    // More detailed arguments and values (either of the original configuration or of the mixins) can be specified
    // by overriding the value
    override val maximumNumberOfRecords = 50
    override val lowRunLimit = 10
  }

  // The framework has to be initialized with the configuration
  Adaptive.initialize(configuration)

  val fastHello = () => println("Hello World!")
  val slowHello = () => {
    Thread.sleep(10)
    println("Sloooooow Hello World!")
  }

  // Note that the adaptive function has to be created after the initialization to take an effect
  val hello = fastHello or slowHello

  def main(args: Array[String]): Unit = {
    Seq.range(0, 100).foreach(i => hello())

    println(hello.getAnalyticsData.isDefined)
  }
}
