package tests.trainandpredict

import commonutils.Sorter
import tests.TestMethods

import scala.util.Random
import scalaadaptive.api.Adaptive
import scalaadaptive.core.configuration.Configuration
import scalaadaptive.core.configuration.blocks.logging.ConsoleLogging
import scalaadaptive.core.configuration.blocks.selection.LoessInterpolationInputBasedStrategy
import scalaadaptive.core.configuration.defaults.DefaultConfiguration

/**
  * Created by Petr Kubat on 6/7/17.
  */
object PredictionTest {
  def generateRandomDataOfRandomSize(maxSize: Int): List[Int] =
    generateRandomData(Random.nextInt(maxSize))

  def generateRandomData(size: Int): List[Int] =
    Seq.fill(size)(Random.nextInt).toList

  def runTestWithConfig(config: Configuration): Unit = {
    Adaptive.initialize(config)
    val testMethods = new TestMethods()
    val sorter = new Sorter()

    import scalaadaptive.api.Implicits._
    val sort = sorter.quickSort _ or sorter.selectionSort by (_.length)

    val testCount = 400
    val maxSize = 5000
    val verificationStep = 50
    val random = new Random(System.nanoTime)
    val trainData = Seq.fill(testCount)(generateRandomDataOfRandomSize(maxSize).toArray).toList

    // Training the model
    sort.train(trainData)

    val verificationData = Seq.range(0, maxSize, verificationStep).map(generateRandomData).map(_.toArray)

    verificationData.foreach(d => {
      sort(d)
    })
  }

  def main(args: Array[String]): Unit = {
    val configurations = List(
      new DefaultConfiguration with LoessInterpolationInputBasedStrategy with ConsoleLogging
    )

    configurations.foreach(runTestWithConfig)
  }
}
