package evaluation.spark.data

import scala.util.Random

/**
  * Created by petrkubat on 26/02/2017.
  */
object RandomExtensions {
  implicit def richRand(rand: Random) = new {
    def nextRecord = new Record(rand.nextInt(Record.MaxRecordType),
                                rand.nextInt(Record.MaxSize),
                                rand.nextInt(),
                                rand.nextInt(Record.MaxUsers))

    def nextString(length: Int): String = List.fill(length)(rand.alphanumeric.filter(_.isLetter).head).toString()
  }
}
