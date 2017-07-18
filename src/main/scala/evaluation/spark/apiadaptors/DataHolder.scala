package evaluation.spark.apiadaptors

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset

/**
  * Created by pk250187 on 6/4/17.
  */
trait DataHolder[T] {
  def getRdd: RDD[T]
  def getDataset: Dataset[T]
  val size: Long
}
