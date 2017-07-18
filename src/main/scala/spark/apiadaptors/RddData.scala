package spark.apiadaptors

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, Encoder, SQLContext}
import org.apache.spark.storage.StorageLevel

/**
  * Created by pk250187 on 6/4/17.
  */
class RddData[T : Encoder](val data: RDD[T], val size: Long, val sqlContext: SQLContext) extends DataHolder[T] {
  private val dataset = sqlContext.createDataset(data)
  dataset.persist(StorageLevel.MEMORY_ONLY)
  dataset.count()

  override def getRdd: RDD[T] = data
  override def getDataset: Dataset[T] = dataset
}
