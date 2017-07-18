package spark.queries

import spark.apiadaptors.DataHolder

import scalaadaptive.api.options.Selection


/**
  * Created by pk250187 on 6/3/17.
  */
class GroupPairsByIdQuery extends Query[(Int, Array[Byte])] {
  def rddQuery(data: DataHolder[(Int, Array[Byte])]): Long = {
    data.getRdd.groupBy(_._1).count
  }

  def sqlQuery(data: DataHolder[(Int, Array[Byte])]): Long = {
    val dataset = data.getDataset
    import dataset.sqlContext.implicits._
    data.getDataset.groupByKey(i => i._1).keys.count()
  }

  import scalaadaptive.api.Implicits._

  override val query = rddQuery _ or sqlQuery selectUsing Selection.MeanBased
}
