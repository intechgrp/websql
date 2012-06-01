package models

/**
 * Created with IntelliJ IDEA.
 * User: croiseaux
 * Date: 28/05/12
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */

case class Page(id: String, query: Option[String], template: (SqlStmt, Option[(String, String)]) => play.api.templates.Html, detail: Option[(String, String)] = None, titles:Option[List[String]] = None) {

  def fromQuery(theQuery: String) = Page(id, Some(theQuery), template, detail, titles)

  def withTemplate(t: (SqlStmt, Option[(String, String)]) => play.api.templates.Html) = Page(id, query, t, detail, titles)

  def withDetailPage(theId: String, theDetail: String) = Page(id, query, template, Some(theId, theDetail), titles)

  def withTitles(theTitles: List[String]) = Page(id, query, template, detail, Some(theTitles))

}

object Page {
  def ListPage(id: String) = Page(id, None, views.html.listResult(_, _))

  def DetailPage(id: String) = Page(id, None, views.html.detail(_, _))

}
