name := "mutil-db"

scalaVersion := "2.9.1"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked")

resolvers ++= Seq(
  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/")

offline := true

seq(webSettings :_*)

// If using JRebel
scanDirectories in Compile := Nil

port in container.Configuration := 8087

//env in Compile := Some(file(".") / "jetty-env.xml" asFile)

// lift库
libraryDependencies ++= {
  val liftVersion = "2.4"
  Seq(
    "net.liftweb" %% "lift-mongodb-record" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default")
}

// scala第3方库
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "1.7.2" % "test")

// Jetty库
libraryDependencies ++= {
  val jettyVersion = "7.5.4.v20111024"
  Seq(
    "org.eclipse.jetty" % "jetty-plus" % jettyVersion % "container",
    "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "container"
  )
}

// Java第3方
libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.3.163",
  "ch.qos.logback" % "logback-classic" % "1.0.2" % "compile->default",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "junit" % "junit" % "4.10" % "test->default")

