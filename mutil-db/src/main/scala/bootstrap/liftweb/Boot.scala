package bootstrap.liftweb

import net.liftweb.http.{ XHtmlInHtml5OutProperties, LiftRules, Req }
import net.liftweb.mongodb.{ MongoDB, DefaultMongoIdentifier, MongoAddress, MongoHost, MongoIdentifier }

class Boot {
  def boot {
    LiftRules.addToPackages("learn")

    LiftRules.htmlProperties.default.set((r: Req) => new XHtmlInHtml5OutProperties(r.userAgent))

    MongoDB.defineDb(IdentifierFactory("xxxxx"), MongoAddress(MongoHost("localhost", 27017), "xxxxx"))

    MongoDB.defineDb(IdentifierFactory("nnnnn"), MongoAddress(MongoHost("localhost", 27017), "nnnnn"))
  }
}

object IdentifierFactory {
  def apply(name: String) = is(name)

  private var is =
    Map("nnnnn" -> new MongoIdentifier { def jndiName = "nnnnn" },
      "xxxxx" -> new MongoIdentifier { def jndiName = "xxxxx" })

  def put(identifier: TraversableOnce[(String, MongoIdentifier)]) {
    is ++= identifier
  }

  def put(identifier: (String, MongoIdentifier)*) {
    is ++= identifier
  }
}
