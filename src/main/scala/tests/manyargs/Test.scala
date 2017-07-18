package tests.manyargs

import scala.util.Random

/**
  * Created by Petr Kubat on 6/5/17.
  */
object Test {
  val size = 200000
  val data = new DataGenerator().generateData(size)
  val random = new Random(42)

  val testCount = 100

  def runThreeArgsTest(lookingFor: Seq[Int]): Seq[(Int, String)] = {
    val methods = new ThreeArgsMethods
    methods.find(data, lookingFor(0), lookingFor(1))
  }

  def runFourArgsTest(lookingFor: Seq[Int]): Seq[(Int, String)] = {
    val methods = new FourArgsMethods
    methods.find(data, lookingFor(0), lookingFor(1), lookingFor(2))
  }

  def runFiveArgsTest(lookingFor: Seq[Int]): Seq[(Int, String)] = {
    val methods = new FiveArgsMethods
    methods.find(data, lookingFor(0), lookingFor(1), lookingFor(2), lookingFor(3))
  }

  def main(args: Array[String]): Unit = {
    val lookingFor = Seq.range(0, 4).map(i => random.nextInt(size) + 1)
    Seq.range(0, testCount).foreach(i => {
      println(runFiveArgsTest(lookingFor).map(_._2.toString).mkString(","))
    })
  }
}
