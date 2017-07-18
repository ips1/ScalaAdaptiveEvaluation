package evaluation.loadbalance

import java.time.Duration

import scalaadaptive.api.identifiers.IdentifiedFunction
import scalaadaptive.api.options.Selection
import scalaj.http.{Http, HttpOptions}

/**
  * Created by Petr Kubat on 5/14/17.
  */
class WebApi(val baseUrl: String, val ports: Seq[Int]) {
  val route = "request"

  def buildUrl(port: Int) = s"http://$baseUrl:$port/$route"

  def performRequest(url: String)(query: String): Option[String] = {
    val response = Http(url)
      .param("data", query)
      .option(HttpOptions.readTimeout(20000))
      .asString
    if (response.code != 200) {
      None
    }
    else {
      Some(response.body)
    }
  }

  private def createRequestFun(port: Int) = IdentifiedFunction(performRequest(buildUrl(port)) _, s"req_port_$port")
  val performBalancedRequest =
    ports.tail.foldLeft(createRequestFun(ports.head)) { (f, port) =>
      f.or(createRequestFun(port))
    } selectUsing Selection.MeanBased limitedTo Duration.ofSeconds(20)
}
