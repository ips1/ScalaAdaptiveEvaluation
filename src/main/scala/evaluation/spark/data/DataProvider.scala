package evaluation.spark.data

import org.apache.spark.sql.SparkSession
import evaluation.spark.apiadaptors.DataHolder

/**
  * Created by Petr Kubat on 7/18/17.
  */
trait DataProvider[T] {
  def generateData(size: Int, spark: SparkSession): DataHolder[T]
}
