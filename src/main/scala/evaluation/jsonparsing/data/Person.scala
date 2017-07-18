package evaluation.jsonparsing.data

/**
  * Created by Petr Kubat on 6/1/17.
  */
class Person {
  var id: Integer = 0
  var isActive: Boolean = false
  var age: String = ""
  var name: String = ""
  var email: String = ""
  var phone: String = ""
  var address: String = ""
  var about: String = ""
  var tags: Array[String] = new Array[String](10)
}
