package models

/**
 * Created with IntelliJ IDEA.
 * User: croiseaux
 * Date: 28/05/12
 * Time: 20:40
 * To change this template use File | Settings | File Templates.
 */

case class Page(id: String, query: Option[String], pageType:String="DETAIL") {

  def fromQuery(theQuery: String) = Page(id, Some(theQuery), pageType)

}

object Page {
  def ListPage(id:String) = Page(id, None, "LIST")

  def DetailPage(id:String) = Page(id, None, "DETAIL")

}
