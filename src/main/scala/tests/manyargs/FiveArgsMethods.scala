package tests.manyargs

/**
  * Created by Petr Kubat on 6/5/17.
  */
class FiveArgsMethods {
  def findViaFilter(list: Seq[(Int, String)], item1: Int, item2: Int, item3: Int, item4: Int): Seq[(Int, String)] =
    FindMultipleItems.findViaFilter(list, List(item1, item2, item3, item4))

  def findViaFind(list: Seq[(Int, String)], item1: Int, item2: Int, item3: Int, item4: Int): Seq[(Int, String)] =
    FindMultipleItems.findViaFind(list, List(item1, item2, item3, item4))

  import scalaadaptive.api.Implicits._

  val find = findViaFilter _ or findViaFind
}