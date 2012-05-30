import models.SqlStmt
import play.api._
import play.api.Play._
import play.api.db._
import anorm._
import play.api.Play.current


object Global extends GlobalSettings {

  def insertTestData() {
    if (SqlStmt.runSelect("Select * from client").values() isEmpty)
      DB.withConnection {
        implicit c =>
          SQL("insert into Client(nom, prenom, adresse) values ({nom}, {prenom}, {adresse})")
            .on("nom" -> "CROISEAUX", "prenom" -> "Fabrice", "adresse" -> "12 rue Claude Monet").executeInsert()
          SQL("insert into Client(nom, prenom, adresse) values ({nom}, {prenom}, {adresse})")
            .on("nom" -> "DETANTE", "prenom" -> "Antoine", "adresse" -> "1 rue des Roses").executeInsert()
          SQL("insert into Compte(iban, description, solde, client) values ({iban}, {description}, {solde}, {client})")
            .on("iban" -> "1234-564321", "description" -> "Compte Courant", "solde" -> 1000, "client" -> 1).executeInsert()
          SQL("insert into Compte(iban, description, solde, client) values ({iban}, {description}, {solde}, {client})")
            .on("iban" -> "6547-567294", "description" -> "Compte Epargne", "solde" -> 199.99, "client" -> 1).executeInsert()
      }
  }

  override def onStart(app: Application) {
    if (!isProd)
      insertTestData() // Insert test data
  }

}
