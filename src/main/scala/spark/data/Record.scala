package spark.data

/**
  * Created by petrkubat on 26/02/2017.
  */
case class Record(recordType: Int, size: Int, data: Int, users: Int)

object Record {
  val MaxRecordType = 20
  val MaxSize = 10000
  val MaxData = Int.MaxValue
  val MaxUsers = 1000
}
