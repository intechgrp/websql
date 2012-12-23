package models

import play.api.db._

import play.api.Play.current
import anorm._
import scala.collection.Map

case class QueryResult(query:Query, result:List[Map[String,Any]]){

  def titles:List[(String,String)] =
    result match {
      case head +: _ =>
        val columnsMap = query.columns.map(c=>(c.queryCol,c.title)).toMap
        head.toList.map(header=>columnsMap.get(header._1) match {
          case Some(title) => (header._1,title)
          case _ => (header._1,header._1)
        })
      case _ => 
        List()
    }

  def data:List[List[(String,String,Option[Link])]] = {
    val columnsMap = query.columns.map(c=>(c.queryCol,c.link)).toMap
    result.map{line=>
      line.toList.map {col=>
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

case class PageResult(defaultQuery:Option[QueryResult], namedQueries:Map[String,QueryResult])

object PageResult{
  private def processQuery(query:Query, parameters:Seq[ParameterValue]):QueryResult = {
    QueryResult(query,
      DB.withConnection {
        implicit connection =>
          SQL(query.queryString).on(
            parameters.map(p => p.name -> anorm.ParameterValue(p.value.getOrElse(""), ToStatement.anyParameter)) :_*
          ).apply().map(_.asMap).toList
      }
    )
  }

  def processPageQueries(request:PageRequest):PageResult = 
    PageResult(
      request.page.defaultQuery.map(processQuery(_,request.parameters)),
      request.page.namedQueries.map(q=>(q.name,processQuery(q,request.parameters))).toMap
    )

  def linkToHtml(link:Link,col:(String,String)) = 
    """<a href="%s?%s">%s</a>""".format(
        link.toPage,
        link.parameterName.getOrElse(col._1)+"="+col._2,
        link.title.getOrElse(col._2)
      )
}