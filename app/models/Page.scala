package models

/**
 * Created with IntelliJ IDEA.
 * User: croiseaux
 * Date: 28/05/12
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */

case class Page(id: String, query: Option[String], template:SqlStmt=>play.api.templates.Html) {

  def fromQuery(theQuery: String) = Page(id, Some(theQuery), template)

  def withTemplate(t:SqlStmt=>play.api.templates.Html) = Page(id, query, t)

}

object Page {
  def ListPage(id:String) = Page(id, None, views.html.listResult(_))

  def DetailPage(id:String) = Page(id, None, views.html.detail(_))

}
