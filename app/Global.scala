import play.api._
import play.api.Play._
import play.api.db._
import anorm._
import play.api.Play.current



object Global extends GlobalSettings {

  def insertTestData() {
    DB.withConnection { implicit c =>
      SQL("insert into Client(nom, prenom, adresse) values ({nom}, {prenom}, {adresse})")
        .on("nom" -> "CROISEAUX", "prenom" -> "Fabrice", "adresse" -> "12 rue Claude Monet").executeInsert()
      SQL("insert into Client(nom, prenom, adresse) values ({nom}, {prenom}, {adresse})")
        .on("nom" -> "DETANTE", "prenom" -> "Antoine", "adresse" -> "1 rue des Roses").executeInsert()
    }
  }

  override def onStart(app: Application) {
    if(!isProd)
      insertTestData() // Insert test data
  }

}
