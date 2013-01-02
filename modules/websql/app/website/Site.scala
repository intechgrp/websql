package website

import models.Page
import models.Template._
import collection.mutable.MutableList
import slick.session.Database
import play.Play
import play.api.templates.Html

trait Website{
   // Mandatory
   def title:String
   def mainMenu:Map[String,String]
   def pages:List[Page]
   def dbUrl:String
   def dbDriver:String

   // Optional
   def authentication:String=null
   def mainTemplate:(Option[String] => Html => Html)=views.html.main.apply _

   // Utilities
   def getPage(key:String):Option[Page] = pages.find(_.id == key)
   def hasAuthentication:Boolean = authentication != null
}

object Site{
  
  import play.api.Play.current
  lazy val WebSite:Website=Play.application().classloader().loadClass(play.api.Play.configuration.getString("sitedesc").getOrElse("website.SiteDesc")).newInstance.asInstanceOf[Website]

  implicit lazy val db = Database.forURL(WebSite.dbUrl, driver = WebSite.dbDriver)
}

