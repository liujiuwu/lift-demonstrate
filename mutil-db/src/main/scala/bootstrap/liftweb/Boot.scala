package bootstrap.liftweb

import net.liftweb.http.{ XHtmlInHtml5OutProperties, LiftRules, Req }
import net.liftweb.mongodb.{ MongoDB, DefaultMongoIdentifier, MongoAddress, MongoHost, MongoIdentifier }

class Boot {
  def boot {
    LiftRules.addToPackages("learn")

    LiftRules.htmlProperties.default.set((r: Req) => new XHtmlInHtml5OutProperties(r.userAgent))

    MongoDB.defineDb(DefaultMongoIdentifier, MongoAddress(MongoHost("localhost", 27017), "xxxxx"))

    MongoDB.defineDb(NNNNNMongoIdentifier, MongoAddress(MongoHost("localhost", 27017), "nnnnn"))
  }
}

object NNNNNMongoIdentifier extends MongoIdentifier {
  def jndiName = "nnnnn"
}
