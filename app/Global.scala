import play.api._
import play.api.Play._
import slick.session.{Database}
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q}

import models.PageResult._

import play.api.Play.current


object Global extends GlobalSettings {


  def insertTestData() {
    Database.forURL("jdbc:h2:mem:websql", driver = "org.h2.Driver") withSession {
          if(Q.queryNA("select * from client").list.size == 0){
            (Q.u + "insert into Client(nom, prenom, adresse, login) values('CROISEAUX', 'Fabrice', '12 rue Claude Monet', 'fcx')").execute
            (Q.u + "insert into Client(nom, prenom, adresse, login) values('DETANTE', 'Antoine', '1 rue des Roses', 'ade')").execute

            (Q.u + "insert into Compte(iban, description, solde, client) values('1234-564321', 'Compte Couant', 1000.0, 1)").execute
            (Q.u + "insert into Compte(iban, description, solde, client) values('6547-567294', 'Compte Epargne', 1234.5, 1)").execute
            (Q.u + "insert into Compte(iban, description, solde, client) values('1123-984702', 'Compte Epargne', 10000.0, 2)").execute

            (Q.u + "insert into USER (LOGIN,PASSWORD,USERNAME) values('ade', 'adepassword', 'Antoine')").execute
            (Q.u + "insert into USER (LOGIN,PASSWORD,USERNAME) values('fcx', 'password', 'Fabrice')").execute
          }
      }
  }

  override def onStart(app: Application) {
    if (!isProd && !isTest)
      insertTestData() // Insert test data
  }

}
