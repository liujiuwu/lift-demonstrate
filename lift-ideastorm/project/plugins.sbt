resolvers += Classpaths.typesafeResolver

libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % (if (v == "0.11.0") {v + "-0.2.8"} else {v + "-0.2.11"}))

//addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse" % "1.5.0")
