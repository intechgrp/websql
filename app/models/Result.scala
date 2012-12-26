package models

import play.api.Play.current
import collection.{mutable, Map}
import slick.session.{PositionedParameters, PositionedResult, Database}
import Database.threadLocalSession
import slick.jdbc.{StaticQuery => Q, SetParameter, GetResult}
import scala.Predef._
import website.Site._
import scala.Some



case class QueryResult(query: Query, result: List[Map[String, Any]]) {

  def titles: List[(String, String)] =
    result match {
      case head +: _ =>
        val columnsMap = query.columns.map(c => (c.queryCol, c.title)).toMap
        head.toList.map(header => columnsMap.get(header._1) match {
          case Some(title) => (header._1, title)
          case _ => (header._1, header._1)
        })
      case _ =>
        List()
    }

  def data: List[List[(String, String, Option[Link])]] = {
    val columnsMap = query.columns.map(c => (c.queryCol, c.link)).toMap
    result.map {
      line =>
        line.toList.map {
          col =>
            (
              col._1,
              col._2 match {
                case Some(str) => str.toString
                case _ => col._2.toString
              },
              columnsMap.get(col._1) match {
                case Some(optLink) => optLink
                case _ => None
              }
              )
        }
    }
  }

}


case class PageResult(page: Page, defaultQuery: Option[QueryResult], namedQueries: Map[String, QueryResult], parameters: Seq[ParameterValue]) {
  private val parameterMap = parameters.collect {
    case ParameterValue(pName, Some(pValue)) => (pName, pValue)
  }.toMap

  def parameter(pName: String): String = parameterMap.get(pName).getOrElse("")
}

object PageResult {
  type QResult = Map[String, Any]

  implicit val getQResult = GetResult(r => convertPositionnedResult(r))

  private def convertPositionnedResult(r: PositionedResult): Map[String, Any] = {
    val m = mutable.MutableList[(String, Any)]()
    while (r.hasMoreColumns)
      m.+=(r.rs.getMetaData.getColumnName(r.currentPos+1) -> r.nextObject())
    m.toMap
  }

  private def processQuery(query: Query, parameters: Seq[ParameterValue]): QueryResult = {
    // Replace {nomvar} by ? in the query
    val parmPattern = "\\{[a-zA-Z0-9]*\\}".r
    val normQuery = parmPattern.replaceAllIn(query.queryString, "?")
    //  Build a list of ordered parameter names
    val params = parmPattern.findAllIn(query.queryString).map(s => s.substring(1, s.length - 1)).toList
    // Create a map to be able to easily retrive parameter value from its name
    val parmValues = parameters.map(pv => pv.name -> pv.value.getOrElse("")).toMap

    def fillParams(s: Seq[ParameterValue], p: PositionedParameters): Unit = {
      params.map(s => p.setString(parmValues(s)))
    }

    //used by Slick to feed the SqlStatement from the sequence of ParameterValue
    implicit val setQParam = SetParameter((s: Seq[ParameterValue], p) => fillParams(s, p))

    QueryResult(query,
      Database.forURL(WebSite.dbUrl, driver = WebSite.dbDriver) withSession {
        Q.query(normQuery).list(parameters)
      }
    )
  }

  def processPageQueries(request: PageRequest): PageResult =
    PageResult(
      request.page,
      request.page.defaultQuery.map(processQuery(_, request.parameters)),
      request.page.namedQueries.map(q => (q.name, processQuery(q, request.parameters))).toMap,
      request.parameters
    )


}