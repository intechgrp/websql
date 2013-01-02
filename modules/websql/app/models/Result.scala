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


case class PageResult(page: Page, defaultQuery: Option[QueryResult], namedQueries: Map[String, QueryResult], parameters: Seq[ParameterValue], authenticatedUser:Option[String]) {
  private val parameterMap = parameters.collect {
    case ParameterValue(pName, Some(pValue)) => (pName, pValue)
  }.toMap

  def parameter(pName: String): String = parameterMap.get(pName).getOrElse("")
}

object PageResult {
  type QResult = Map[String, Any]

  implicit val getQResult = GetResult(r => convertPositionnedResult(r))

  def convertPositionnedResult(r: PositionedResult): Map[String, Any] = {
    val m = mutable.MutableList[(String, Any)]()
    while (r.hasMoreColumns)
      m.+=(r.rs.getMetaData.getColumnName(r.currentPos+1) -> r.nextObject())
    m.toMap
  }

  private def processQuery(query: Query, parameters: Seq[ParameterValue],db:Database): QueryResult = {
    // Create a map to be able to easily retrieve parameter value from its name
    val parmMap = parameters.map(pv => pv.name -> pv.value.getOrElse("")).toMap

    def fillParams(s: Seq[ParameterValue], p: PositionedParameters): Unit = {
      query.paramNames.map(s => p.setString(parmMap(s)))
    }

    //used by Slick to feed the SqlStatement from the sequence of ParameterValue
    implicit val setQParam = SetParameter((s: Seq[ParameterValue], p) => fillParams(s, p))

    QueryResult(query,
      db withSession {
        Q.query(query.normQuery).list(parameters)
      }
    )
  }

  def processPageQueries(request: PageRequest)(implicit db:Database): PageResult =
    PageResult(
      request.page,
      request.page.defaultQuery.map(processQuery(_, request.parameters,db)),
      request.page.namedQueries.map(q => (q.name, processQuery(q, request.parameters,db))).toMap,
      request.parameters,
      request.user
    )


}