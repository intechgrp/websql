package controllers

import play.api._
import data.Form
import play.api.mvc._
import models._
import website.Site._
import website.Site.db
import play.api.data.Forms._

import slick.session.Database
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, SetParameter, GetResult}


object WebSQL extends Controller {

  // TODO : dummy parameter for dynamic routes in route file... another way ? 
  def getPageWithParameters(id: String, format: String = null, params:String = null) = page(id, format)
  def postPageWithParameters(id: String, format: String = null, params:String = null) = page(id, format)

  def getPage(id: String, format: String = null) = page(id, format)

  def postPage(id: String, format: String = null) = page(id, format)

  private def page(id: String, format: String = null) = Action {
    request =>
      WebSite.getPage(id) match {

        // Authentication required for this page, and not use in session : redirect to login page
        case Some(page: Page) if page.secured && WebSite.hasAuthentication && request.session.get("username").isEmpty =>
          Ok(views.html.authentication()).withSession("page" -> id)

        // Render the page according to requested format
        case Some(page: Page) =>
          val pageResult = PageResult.processPageQueries(PageRequest.fold(page, request))
          format match {
            case "xml" => Ok(page.xml(pageResult))
            case "csv" => Ok(page.csv(pageResult))
            case "json" => Ok(page.json(pageResult))
            case _ => Ok(page.html(pageResult))
          }

        // Page id not found
        case _ =>
          NotFound(views.html.error("page " + id + " not found"))
      }
  }

  
  def login = Action {
    implicit request =>
      Form(tuple(
        "username" -> text,
        "password" -> text
      )).bindFromRequest.fold(
      errors => BadRequest, {
        folder =>
          db withSession {
            Q.query(WebSite.authentication)
              .list(List(folder._1, folder._2))
              .headOption match {
              case None => Forbidden(views.html.authentication(Some("Incorrect username/password")))
              case Some(row) =>
                Redirect("/" + request.session.get("page").getOrElse(""))
                  .withSession("username" -> row("LOGIN").toString)
            }
          }
      }
      )
  }

  def logout = Action(request => Redirect("/").withNewSession)


  implicit val getQResult = GetResult(r => PageResult.convertPositionnedResult(r))

  // TODO May have to be modified to support all parameter types
  implicit val setQParam = SetParameter((s: List[String], p) => s.map(str => p.setString(str)))


}