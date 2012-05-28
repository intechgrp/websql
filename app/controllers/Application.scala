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
    val thePage = Site.pages.find(_.id == id)
    thePage match {
      case Some(p: Page) =>
        p.pageType match {
          case "LIST" => Ok(views.html.listResult(SqlStmt.runSelect(p.query.get)))
          case "DETAIL" => Ok(views.html.detail(SqlStmt.runSelect(p.query.get)))
          case _ => NotFound
        }
      case None => NotFound
    }
  }

  def pageWithParm(id: String, param: String) = Action {
    val thePage = Site.pages.find(_.id == id)
    thePage match {
      case Some(p: Page) =>
        p.pageType match {
          case "LIST" => Ok(views.html.listResult(SqlStmt.runSelect(p.query.get, param)))
          case "DETAIL" => Ok(views.html.detail(SqlStmt.runSelect(p.query.get, param)))
          case _ => NotFound
        }
      case None => NotFound
    }
  }

}