package commonutils

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class Sorter {
  def selectionSort(array: Array[Int]): Array[Int] = {
    for (i <- array.indices) {
      var minIndex = i

      for (j <- i + 1 until array.length) {
        if (array(j) < array(minIndex)) {
          minIndex = j
        }
      }

      // Swap
      if (minIndex != i) {
        val temp = array(i)
        array(i) = array(minIndex)
        array(minIndex) = temp
      }
    }

    array
  }

  def quickSort(array: Array[Int]): Array[Int] = {
    def getPivot(array: mutable.Buffer[Int]): Int = {
      // Selecting max from three locations
      val first = array.head
      val middle = array(array.length / 2)
      val last = array.last

      Math.max(Math.min(first, middle), Math.min(Math.max(first, middle), last))
    }

    def quickSortInner(array: mutable.Buffer[Int]): mutable.Buffer[Int] = {
      if (array.length <= 1) {
        return array
      }

      val pivot = getPivot(array)
      array -= pivot

      val (low, high) = array.toArray.partition(_ <= pivot)
      ArrayBuffer.concat(quickSortInner(low.toBuffer), ArrayBuffer(pivot), quickSortInner(high.toBuffer))
    }

    quickSortInner(array.toBuffer).toArray
  }


}