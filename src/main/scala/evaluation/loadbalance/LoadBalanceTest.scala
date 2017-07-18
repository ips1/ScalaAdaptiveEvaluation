package evaluation.loadbalance

import scalaadaptive.api.Adaptive
import scalaadaptive.core.configuration.blocks.logging.ConsoleLogging
import scalaadaptive.core.configuration.defaults.DefaultConfiguration

/**
  * Created by Petr Kubat on 5/14/17.
  *
  * The load balancing test. Note that the simple Node.js server that is provided along with the package has to be
  * running on the same machine (i.e. accessible via localhost) in two instances on two ports (firstPort and secondPort).
  *
  * Each one of the scenarios consists of a couple of stages. In each stage, stageRunCount requests will be sent using
  * both the simple requests and the combined one. After sending all three, the test actively waits for waitTimeMilis
  * before sending another one.
  *
  * The scenarios change the response times of the servers between stages using special requests. Custom scenarios
  * can be designed.
  *
  * The output of the test (printed to the standard output) are the response times of the request to the first server,
  * request to the second server and combined request, separated by commas.
  *
  */
object LoadBalanceTest {
  val stageRunCount = 50
  val waitTimeMilis = 500
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
      while (getMilisTime < startTime + (j * waitTimeMilis)) { }
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
