package evaluation.jsonparsing

import evaluation.jsonparsing.data.PersonList

import scala.io.Source
import scalaadaptive.api.Adaptive
import scalaadaptive.core.configuration.blocks.history.CachedStatisticsHistory
import scalaadaptive.core.configuration.defaults.DefaultConfiguration

/**
  * Created by Petr Kubat on 6/19/17.
  *
  * Test used for comparing JSON parsing times in a given scenario.
  *
  * The JSON files are stored in the resources directory.
  *
  * The test can be configured by setting the repeat counts and the usePolicy variable. The output of the test, printed
  * to standard output, are run total and average run times after parsing each one of the files given number of times.
  *
  * The evaluation in the thesis was done by adding up these numbers.
  *
  */
object ComparisonTest {
  val smallRepeatCount = 10000
  val bigRepeatCount = 5000
  val superBigRepeatCount = 200
  val usePolicy = true

  private def measureParserRun(parser: (String, Class[PersonList]) => PersonList,
                                       data: String): Long = {
    val startTime = System.nanoTime()

    val result = parser(data, classOf[PersonList])

    System.nanoTime() - startTime
  }

  private def runTestWithData(name: String,
                              data: String,
                              repeatCount: Int,
                              usePolicy: Boolean) = {
    var totalGson: Long = 0
    var totalJackson: Long = 0
    var totalCombined: Long = 0
    val parser = new JsonParser[PersonList](usePolicy)

    // Initial runs to avoid structure intialization etc.
    Seq.range(0, 20).foreach(i => {
      parser.parseWithGson(data, classOf[PersonList])
      parser.parseWithJackson(data, classOf[PersonList])
    })

    Seq.range(0, repeatCount).foreach(i => {
      totalGson += measureParserRun(parser.parseWithGson, data)
      totalJackson += measureParserRun(parser.parseWithJackson, data)
      totalCombined += measureParserRun(parser.parse, data)
    })

    val gsonMilis = totalGson.toDouble / (1000 * 1000)
    val jacksonMilis = totalJackson.toDouble / (1000 * 1000)
    val combinedMilis = totalCombined.toDouble / (1000 * 1000)

    println(s"$name - GSON - total: $gsonMilis, average: ${gsonMilis / repeatCount}")
    println(s"$name - Jackson - total: $jacksonMilis, average: ${jacksonMilis / repeatCount}")
    println(s"$name - combined - total: $combinedMilis, average: ${combinedMilis / repeatCount}")
  }

  def main(args: Array[String]): Unit = {
    val bigJsonString = Source.fromInputStream(getClass.getResourceAsStream("/json/BigJsonData.json")).mkString
    val smallJsonString = Source.fromInputStream(getClass.getResourceAsStream("/json/SmallJsonData.json")).mkString
    val superBigJsonString = Source.fromInputStream(getClass.getResourceAsStream("/json/SuperBigJsonData.json")).mkString

    Adaptive.initialize(new DefaultConfiguration with CachedStatisticsHistory)

    // Small JSON
    runTestWithData("Small", smallJsonString, smallRepeatCount, usePolicy)

    // Big JSON
    runTestWithData("Big", bigJsonString, bigRepeatCount, usePolicy)

    // Super big JSON
    runTestWithData("Super big", superBigJsonString, superBigRepeatCount, usePolicy)

    println("DONE!")
  }
}
