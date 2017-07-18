package tests.manyargs

/**
  * Created by Petr Kubat on 6/5/17.
  */
object FindMultipleItems {
  def findViaFilter(list: Seq[(Int, String)], items: Seq[Int]): Seq[(Int, String)] =
    list.filter(i => items.contains(i._1)).groupBy(_._1).map(_._2.head).toList

  def findViaFind(list: Seq[(Int, String)], items: Seq[Int]): Seq[(Int, String)] =
    items.map(i => list.find(_._1 == i)).filter(_.isDefined).map(_.get)
}
