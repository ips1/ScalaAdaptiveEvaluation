package evaluation.matrixmul


import org.jlinalg.{Matrix, MatrixMultiplication}
import org.jlinalg.doublewrapper.DoubleWrapper

import scala.util.Random
import scalaadaptive.api.Adaptive
import scalaadaptive.api.options.Selection
import scalaadaptive.core.configuration.blocks.history.{CachedRegressionHistory, CachedStatisticsHistory}
import scalaadaptive.core.configuration.blocks.persistence.BufferedPersistence
import scalaadaptive.core.configuration.blocks.selection.{TTestMeanBasedStrategy, WindowBoundRegressionInputBasedStrategy}
import scalaadaptive.core.configuration.defaults.DefaultConfiguration

/**
  * Created by Petr Kubat on 7/3/17.
  */
object MatrixMultiplicationTest {
  val runCount = 200
  val roundSize = 40
  val maxMatrixSize = 900
  val minMatrixSize = 50
  val maxInt = 100000

  // Ensure static constructor is called to replace the protected static values
  val customizedMul = new CustomizedMatrixMultiplication()

  private def measureExecTime(fun: () => Unit): Long = {
    val start = System.nanoTime
    fun()
    System.nanoTime - start
  }

  private def genMatrix(size: Int): Matrix[DoubleWrapper] = {
    val items: Array[Array[DoubleWrapper]] = Seq.range(0, size).map(i => {
      Seq.fill(size)(new DoubleWrapper(Random.nextInt(maxInt).toDouble)).toArray
    }).toArray

    new Matrix[DoubleWrapper](items)
  }

  private def run(data: Seq[(Matrix[DoubleWrapper], Matrix[DoubleWrapper])],
                  fun: (Matrix[DoubleWrapper], Matrix[DoubleWrapper]) => Matrix[DoubleWrapper]): Seq[Long] = {
    data.map(d => {
      println("next")
      measureExecTime(() => fun(d._1, d._2))
    })
  }

  class Result(val inputSize: Int,
               val normalTime: Long,
               val strassenTime: Long,
               val combinedTime: Long)

  def normalMultiply(mat1: Matrix[DoubleWrapper], mat2: Matrix[DoubleWrapper]): Matrix[DoubleWrapper] = {
    MatrixMultiplication.simple(mat1, mat2)
  }

  def strassenMultiply(mat1: Matrix[DoubleWrapper], mat2: Matrix[DoubleWrapper]): Matrix[DoubleWrapper] = {
    MatrixMultiplication.strassenOriginal(mat1, mat2)
  }

  private def printResult(res: Result) =
    println(s"${res.inputSize}, ${res.combinedTime}, ${res.normalTime}, ${res.strassenTime}")

  private def printSum(res: Seq[Result]) =
    println(s"${res.map(_.combinedTime).sum}, ${res.map(_.normalTime).sum}, ${res.map(_.strassenTime).sum}")

  private def genMatrices(count: Int): Seq[(Matrix[DoubleWrapper], Matrix[DoubleWrapper])] = {
    Seq.range(0, count).map(i => {
      val currentSize = Random.nextInt(maxMatrixSize - minMatrixSize) + minMatrixSize
      (genMatrix(currentSize), genMatrix(currentSize))
    })
  }

  import scalaadaptive.api.Implicits._

  private val customMultiply = (
    normalMultiply _ or strassenMultiply
      by ((m1, m2) => m1.getRows)
      selectUsing Selection.InputBased
    )


  private def run(testCount: Int): Seq[Result] = {
    val matrices = genMatrices(runCount)
    val normalPerf = run(matrices, normalMultiply)
    val strassenPerf = run(matrices, strassenMultiply)
    val customPerf = run(matrices, customMultiply)

    val sizes = matrices.map(_._1.getRows)

    val results = (sizes, (normalPerf, strassenPerf, customPerf).zipped.toList).zipped.map((size, perfs: (Long, Long, Long)) => {
      new Result(size, perfs._1, perfs._2, perfs._3)
    })

    results
  }

  def main(args: Array[String]): Unit = {
    val config = new DefaultConfiguration
      with TTestMeanBasedStrategy
      with WindowBoundRegressionInputBasedStrategy
      with CachedRegressionHistory
      with CachedStatisticsHistory
      with BufferedPersistence {
      override val lowRunLimit = 5
    }

    Adaptive.initialize(config)

    // Initialization runs
    //run(matrices, strassenMultiply)
    //run(matrices, normalMultiply)

    val roundCount = runCount / roundSize

    // Measured runs
    val results = Seq.range(0, roundCount).flatMap(r => {
      run(roundSize)
    }).toList

    val sortedResults = results.sortBy(_.inputSize)

    // Results are printed as a CSV
    sortedResults.foreach(printResult)
    println("Sums:")
    printSum(sortedResults)
  }
}
