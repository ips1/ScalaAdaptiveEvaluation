/**
  * Created by Petr Kubat on 4/30/17.
  */
package evaluation.methodnames

import commonutils.Sorter

object MethodNamesTester {
  import scala.math._
  import scalaadaptive.api.Implicits._
  import scalaadaptive.core.macros.MacroUtils._

  def method(i: Int): Int = ???
  def method2(i: Int): Int = ???

  def overloadedMethod(i: Int): Int = ???
  def overloadedMethod(i: String): Int = ???
  def overloadedMethod(i: Int, j: String): Int = ???

  def genericMethod[T](t: T): T = ???

  def genericMethodMultipleArgs[T, U](t: T): U = ???

  def genericMethodImplicit[T](t: T)(implicit ord: T => Ordered[T]): T = ???

  def genericMethodImplicitMultipleArgs[T, U](t: T, u: U)(implicit ord: T => Ordered[T], num: U => Ordered[U]): T = ???

  def genericMethodList[T](t: List[T]): T = ???

  val fun: (Int) => Int = (i: Int) => ???
  val fun2: (Int) => Int = (i: Int) => ???

  object SorterSingleton {
    val sorter = new Sorter()
    def quickSort(array: Array[Int]): Array[Int] = sorter.quickSort(array)
    def selectionSort(array: Array[Int]): Array[Int] = sorter.selectionSort(array)
  }

  var variableSorter = new Sorter()

  def getSorter: Sorter = new Sorter()
  val oveloadResolution: (Int) => Int = overloadedMethod

  // Some interesting AST - uncomment to get
  //  printAst(genericMethod[Int] _)
  //  printAst(genericMethodMultipleArgs[Int, Double] _)
  //  printAst(genericMethodImplicit[Int] _)
  //  printAst(genericMethodImplicitMultipleArgs[Double, Int] _)

  val thisCall = method _ or method2
  val genericThisCall = genericMethod[Int] _ or method
  val valCall = SorterSingleton.quickSort _ or SorterSingleton.selectionSort
  val varCall = variableSorter.quickSort _ or variableSorter.selectionSort
  val etaLikeLambdaCall = ((i: Int) => method(i)) or method2
  val lambdaCalls = ((i: Int) => method(i + 1)) or method2
  val lambdaCalls2 = ((i: Int) => { val x = i * 2; x + 1 }) or method2
  val methodCall = getSorter.quickSort _ or getSorter.selectionSort
  val ctorCall = new Sorter().quickSort _ or new Sorter().selectionSort
  val genericCtorCall = new GenericClass[Int]().doStuff _ or method2
  val funCall = fun or fun2

  def main(args: Array[String]): Unit = {
    println(thisCall.toDebugString)
    println(genericThisCall.toDebugString)
    println(valCall.toDebugString)
    println(varCall.toDebugString)
    println(etaLikeLambdaCall.toDebugString)
    println(lambdaCalls.toDebugString)
    println(lambdaCalls2.toDebugString)
    println(methodCall.toDebugString)
    println(ctorCall.toDebugString)
    println(genericCtorCall.toDebugString)
    println(funCall.toDebugString)
  }
}
