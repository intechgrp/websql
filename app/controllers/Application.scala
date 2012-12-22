package controllers

import play.api._
import data.Form
import db.DB
import play.api.mvc._
import models._
import website.Site._
import play.api.data.Forms._

import play.api.Play.current
import anorm._
import website.SiteUtils

object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  private def page(id: String, param: Option[String], format: Option[String], query:List[(String,String)]):(RequestHeader=>Result) = {request=>
    val thePage = WebSite.getPage(id)
    thePage match {
      /*case Some(p:Page) if p.secured && WebSite.authentication.isDefined && request.session.get("username").isEmpty =>
        Ok(views.html.authentication()).withSession("page"->id,"param"->param.getOrElse(""))*/
      case Some(p: Page) =>
        format match {
          case Some("html") => Ok(p.html(param,request.session.get("username"),query))
          case Some("xml")  => Ok(p.xml(param)).as("text/xml")
          case Some("json") => Ok(p.json(param)).as("application/json")
          case Some("csv") => Ok(p.csv(param)).as("text/csv")
          case _            => NotAcceptable("Invalid format : " + format.getOrElse("not set"))
        }
      case None => NotFound(views.html.error("page " + id + " not found"))
    }
  }

  def getPage(id: String, param: String = null, format: String = null): Action[AnyContent] = Action{ request=> 
    page(id, param match {
      case null => None
      case _ => Some(param)
    }, format match {
      case null => None
      case _ => Some(format)
    },request.queryString.toList.map(p=>(p._1,p._2(0))))(request)
  }

  def postPage(id:String, format:String) = Action{
    request => 
    val params=
      request.body.asFormUrlEncoded match {
        case Some(p) =>
          p.toList.filter(_._2.size>0).map(v=>(v._1,v._2.head))
        case None => List()
      }
    page(id, 
      None,
      format match {
        case null=> None
        case _ => Some(format)
      },
      params
    )(request)
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
          DB.withConnection {
            implicit connection =>
              SQL(WebSite.authentication.get)
                .on("login" -> folder._1, "password" -> folder._2)
                .apply().headOption match {
                case None => Forbidden(views.html.authentication(Some("Incorrect username/password")))
                case Some(row) =>
                  Redirect("/" + request.session.get("page").getOrElse("") + request.session.get("param").filter(_.length() > 0).map("/" + _).getOrElse(""))
                    .withSession("username" -> row.data(0).toString)
              }
          }
      }
      )
  }

  def logout = Action(request => Redirect(routes.Application.index()).withNewSession)
}