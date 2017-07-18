package tests.policies

import tests.TestMethods

import scalaadaptive.api.policies.LimitedGatherTimePolicy

/**
  * Created by Petr Kubat on 6/5/17.
  */
object LimitedGatherTime {
  def main(args: Array[String]): Unit = {
    val methods = new TestMethods

    // Every 2s in real-time, we allow 100ms of data gathering and 400ms of deciding
    val policy = new LimitedGatherTimePolicy(2 * 1000 * 1000 * 1000, 1000 * 1000 * 100, 1000 * 1000 * 400)

    import scalaadaptive.api.Implicits._
    val function = methods.fastMethod _ or methods.slowMethod withPolicy policy

    val testRuns = 10000
    Seq.range(0, testRuns).foreach(i => {
      println("Running:")
      function(List(1))
    })
  }
}
