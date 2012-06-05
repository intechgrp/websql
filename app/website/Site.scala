package website

import models.Page
import models.Page._
import collection.mutable.MutableList


case class Site(title:String, authentication:Option[String]=None) {
  val pages: MutableList[Page] = MutableList[Page]()
  var menu: Map[String, String] = Map[String, String]()

  def addPage(p: Page) = {
    pages += p
  }

  def addPages(lp: List[Page]) = {
    pages ++= lp
  }

  def getPage(theId: String): Option[Page] = pages.find(_.id == theId)

}

object Site {

  val WebSite: Site = new Site(SiteDesc.title,SiteDesc.authentication)

  {
    WebSite.addPages(SiteDesc.pages)
    WebSite.menu = SiteDesc.mainMenu
  }


}
