package controllers

import play.api._
import data.Form
import play.api.mvc._
import models._
import website.Site._
import play.api.data.Forms._

import slick.session.{PositionedResult, Database}
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, SetParameter, GetResult}
import collection.mutable


object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def getPage(id: String, format: String = null) = page(id, format)

  def postPage(id: String, format: String = null) = page(id, format)

  private def page(id: String, format: String = null) = Action {
    request =>
      WebSite.getPage(id) match {

        // Authentication required for this page, and not use in session : redirect to login page
        case Some(page: Page) if page.secured && WebSite.authentication.isDefined && request.session.get("username").isEmpty =>
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

  def editSiteDesc = Action {
    Ok(views.html.editSiteDesc(website.SiteUtils.siteDescSource))
  }

  def saveSiteDesc(source: String) = Action {
    website.SiteUtils.flushSiteDescSource(source)
    Redirect("/editSite")
  }

  def login = Action {
    implicit request =>
      Form(tuple(
        "username" -> text,
        "password" -> text
      )).bindFromRequest.fold(
      errors => BadRequest, {
        folder =>
          Database.forURL(WebSite.dbUrl, driver = WebSite.dbDriver) withSession {
            Q.query(WebSite.authentication.get)
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

  def logout = Action(request => Redirect(routes.Application.index()).withNewSession)


  implicit val getQResult = GetResult(r => convertPositionnedResult(r))

  implicit val setQParam = SetParameter((s: List[String], p) => s.map(str => p.setString(str)))

  private def convertPositionnedResult(r: PositionedResult): Map[String, Any] = {
    val m = mutable.MutableList[(String, Any)]()
    while (r.hasMoreColumns)
      m.+=(r.rs.getMetaData.getColumnName(r.currentPos + 1) -> r.nextObject())
    m.toMap
  }

}