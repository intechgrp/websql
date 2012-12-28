package website

import models.Page
import collection.mutable.MutableList
import slick.session.Database

case class Site(title:String, authentication:Option[String]=None) {
  val pages: MutableList[Page] = MutableList[Page]()
  var menu: Map[String, String] = Map[String, String]()
  var dbUrl:String = ""
  var dbDriver:String = ""

  def addPage(p: Page) = {
    pages += p
  }

  def addPages(lp: List[Page]) = {
    pages ++= lp
  }

  def getPage(theId: String): Option[Page] = pages.find(_.id == theId)

}

object Site {

  val WebSite: Site = new Site(SiteDesc.title,SiteDesc.authentication match { case null => None case _ => Some(SiteDesc.authentication)})
          
  {
    WebSite.addPages(SiteDesc.pages)
    WebSite.menu = SiteDesc.mainMenu
    WebSite.dbUrl = SiteDesc.dbUrl
    WebSite.dbDriver = SiteDesc.dbDriver
  }

  implicit val db = Database.forURL(WebSite.dbUrl, driver = WebSite.dbDriver)

}
