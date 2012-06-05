package website

import models.Page
import models.Page._

object SiteDesc {

  val title = "POC WebSql"

  val authentication=Some("select USERNAME from USER where LOGIN = {login} and PASSWORD = {password}")

  val mainMenu = Map[String, String](
    "Liste des Clients" -> "clients",
    "Liste des Comptes" -> "comptes",
    "Liste Comptes/Client" -> "accounts"
  )

  val pages = List[Page](
    DetailPage("client") fromQuery "Select * from Client where id = {param}"
      withTitles List[String]("Identifiant", "Nom du gars", "Prénom", "Adresse"),
    DetailPage("compte") fromQuery "Select * from Compte where id = {param}",  
    ListPage("clients") fromQuery "Select *  from Client"
      withDetailPage("CLIENT.ID", "client")
      withTitles List[String]("Identifiant", "Nom", "Prénom", "Adresse"),
    ListPage("comptes") fromQuery "Select * from Compte" withDetailPage("COMPTE.ID", "compte"),

    TemplatePage("accounts", views.html.accounts(_, _)) fromQuery "Select * from Client" withAuthentication,

    ListPage("clientAccounts") fromQuery "Select * from Compte where client = {param}"
  )

}








