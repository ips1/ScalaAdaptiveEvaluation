package tutorials.reset_flush

import scalaadaptive.api.Adaptive

/**
  * Created by Petr Kubat on 7/17/17.
  */
object ResetFlush {
  import scalaadaptive.api.Implicits._

  val fastHello = () => println("Hello World!")
  val slowHello = () => {
    Thread.sleep(10)
    println("Sloooooow Hello World!")
  }

  val hello = fastHello or slowHello

  def main(args: Array[String]): Unit = {
    Seq.range(0, 100).foreach(i => hello())

    // Resetting the whole framework
    println("Resetting")
    Adaptive.reset()
    Seq.range(0, 100).foreach(i => hello())

    // Flushing function history
    println("Flushing slowHello")
    slowHello.flushHistory()
    Seq.range(0, 100).foreach(i => hello())

    println("Flushing fastHello")
    fastHello.flushHistory()
    Seq.range(0, 100).foreach(i => hello())
  }
}
