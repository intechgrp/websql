package controllers

import play.api._
import play.api.db._
import anorm._
import play.api.mvc._
import models._
import website.Site


object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def page(id: String) = Action {
    val thePage = Site.s.getPage(id)
    thePage match {
      case Some(p: Page) => Ok(p.template(SqlStmt.runSelect(p.query.get), p.detail))
      case None => NotFound("**** Page " + id + " non trouvée ****÷\n" + Site.s.toString())
    }
  }

  def pageWithParm(id: String, param: String) = Action {
    val thePage = Site.s.getPage(id)
    thePage match {
      case Some(p: Page) => Ok(p.template(SqlStmt.runSelect(p.query.get, param), p.detail))
      case None => NotFound
    }
  }

}