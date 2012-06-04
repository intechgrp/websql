package controllers

import play.api._
import play.api.mvc._
import models._
import website.Site._

import java.io.File

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  private def page(id: String, param: Option[String], format: Option[String]) = Action {
    val thePage = WebSite.getPage(id)
    thePage match {
      case Some(p: Page) =>
        format match {
          case Some("html") => Ok(p.html(param))
          case Some("xml") => Ok(p.xml(param)).as("text/xml")
          case Some("json") => Ok(p.json(param)).as("application/json")
          case Some(other) => NotAcceptable("Invalid format : " + other)
        }
      case None => NotFound("**** Page " + id + " non trouvée ****÷\n" + WebSite.toString())
    }
  }

  def page(id: String, param: String = null, format: String = null): Action[AnyContent] = page(id, param match {
    case null => None
    case _ => Some(param)
  }, format match {
    case null => None
    case _ => Some(format)
  })

  def editSiteDesc = Action {
    Ok(views.html.editSiteDesc(website.SiteUtils.siteDescSource))
  }

  def saveSiteDesc(source: String) = Action {
    website.SiteUtils.flushSiteDescSource(source)
    Redirect("/editSite")
  }
}