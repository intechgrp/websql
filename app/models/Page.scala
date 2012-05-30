package models

/**
 * Created with IntelliJ IDEA.
 * User: croiseaux
 * Date: 28/05/12
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */

case class Page(id: String, query: Option[String], template: (SqlStmt, Option[(String, String)]) => play.api.templates.Html, detail: Option[(String, String)]) {

  def fromQuery(theQuery: String) = Page(id, Some(theQuery), template, detail)

  def withTemplate(t: (SqlStmt, Option[(String, String)]) => play.api.templates.Html) = Page(id, query, t, detail)

  def withDetailPage(theId: String, detail: String) = Page(id, query, template, Some(theId, detail))

}

object Page {
  def ListPage(id: String) = Page(id, None, views.html.listResult(_, _), None)

  def DetailPage(id: String) = Page(id, None, views.html.detail(_, _), None)

}
