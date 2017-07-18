package tests

/**
  * Created by Petr Kubat on 6/10/17.
  */
object WaitMethods {
  def waitForNanos(nanos: Long): Unit = {
    val continueTime = System.nanoTime + nanos
    while (System.nanoTime < continueTime) { }
  }

  def waitForMicros(micros: Long): Unit = {
    val continueTime = System.nanoTime + (micros * 1000)
    while (System.nanoTime < continueTime) { }
  }

  def sleep(milis: Long): Unit = {
    Thread.sleep(milis)
  }
}
