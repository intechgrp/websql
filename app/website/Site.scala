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

  val s: Site = new Site

  {
    val client = DetailPage("client") fromQuery "Select * from Client where id = {param}"
    s.addPage(client)
    val clients = ListPage("clients") fromQuery "Select *  from Client" withDetailPage ("CLIENT.ID", "client")
    s.addPage(clients)
    val comptes = ListPage("comptes") fromQuery "Select * from Compte"
    s.addPage(comptes)
    s.menu = mainMenu
  }


}
