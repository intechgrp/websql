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


object Application extends Controller {

  def index = Action {
    Ok(views.html.index())
  }

  def editSiteDesc = Action {
    Ok(views.html.editSiteDesc(website.SiteUtils.siteDescSource))
  }

  def saveSiteDesc = Action {request=>
    println(request.body)
    request.body.asFormUrlEncoded.map{
      _.get("source") match {
        case Some(source +: _) => 
          website.SiteUtils.flushSiteDescSource(source)
        case _ => 
      }
    }
    Redirect("/editSite")
  }


}