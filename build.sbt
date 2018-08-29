name := "percent"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies += "com.tdunning" % "t-digest" % "3.2"

libraryDependencies += "com.storm-enroute" %% "scalameter-core" % "0.10.1"

libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.10.1" % "test"

libraryDependencies += "org.scala-tools.testing" % "scalatest" % "0.9.5" % Test
