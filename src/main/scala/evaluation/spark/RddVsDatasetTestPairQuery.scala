package evaluation.spark

import evaluation.spark.data.ParallelRddDataGenerator
import evaluation.spark.queries.GroupPairsByIdQuery

/**
  * Created by pk250187 on 6/24/17.
  *
  * An instance of RddVsDatasetTest with specific data generator and query (denoted as Query 2 in the thesis text).
  *
  */
object RddVsDatasetTestPairQuery {
  def main(args: Array[String]): Unit = {
    val test = new RddVsDatasetTestCommons[(Int, Array[Byte])]
    test.execute(args, new ParallelRddDataGenerator, (_) => new GroupPairsByIdQuery)
  }
}
