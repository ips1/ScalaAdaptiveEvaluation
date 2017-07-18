package spark.queries

import spark.apiadaptors.DataHolder

/**
  * Created by Petr Kubat on 7/18/17.
  */
trait Query[TDataType] {
  val query: (DataHolder[TDataType]) => Long
}
