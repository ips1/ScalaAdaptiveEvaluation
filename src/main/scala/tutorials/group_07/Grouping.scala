package tutorials.group_07

import scala.util.Random
import scalaadaptive.api.Adaptive
import scalaadaptive.api.grouping.GroupId
import scalaadaptive.core.configuration.blocks.logging.ConsoleLogging
import scalaadaptive.core.configuration.defaults.DefaultConfiguration

/**
  * Created by Petr Kubat on 7/17/17.
  */
object Grouping {
  import scalaadaptive.api.Implicits._

  def fibonacciRecursive(i: Int): Int = {
    def fib(i: Int): Int = {
      if (i == 0) 0
      else if (i == 1) 1
      else fib(i - 1) + fib(i - 2)
    }

    fib(i)
  }

  def fibonacciIterative(i : Int): Int = {
    var first = 0
    var second = 1
    Seq.range(0, i).foreach(i => {
      val third = first + second
      first = second
      second = third
    })
    first
  }

  // Using the "groupBy" method assigns an input to a group - only historical runs from given group get compared.
  val fibonacci = fibonacciRecursive _ or fibonacciIterative groupBy (i => GroupId(i))

  def main(args: Array[String]): Unit = {
    Seq.range(0, 100).foreach(i => {
      val n = 5
      println(s"$n: ${fibonacci(n)}")
      println(fibonacci.getAnalyticsData.get.getAllRunInfo.last.selectedFunction)
    })

    Seq.range(0, 100).foreach(i => {
      val n = 35
      println(s"$n: ${fibonacci(n)}")
      println(fibonacci.getAnalyticsData.get.getAllRunInfo.last.selectedFunction)
    })

    Seq.range(0, 100).foreach(i => {
      val n = 5
      println(s"$n: ${fibonacci(n)}")
      println(fibonacci.getAnalyticsData.get.getAllRunInfo.last.selectedFunction)
    })
  }
}
