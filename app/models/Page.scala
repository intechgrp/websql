package models


/**
 * Created with IntelliJ IDEA.
 * User: croiseaux
 * Date: 28/05/12
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */

case class Page(id: String, query: Option[String], template: (SqlStmt, Option[(String, String)], Option[String]) => play.api.templates.Html, detail: Option[(String, String)] = None, titles: Option[List[String]] = None, secured: Boolean = false) {

  def fromQuery(theQuery: String) = Page(id, Some(theQuery), template, detail, titles)

  def withTemplate(t: (SqlStmt, Option[(String, String)], Option[String]) => play.api.templates.Html) = Page(id, query, t, detail, titles)

  def withDetailPage(theId: String, theDetail: String) = Page(id, query, template, Some(theId, theDetail), titles)

  def withTitles(theTitles: List[String]) = Page(id, query, template, detail, Some(theTitles))

  def withAuthentication: Page = this.copy(secured = true)

  def html(param: Option[String], username: Option[String]) = {
    template(SqlStmt.runSelect(query.get, param, titles, username), detail, username)
  }

  def xml(param: Option[String]) = {
    val result = SqlStmt.runSelect(query.get, param, titles, None)
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
      "<" + id + ">" +
      result.getResult().map(row => "<item>" +
        row.map(el => "<" + el._1.replace(' ', '_') + ">" + el._2 + "</" + el._1.replace(' ', '_') + ">").mkString + "</item>"
      ).mkString +
      "</" + id + ">"
  }

  def json(param: Option[String]) = {
    val result = SqlStmt.runSelect(query.get, param, titles, None)
    "{\"" + id + "\":[" +
      result.getResult().map(row => "{" +
        row.map(el => "\"" + el._1 + "\":\"" + el._2 + "\"").mkString(",") + "}"
      ).mkString(",") +
      "]}"
  }
}

object Page {
  def ListPage(id: String) = Page(id, None, views.html.listResult(_, _, _))

  def DetailPage(id: String) = Page(id, None, views.html.detail(_, _, _))

  def TemplatePage(id: String, template: (SqlStmt, Option[(String, String)], Option[String]) => play.api.templates.Html) = Page(id, None, template)

}
