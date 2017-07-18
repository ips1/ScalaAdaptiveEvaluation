package evaluation.spark.queries

import org.apache.spark.sql.SQLContext
import evaluation.spark.apiadaptors.DataHolder
import evaluation.spark.data.Record

import scalaadaptive.api.options.Selection

/**
  * Created by pk250187 on 6/6/17.
  */
class MultipleRecordsGroupingQuery(sparkSql: SQLContext) extends Query[Record] {
  import sparkSql.implicits._
  def executeDataset(data: DataHolder[Record]): Long =
    data.getDataset
      .filter(_.recordType > 5)
      .groupByKey(_.size)
      .mapGroups((k, g) => k * g.size)
      .filter(i => i > 5000)
      .count()

  def executeRdd(data: DataHolder[Record]): Long =
    data.getRdd
      .filter(_.recordType > 5)
      .groupBy(_.size)
      .map(i => i._1 * i._2.size)
      .filter(i => i > 5000)
      .count()

  import scalaadaptive.api.Implicits._
  override val query = executeDataset _ or executeRdd selectUsing Selection.MeanBased
}
