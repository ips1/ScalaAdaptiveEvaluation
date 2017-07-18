package evaluation.spark

import evaluation.spark.data.{ParallelRddDataGenerator, Record, RecordDatasetGenerator}
import evaluation.spark.queries.{GroupPairsByIdQuery, MultipleRecordsGroupingQuery}

/**
  * Created by Petr Kubat on 7/18/17.
  */
object RddVsDatasetTestRecordQuery {
  def main(args: Array[String]): Unit = {
    val test = new RddVsDatasetTestCommons[Record]
    test.execute(args, new RecordDatasetGenerator, (spark) => new MultipleRecordsGroupingQuery(spark.sqlContext))
  }
}
