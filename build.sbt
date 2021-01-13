val AkkaVersion = "2.6.10"
val AkkaPersistenceCassandraVersion = "0.101"

lazy val `test` = project
  .in(file("."))
  .settings(
    organization := "com.test",
    version := "1.0",
    scalaVersion := "2.12.12",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-cluster-sharding-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-persistence-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-persistence-cassandra" % AkkaPersistenceCassandraVersion,
      "com.typesafe.akka" %% "akka-persistence-cassandra-launcher" % AkkaPersistenceCassandraVersion,
      "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    ),
    mainClass in (Compile, run) := Some("Main")
  )
