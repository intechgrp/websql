package website

import models.Page
import models.Page._

object SiteDesc {

  val title = "POC WebSql"

  val mainMenu = Map[String, String](
    "Liste des Clients" -> "clients",
    "Liste des Comptes" -> "comptes",
    "Noms clients" -> "nomcli",
    "Liste Comptes/Client" -> "accounts"
  )

  val pages = List[Page](
    DetailPage("client") fromQuery "Select * from Client where id = {param}"
      withTitles List[String]("Identifiant", "Nom du gars", "Prénom", "Adresse"),
    ListPage("clients") fromQuery "Select *  from Client"
      withDetailPage("CLIENT.ID", "client")
      withTitles List[String]("Identifiant", "Nom", "Prénom", "Adresse"),
    ListPage("comptes") fromQuery "Select * from Compte",
    ListPage("nomcli") fromQuery "Select nom from client",

    TemplatePage("accounts", views.html.accounts(_, _)) fromQuery "Select * from Client",

    ListPage("clientAccounts") fromQuery "Select * from Compte where client = {param}"
  )

}




