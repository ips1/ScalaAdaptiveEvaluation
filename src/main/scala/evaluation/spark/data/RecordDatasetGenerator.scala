package evaluation.spark.data

import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import evaluation.spark.apiadaptors.{DataHolder, DatasetData}

import scala.util.Random

/**
  * Created by pk250187 on 5/15/17.
  */
class RecordDatasetGenerator extends DataProvider[Record] {
  import evaluation.spark.data.RandomExtensions._
  def generateData(size: Int, spark: SparkSession): DataHolder[Record] = {
    val rand = new Random(System.nanoTime())

    val data = Seq.fill(size)(rand.nextRecord)

    import spark.implicits._
    val dataset = spark.sqlContext.createDataset(data)

    dataset.persist(StorageLevel.MEMORY_ONLY)
    dataset.count()
    new DatasetData(dataset)
  }
}
