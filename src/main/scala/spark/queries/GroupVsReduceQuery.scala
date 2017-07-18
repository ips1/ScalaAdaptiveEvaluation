package spark.queries

import spark.apiadaptors.DataHolder

import scalaadaptive.api.options.Selection

/**
  * Created by pk250187 on 6/25/17.
  */
class GroupVsReduceQuery {
  def queryGroup(data: DataHolder[(Int, Array[Byte])]): Array[(Int, Array[Byte])] =
    data
      .getRdd
      .groupByKey()
      .map(i => (i._1, i._2.flatten.toArray))
      .collect()

  def queryReduce(data: DataHolder[(Int, Array[Byte])]): Array[(Int, Array[Byte])] =
    data
      .getRdd
      .reduceByKey((arr1, arr2) => arr1 ++ arr2)
      .collect()

  import scalaadaptive.api.Implicits._
  val query = queryGroup _ or queryReduce selectUsing Selection.MeanBased
}
