name := "SchemeReader"

version := "1.0"

scalaVersion := "2.11.6"

//resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

//addCompilerPlugin("com.artima.supersafe" %% "supersafe" % "1.0.3")

//scalacOptions += "-P:artima-supersafe:config-file:project/supersafe.cfg"

scalacOptions ++= Seq("-feature")

libraryDependencies ++= Seq(
  "org.clapper" %% "grizzled-slf4j" % "1.0.2",
  "org.slf4j" % "slf4j-simple" % "1.6.4",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test",
  "com.typesafe.play" %% "play-json" % "2.3.4",
  "org.springframework" % "spring-core" % "4.1.1.RELEASE",
  "org.springframework" % "spring-context" % "4.1.1.RELEASE",
  "org.springframework" % "spring-beans" % "4.1.1.RELEASE",
  "org.springframework" % "spring-tx" % "4.1.1.RELEASE",
  "org.springframework" % "spring-jdbc" % "4.1.1.RELEASE",
  "org.springframework" % "spring-orm" % "4.1.1.RELEASE",
  "com.oracle" % "ojdbc14" % "10.2.0.4")