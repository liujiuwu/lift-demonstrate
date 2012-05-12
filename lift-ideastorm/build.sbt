name := "lift-ideastorm"

scalaVersion := "2.9.1"

seq(webSettings: _*)

libraryDependencies ++= {
  val jettyVersion = "7.5.4.v20111024"
  Seq(
    "org.eclipse.jetty" % "jetty-webapp" % jettyVersion % "compile,test,container",
    "org.eclipse.jetty" % "jetty-plus" % jettyVersion % "container")
}

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked")

//javaOptions  += "-Drun.mode=production" 

offline := true

// If using JRebel
scanDirectories in Compile := Nil

port in container.Configuration := 8087

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
  "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/")

libraryDependencies ++= {
  val liftVersion = "2.4" // Put the current/latest lift version here
  Seq(
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-textile" % liftVersion % "compile->default",
    "net.liftweb" %% "lift-mongodb-record" % liftVersion % "compile->default"
    )
}

// Customize scala lib
libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor" % "2.0.1",
  "com.typesafe.akka" % "akka-remote" % "2.0.1",
  "com.typesafe.akka" % "akka-slf4j" % "2.0.1",
  "com.typesafe.akka" % "akka-testkit" % "2.0.1",
  "com.typesafe.akka" % "akka-kernel" % "2.0.1",
  "com.typesafe.akka" % "akka-durable-mailboxes" % "2.0.1",
  "org.mongodb" %% "casbah" % "3.0.0-M2",
  "org.scalatest" %% "scalatest" % "1.7.2" % "test",
  "org.scalamock" %% "scalamock-scalatest-support" % "latest.integration" % "test"
)

// Customize java lib
libraryDependencies ++= Seq(
  "org.clapper" %% "markwrap" % "0.5.4",
  "org.bouncycastle" % "bcprov-ext-jdk15on" % "1.47",
  "org.apache.commons" % "commons-lang3" % "3.1",
  "commons-io" % "commons-io" % "2.3",
  "commons-codec" % "commons-codec" % "1.6",
  "org.apache.commons" % "commons-email" % "1.2",
  "org.apache.httpcomponents" % "httpclient" % "4.1.3",
  "io.netty" % "netty" % "3.4.4.Final",
  "org.slf4j" % "slf4j-api" % "1.6.4",
  "ch.qos.logback" % "logback-classic" % "1.0.2",
  "mysql" % "mysql-connector-java" % "5.1.20",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "net.sourceforge.jexcelapi" % "jxl" % "2.6.12",
  "junit" % "junit" % "4.10" % "test"
)

