package website

import models.Page
import collection.mutable.MutableList
import slick.session.Database
import play.Play

trait Website{
   def title:String
   def mainMenu:Map[String,String]
   def pages:List[Page]
   def authentication:String=null
   def dbUrl:String
   def dbDriver:String

   def getPage(key:String):Option[Page] = pages.find(_.id == key)
   def hasAuthentication:Boolean = authentication != null
}

object Site{

  /*
  TODO : use structural type ??
  type Website = {
    def title:String
    def mainMenu:Map[String,String]
    def pages:List[Page]
    def authentication:String
    def dbUrl:String
    def dbDriver:String
  }*/


  import play.api.Play.current
  lazy val WebSite:Website=Play.application().classloader().loadClass(play.api.Play.configuration.getString("sitedesc").getOrElse("website.SiteDesc")).newInstance.asInstanceOf[Website]

  implicit lazy val db = Database.forURL(WebSite.dbUrl, driver = WebSite.dbDriver)
}

