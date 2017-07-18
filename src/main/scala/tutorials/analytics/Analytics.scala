package tutorials.analytics

import java.io.PrintWriter

/**
  * Created by Petr Kubat on 7/17/17.
  */
object Analytics {
  import scalaadaptive.api.Implicits._

  def fastHello(): Unit = println("Hello World!")
  def slowHello(): Unit =  {
    Thread.sleep(10)
    println("Sloooooow Hello World!")
  }

  val hello = fastHello _ or slowHello

  def main(args: Array[String]): Unit = {
    Seq.range(0, 100).foreach(i => hello())

    // Note that the analytics data collection might be turned off using the configuration, in such a case, None would
    // be returned and exception would be raised when unwrapping Optional
    val analytics = hello.getAnalyticsData.get

    // Printing all the analytics data to the console (can be saved to file in the same way)
    analytics.saveData(new PrintWriter(System.out))

    // Making a few computations using the analytics data
    val runsByFunctions = analytics.getAllRunInfo.groupBy(run => run.selectedFunction)

    val countsByFunction = runsByFunctions.mapValues(_.size)
    countsByFunction.foreach(t => println(s"Function ${t._1} invoked ${t._2} times."))

    val averageTimesByFunction = runsByFunctions.mapValues(records => {
      val sumInMs = records.map(r => r.runTime).sum.toDouble / (1000 * 1000)
      if (records.nonEmpty) sumInMs / records.size else 0
    })
    averageTimesByFunction.foreach(t => println(s"Function ${t._1} takes ${t._2} ms on average."))
  }
}
