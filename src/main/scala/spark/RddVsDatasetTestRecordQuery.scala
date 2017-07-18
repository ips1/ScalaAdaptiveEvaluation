package spark

import spark.data.{ParallelRddDataGenerator, Record, RecordDatasetGenerator}
import spark.queries.{GroupPairsByIdQuery, MultipleRecordsGroupingQuery}

/**
  * Created by Petr Kubat on 7/18/17.
  */
object RddVsDatasetTestRecordQuery {
  def main(args: Array[String]): Unit = {
    val test = new RddVsDatasetTestCommons[Record]
    test.execute(args, new RecordDatasetGenerator, (spark) => new MultipleRecordsGroupingQuery(spark.sqlContext))
  }
}
