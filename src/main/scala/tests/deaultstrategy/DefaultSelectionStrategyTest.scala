package tests.deaultstrategy

import tests.TestMethods

import scalaadaptive.api.Adaptive
import scalaadaptive.api.options.Selection
import scalaadaptive.core.configuration.blocks.logging.ConsoleLogging
import scalaadaptive.core.configuration.defaults.DefaultConfiguration

/**
  * Created by Petr Kubat on 7/16/17.
  */
object DefaultSelectionStrategyTest {
  Adaptive.initialize(new DefaultConfiguration with ConsoleLogging)
  import scalaadaptive.api.Implicits._
  val testMethods = new TestMethods()
  val fun1 = testMethods.linearHighConstant _ or testMethods.linearMinConstant by (_.size)
  val fun2 = testMethods.fastMethod _ or testMethods.slowMethod
  val fun3 = testMethods.linearHighConstant _ or testMethods.linearMinConstant by (_.size) selectUsing Selection.MeanBased
  val fun4 = testMethods.fastMethod _ or testMethods.slowMethod selectUsing Selection.InputBased

  def main(args: Array[String]): Unit = {
    val list = List(1, 2, 3)
    Seq.range(0, 100).foreach(i => {
      fun1(list)
      fun2(list)
      fun3(list)
      fun4(list)
    })
  }
}
