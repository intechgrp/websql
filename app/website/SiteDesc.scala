package website

import models.Page
import models.Page._
import models.Forms._

object SiteDesc {

  val title = "POC WebSql"

  val authentication="select LOGIN from USER where LOGIN = {login} and PASSWORD = {password}"

  val mainMenu = Map[String, String](
    "Liste des Clients" -> "clients",
    "Nouveau client" -> "newClient",
    "Liste des Comptes" -> "comptes",
    "Mes comptes" -> "myAccounts"
  )

  val pages = List[Page](
    DetailPage("client") fromQuery "Select * from Client where id = {param}"
      withTitles List[String]("Identifiant", "Nom du gars", "Prénom", "Adresse", "Login"),
    DetailPage("compte") fromQuery "Select * from Compte where id = {param}",  
    ListPage("clients") fromQuery "Select *  from Client"
      withDetailPage("CLIENT.ID", "client")
      withTitles List[String]("Identifiant", "Nom", "Prénom", "Adresse", "Login"),
    ListPage("comptes") fromQuery "Select * from Compte" withDetailPage("COMPTE.ID", "compte"),

    ListPage("myAccounts")
      fromQuery "Select COMPTE.IBAN,COMPTE.DESCRIPTION,COMPTE.SOLDE,COMPTE.DEVISE from COMPTE, CLIENT where COMPTE.CLIENT = CLIENT.ID and CLIENT.LOGIN = {username}"
      withTitles List[String]("IBAN","Description","Solde","Devise")
      withAuthentication,

    CreatePage("newClient")
      withForm Form(
        Field("nom") ofType text withConstraint isMandatory,
        Field("prenom") ofType text withConstraint isMandatory,
        Field("address") ofType text
      )
      fromQuery "insert into CLIENT (NOM,PRENOM,ADRESSE) VALUES ({nom},{prenom},{address})"
      withResultPage("clients")
  )

}








