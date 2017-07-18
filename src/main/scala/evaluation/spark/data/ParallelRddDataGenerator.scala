package evaluation.spark.data

import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import evaluation.spark.apiadaptors.{DataHolder, RddData}

import scala.util.Random

/**
  * Created by pk250187 on 6/4/17.
  */
class ParallelRddDataGenerator extends DataProvider[(Int, Array[Byte])] {
  def generateData(size: Int, spark: SparkSession): DataHolder[(Int, Array[Byte])] = {
    val numMappers = 10
    val lengthPerMapper = size / numMappers
    val valSize = 1000

    // This data generation is inspired by:
    // https://gitlab.d3s.mff.cuni.cz/bures/spark-bench
    val pairs = spark.sparkContext.parallelize(0 until numMappers, numMappers).flatMap { p =>
      val rand = new Random(System.nanoTime())
      val sequence = Seq.fill(lengthPerMapper) {
        val data = new Array[Byte](valSize)
        rand.nextBytes(data)
        (rand.nextInt(Int.MaxValue), data)
      }
      sequence
    }

    pairs.persist(StorageLevel.MEMORY_ONLY)
    pairs.count()

    import spark.sqlContext.implicits._
    new RddData(pairs, numMappers * lengthPerMapper, spark.sqlContext)
  }
}
