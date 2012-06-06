package models


import models.Forms.Form
import play.api.templates.Html

/**
 * Created with IntelliJ IDEA.
 * User: croiseaux
 * Date: 28/05/12
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */

sealed class Page(val id: String, val secured:Boolean=false){
  def withAuthentication:Page=new Page(id, true)
  def html(param:Option[String], username:Option[String])=Html("")
  def xml(param:Option[String])=""
  def json(param:Option[String])=""
}

case class PageView(pId:String, query: Option[String], template: (SqlStmt, Option[(String, String)], Option[String]) => play.api.templates.Html, detail: Option[(String, String)] = None, titles: Option[List[String]] = None) extends Page(pId,false) {

  def fromQuery(theQuery: String) = PageView(id, Some(theQuery), template, detail, titles)

  def withTemplate(t: (SqlStmt, Option[(String, String)], Option[String]) => play.api.templates.Html) = PageView(id, query, t, detail, titles)

  def withDetailPage(theId: String, theDetail: String) = PageView(id, query, template, Some(theId, theDetail), titles)

  def withTitles(theTitles: List[String]) = PageView(id, query, template, detail, Some(theTitles))

  override def html(param: Option[String], username:Option[String]) = {
    template(SqlStmt.runSelect(query.get, param, titles, username), detail,username)
  }

  override def xml(param: Option[String]) = {
    val result = SqlStmt.runSelect(query.get, param, titles, None)
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
    "<" + id + ">" +
      result.getResult().map(row => "<item>" +
        row.map(el => "<" + el._1.replace(' ', '_') + ">" + el._2 + "</" + el._1.replace(' ', '_') + ">").mkString + "</item>"
      ).mkString +
      "</" + id + ">"
  }

  override def json(param: Option[String]) = {
    val result = SqlStmt.runSelect(query.get, param, titles, None)
    "{\"" + id + "\":[" +
      result.getResult().map(row => "{" +
        row.map(el => "\"" + el._1 + "\":\"" + el._2 + "\"").mkString(",") + "}"
      ).mkString(",") +
      "]}"
  }
}

case class PageForm(pId:String, query:Option[String], form:Option[Form], template: (PageForm, Option[String])=> play.api.templates.Html, resultPage:Option[String]=None) extends Page(pId,false){
  def withForm(form:Form)=copy(form=Some(form))
  def fromQuery(query:String)=copy(query=Some(query))
  override def html(param: Option[String], username:Option[String]) = template(this,username)
  def withResultPage(result: String) = copy(resultPage=Some(result) )
}

object Page {
  def ListPage(id: String) = PageView(id, None, views.html.listResult(_, _, _))

  def DetailPage(id: String) = PageView(id, None, views.html.detail(_, _, _))

  def TemplatePage(id: String, template: (SqlStmt, Option[(String, String)], Option[String]) => play.api.templates.Html) = PageView(id, None, template)

  def CreatePage(id: String) = PageForm(id, None, None, views.html.create(_,_))

}
