package evaluation.loadbalance

import scalaj.http.{Http, HttpOptions, HttpResponse}

/**
  * Created by Petr Kubat on 5/14/17.
  */
class LoadBalanceTestController {
  val ports = List(3123, 3124)
  val baseUrl = "localhost"

  val testData = "TEST"

  val api = new WebApi(baseUrl, ports)

  def buildUrl(port: Int, command: String) = s"http://$baseUrl:$port/$command"

  private def executeCommand(port: Int, command: String) = Http(buildUrl(port, command))
    .postData("")
    .option(HttpOptions.readTimeout(20000))
    .asString

  def increaseLoad(port: Int, times: Int = 1): Unit = {
    Seq.range(0, times).foreach(_ => executeCommand(port, "increaseLoad"))
  }

  def decreaseLoad(port: Int, times: Int = 1): Unit = {
    Seq.range(0, times).foreach(_ => executeCommand(port, "decreaseLoad"))
  }

  def sendRequest(): Unit = api.performBalancedRequest(testData)

  def sendRequest(port: Int): Unit = api.performRequest(api.buildUrl(port))(testData)
}