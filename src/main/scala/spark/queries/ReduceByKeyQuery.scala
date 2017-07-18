package spark.queries

import org.apache.spark.sql.SQLContext
import spark.apiadaptors.DataHolder
import spark.data.Record

import scalaadaptive.api.options.Selection

/**
  * Created by pk250187 on 6/6/17.
  */
class ReduceByKeyQuery(sparkSql: SQLContext) extends Query[Record] {
  import sparkSql.implicits._

  def executeDataset(data: DataHolder[Record]): Long =
    data.getDataset
      .filter(_.recordType > 5)
      .groupByKey(_.size)
      .reduceGroups((left, right) => new Record(left.recordType, left.size, left.data, left.users + right.users))
      .filter(_._2.users > 200)
      .count()

  def executeRdd(data: DataHolder[Record]): Long =
    data.getRdd
      .filter(_.recordType > 5)
      .map(r => (r.size, r))
      .reduceByKey((left, right) => new Record(left.recordType, left.size, left.data, left.users + right.users))
      .filter(_._2.users > 200)
      .count()

  import scalaadaptive.api.Implicits._

  override val query = executeDataset _ or executeRdd selectUsing Selection.MeanBased
}
