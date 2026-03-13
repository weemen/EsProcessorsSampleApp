ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.1"

lazy val esProcessors = uri("https://github.com/weemen/es-processors.git#v0.0.1")

lazy val root = (project in file("."))
  .settings(
    name := "EsProcessorsSampleApp",
    libraryDependencies ++= Seq(
      "org.apache.pekko" %% "pekko-actor-typed" % "1.1.2",
      "org.apache.pekko" %% "pekko-actor"       % "1.1.2",
      "org.apache.pekko" %% "pekko-stream"      % "1.1.2",
      "org.apache.pekko" %% "pekko-slf4j"       % "1.1.2",
      "ch.qos.logback"    % "logback-classic"   % "1.5.16"
    )
  )
  .dependsOn(ProjectRef(esProcessors, "pekko-processors-sagas"))
