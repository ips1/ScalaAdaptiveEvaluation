package tutorials.methods_02

/**
  * Created by Petr Kubat on 7/16/17.
  */
object Methods {
  import scalaadaptive.api.Implicits._

  class Hello {
    def fastHello(): Unit = println("Hello World!")
    def slowHello(): Unit =  {
      Thread.sleep(10)
      println("Sloooooow Hello World!")
    }
  }

  val helloInstance = new Hello

  // The _ operator is necessary to convert the method to a function
  val hello = helloInstance.fastHello _ or helloInstance.slowHello

  def main(args: Array[String]): Unit = {
    Seq.range(0, 100).foreach(i => hello())
  }
}
