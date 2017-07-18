package evaluation.loadbalance

import scalaadaptive.api.Adaptive
import scalaadaptive.core.configuration.blocks.logging.ConsoleLogging
import scalaadaptive.core.configuration.defaults.DefaultConfiguration

/**
  * Created by Petr Kubat on 5/14/17.
  */
object LoadBalanceTest {
  val stageRunCount = 50
  val testDurationMilis = 500
  val testController = new LoadBalanceTestController()

  // Configure ports correctly
  val firstPort = 3123
  val secondPort = 3124

  private def getMilisTime = System.nanoTime() / (1000 * 1000)

  private def measureRequest(request: () => Unit): Double = {
    val startTime = System.nanoTime()
    request()
    (System.nanoTime() - startTime).toDouble / (1000 * 1000)
  }

  private def runStage() = {
    val startTime = getMilisTime
    Seq.range(1, stageRunCount).foreach { j =>
      val firstDur = measureRequest(() => testController.sendRequest(firstPort))
      val secondDur = measureRequest(() => testController.sendRequest(secondPort))
      val combinedDur = measureRequest(() => testController.sendRequest())

      println(s"$firstDur, $secondDur, $combinedDur")
      while (getMilisTime < startTime + (j * testDurationMilis)) { }
    }
  }

  def scenario1(): Unit = {
    runStage()
    testController.increaseLoad(firstPort, 2)
    runStage()
    testController.decreaseLoad(firstPort, 2)
    runStage()
    testController.decreaseLoad(secondPort)
    runStage()
    testController.increaseLoad(secondPort, 2)
    runStage()
    testController.increaseLoad(firstPort, 2)
    runStage()
    testController.decreaseLoad(firstPort)
    testController.increaseLoad(secondPort, 2)
    runStage()
    testController.decreaseLoad(firstPort)
    testController.decreaseLoad(secondPort, 3)
  }

  def scenario2(): Unit = {
    testController.increaseLoad(firstPort, 2)
    runStage()
    testController.decreaseLoad(firstPort, 2)
    testController.increaseLoad(secondPort, 2)
    runStage()
    testController.decreaseLoad(secondPort, 2)
    testController.increaseLoad(firstPort, 2)
    runStage()
    testController.decreaseLoad(firstPort, 2)
    testController.increaseLoad(secondPort, 2)
    runStage()
    testController.decreaseLoad(secondPort, 2)
  }

  def scenario3(): Unit = {
    testController.increaseLoad(firstPort)
    runStage()
    testController.increaseLoad(secondPort, 2)
    runStage()
    testController.increaseLoad(secondPort, 3)
    runStage()
    testController.decreaseLoad(secondPort, 6)
    runStage()
    testController.increaseLoad(secondPort, 1)
    testController.decreaseLoad(firstPort)
  }

  def main(args: Array[String]): Unit = {
    Adaptive.initialize(new DefaultConfiguration with ConsoleLogging)

    scenario1()
    scenario2()
    scenario3()
  }
}
