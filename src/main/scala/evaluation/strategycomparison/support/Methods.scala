package evaluation.strategycomparison.support

import scala.util.Random

/**
  * Created by Petr Kubat on 6/24/17.
  */
class Methods {
  def createSlowerLinear(by: Double): (Int) => Long =
    i => {
      val j = (i * (1 + by)).toInt
      var acc = 0
      Seq.range(0, j).foreach(k => {
        acc = acc + (k * Random.nextInt(1000))
      })
      acc
    }

  def createNormalLinear: (Int) => Long =
    i => {
      val j = i
      var acc = 0
      Seq.range(0, j).foreach(k => {
        acc = acc + (k * Random.nextInt(1000))
      })
      acc
    }

  def createSlowerQuadratic(by: Double): (Int) => Long =
    i => {
      val j = (i * i * (1 + by)).toInt
      var acc = 0
      Seq.range(0, j).foreach(k => {
        acc = acc + (k * Random.nextInt(1000))
      })
      acc
    }

  def createNormalQuadratic: (Int) => Long =
    i => {
      val j = i * i
      var acc = 0
      Seq.range(0, j).foreach(k => {
        acc = acc + (k * Random.nextInt(1000))
      })
      acc
    }
}
