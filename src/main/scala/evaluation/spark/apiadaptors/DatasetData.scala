package evaluation.spark.apiadaptors

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset
import org.apache.spark.storage.StorageLevel

/**
  * Created by pk250187 on 6/24/17.
  */
class DatasetData[T](val data: Dataset[T]) extends DataHolder[T] {
  data.persist(StorageLevel.MEMORY_ONLY)
  data.count()

  private val rdd = data.rdd
  rdd.persist(StorageLevel.MEMORY_ONLY)
  rdd.count()

  override def getRdd: RDD[T] = rdd

  override def getDataset: Dataset[T] = data

  lazy val size: Long = data.count()
}
