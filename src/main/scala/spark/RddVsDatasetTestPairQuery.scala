package spark

import spark.data.ParallelRddDataGenerator
import spark.queries.GroupPairsByIdQuery

/**
  * Created by pk250187 on 6/24/17.
  */
object RddVsDatasetTestPairQuery {
  def main(args: Array[String]): Unit = {
    val test = new RddVsDatasetTestCommons[(Int, Array[Byte])]
    test.execute(args, new ParallelRddDataGenerator, (_) => new GroupPairsByIdQuery)
  }
}
