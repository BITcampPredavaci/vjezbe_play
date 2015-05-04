name := """bitter"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

resolvers += "PayPal-Java-SDK" at "https://github.com/paypal/PayPal-Java-SDK"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.mindrot" % "jbcrypt" % "0.3m",
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "com.typesafe.play" %% "play-mailer" % "2.4.0",
  "mysql" % "mysql-connector-java" % "5.1.18",
  "commons-io" % "commons-io" % "2.3",
  "com.paypal.sdk" % "rest-api-sdk" % "1.2.0"
)
