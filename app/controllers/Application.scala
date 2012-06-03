package controllers

import play.api._
import play.api.db._
import anorm._
import play.api.mvc._
import models._
import website.Site._


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  private def page(id: String, param: Option[String]) = Action {
    val thePage = WebSite.getPage(id)
    thePage match {
      case Some(p: Page) => Ok(p.display(param))
      case None => NotFound("**** Page " + id + " non trouvée ****÷\n" + WebSite.toString())
    }
  }

  def page(id: String, param: String = null): Action[AnyContent] = page(id, param match {
    case null => None
    case _ => Some(param)
  })

}