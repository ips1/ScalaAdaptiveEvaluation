package tutorials.storage_local_05

import scalaadaptive.api.options.Storage

/**
  * Created by Petr Kubat on 7/17/17.
  */
object StorageLocal {
  import scalaadaptive.api.Implicits._

  class Hello {
    def fastHello(): Unit = println("Hello World!")
    def slowHello(): Unit =  {
      Thread.sleep(10)
      println("Sloooooow Hello World!")
    }

    val helloLocal = slowHello _ or fastHello storeUsing Storage.Local
  }

  def main(args: Array[String]): Unit = {
    // Notice that for each call, new and clean history is used, so the first (slow) function is used
    // WARNING: This is an example of a wrong usage of the framework!
    Seq.range(0, 100).foreach(i => {
      (new Hello).helloLocal()
    })
  }
}
