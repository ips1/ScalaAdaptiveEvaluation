package tutorials.storage_persistent_06

import scalaadaptive.api.Adaptive
import scalaadaptive.api.options.Storage
import scalaadaptive.core.configuration.blocks.logging.ConsoleLogging
import scalaadaptive.core.configuration.defaults.DefaultConfiguration
import scalaadaptive.core.logging.ConsoleLogger

/**
  * Created by Petr Kubat on 7/17/17.
  */
object StoragePersistent {
  import scalaadaptive.api.Implicits._

  class Hello {
    def fastHello(): Unit = println("Hello World!")
    def slowHello(): Unit =  {
      Thread.sleep(10)
      println("Sloooooow Hello World!")
    }

    val helloPersistent = fastHello _ or slowHello storeUsing Storage.Persistent
  }

  def main(args: Array[String]): Unit = {
    // The run history gets stored to the HDD
    // -> try running the program twice in a row
    // (to reset the behavior, just delete the adaptive_history folder in the root directory of the project)
    Seq.range(0, 100).foreach(i => {
      (new Hello).helloPersistent()
    })
  }
}
