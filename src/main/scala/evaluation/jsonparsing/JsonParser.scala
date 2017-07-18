package evaluation.jsonparsing

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.gson.GsonBuilder

import scalaadaptive.api.grouping.GroupId
import scalaadaptive.api.options.Selection
import scalaadaptive.api.policies.{AlwaysSelectPolicy, PauseSelectionAfterStreakPolicy}

/**
  * Created by Petr Kubat on 6/1/17.
  */
class JsonParser[TData](usePolicy: Boolean) {
  def parseWithGson(json: String, targetClass: Class[TData]): TData = {
    val builder = new GsonBuilder()
    val gson = builder.create()
    gson.fromJson(json, targetClass)
  }

  def parseWithJackson(json: String, targetClass: Class[TData]): TData = {
    val objectMapper = new ObjectMapper()
    objectMapper.registerModule(DefaultScalaModule)
    objectMapper.readValue(json, targetClass)
  }

  import scalaadaptive.api.Implicits._

  private val policy = if (usePolicy)
    new PauseSelectionAfterStreakPolicy(10, 200)
  else
    new AlwaysSelectPolicy


  val parse = (
    parseWithGson _ or parseWithJackson
    groupBy ((json, c) => GroupId(Math.log(json.length).toInt))
    selectUsing Selection.MeanBased
    withPolicy policy
  )
}
