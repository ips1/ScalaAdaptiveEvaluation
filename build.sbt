name := "ScalaAdaptiveEvaluation"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.scala-lang" % "scala-reflect" % "2.11.8"
libraryDependencies += "org.apache.evaluation.spark" % "evaluation.spark-core_2.11" % "2.1.0"
libraryDependencies += "org.apache.evaluation.spark" % "evaluation.spark-sql_2.11" % "2.1.0"
libraryDependencies += "org.apache.evaluation.spark" % "evaluation.spark-mllib_2.11" % "2.1.0"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.8"
libraryDependencies += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.8"
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.1"
libraryDependencies += "org.scalaj" % "scalaj-http_2.11" % "2.3.0"

fork := true
javaOptions in run += "-Xmx16G"
javaOptions in run += "-Dspark.ui.showConsoleProgress=false"
