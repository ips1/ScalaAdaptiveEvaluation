package tests

import scala.util.Random

/**
  * Created by Petr Kubat on 4/29/17.
  */
class TestRunner(val testRunCount: Int = 2) {
  val bigDataSize = 100
  val smallDataSize = 10
  //val testRunCount = 2
  val runCount = 100
  val step = 2
  lazy val smallData = Seq.fill(smallDataSize)(Random.nextInt).toList
  lazy val bigData = Seq.fill(bigDataSize)(Random.nextInt).toList

  def customData(size: Int): List[Int] = Seq.fill(size)(Random.nextInt).toList

  def isOrdered(l : List[Int]): Boolean = (l, l.tail).zipped.forall(_ <= _)

  private def runTestInstance(testFnc: (List[Int]) => List[Int], testNo: Int, data: List[Int]): Unit = {
    println(s"Running test no: ${testNo} on ${data.size}")
    val startTime = System.nanoTime()
    val res = testFnc(data)
    val elapsed = System.nanoTime() - startTime
    val milis = elapsed.toDouble / (1000 * 1000)
    println(s"Duration: ${milis}ms")
  }

  def runTest(testFnc: (List[Int]) => List[Int]): Unit = {
    Seq.range(0, runCount).foreach(i => {
      val data = if (i % 2 == 0) smallData else bigData
      runTestInstance(testFnc, i, data)
    })
  }

  def runIncrementalTest(testFnc: List[Int] => List[Int]): Unit = {
    Seq.range(0, testRunCount).foreach(k =>
      Seq.range(0, runCount).foreach(i =>
        Seq.range(0, 2).foreach(j => {
          val data = customData(smallDataSize + i * 2)
          runTestInstance(testFnc, i, data)
        })
      )
    )
  }
}
