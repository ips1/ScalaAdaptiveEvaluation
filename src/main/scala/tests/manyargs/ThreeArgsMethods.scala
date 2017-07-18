package tests.manyargs

/**
 * Created by Petr Kubat on 6/5/17.
 */
class ThreeArgsMethods {
  def findViaFilter(list: Seq[(Int, String)], item1: Int, item2: Int): Seq[(Int, String)] =
    FindMultipleItems.findViaFilter(list, List(item1, item2))

  def findViaFind(list: Seq[(Int, String)], item1: Int, item2: Int): Seq[(Int, String)] =
    FindMultipleItems.findViaFind(list, List(item1, item2))

  import scalaadaptive.api.Implicits._

  val find = findViaFilter _ or findViaFind
}
