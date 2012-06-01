package website

import models.Page
import models.Page._
import collection.mutable.MutableList


class Site {
  val pages: MutableList[Page] = MutableList[Page]()
  var menu: Map[String, String] = Map[String, String]()

  def addPage(p: Page) = {
    pages += p
  }

  def addPages(lp: List[Page]) = {
    pages ++= lp
  }

  def getPage(theId: String): Option[Page] = pages.find(_.id == theId)

  override def toString() = pages.toString()


}

object Site {

  val mainMenu = Map[String, String](
    "Liste Clients" -> "clients",
    "Liste Comptes" -> "comptes"
  )

  val sitePages = List[Page](
    DetailPage("client") fromQuery "Select * from Client where id = {param}"
      withTitles List[String]("Identifiant", "Nom du gars", "Prénom", "Adresse"),
    ListPage("clients") fromQuery "Select *  from Client"
      withDetailPage("CLIENT.ID", "client")
      withTitles List[String]("Identifiant", "Nom", "Prénom", "Adresse"),
    ListPage("comptes") fromQuery "Select * from Compte"
  )

  val s: Site = new Site

  {
    s.addPages(sitePages)
    s.menu = mainMenu
  }


}
