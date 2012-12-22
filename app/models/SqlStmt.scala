package models

import play.api.db._

import play.api.Play.current
import anorm._
import scala.collection.Map

case class QueryResult(query:Query, result:List[Map[String,Any]])

case class PageResult(defaultQuery:Option[QueryResult], namedQueries:Map[String,QueryResult])

object PageResult{
  private def processQuery(query:Query, parameters:Seq[ParameterValue]):QueryResult = 
    QueryResult(query,
      DB.withConnection {
        implicit connection =>
          SQL(query.queryString).on(
            parameters.map(p => p.name -> anorm.ParameterValue(p.value.getOrElse(""), ToStatement.anyParameter)) :_*
          ).apply().map(_.asMap).toList
      }
    )

  def processPageQueries(request:PageRequest):PageResult = 
    PageResult(
      request.page.defaultQuery.map(processQuery(_,request.parameters)),
      request.page.namedQueries.map(q=>(q.name,processQuery(q,request.parameters))).toMap
    )
}

case class SqlStmt(result: List[Map[String, Any]], titles: Option[List[String]]) {
  def columns(): List[String] = result match {
    case Nil => List()
    case _ => result.head.keys.toList
  }

  def values(): List[List[Any]] = result.map(_.values.toList.map(
    v => v match {
      case Some(content) => content
      case item => item
    }
  ))

  def getTitles(): List[String] = titles match {
    case Some(theTitles) => theTitles
    case None => columns()
  }

  def getResult(): List[Map[String, Any]] = titles match {
    case None => result
    case Some(theTitles) => result map (row =>
      (row zip theTitles).map(item => item._2 -> (item._1._2 match {
        case Some(content) => content
        case item => item
      }
        ))
      )
  }
}

object SqlStmt {

  def runSelect(stmt: String, param: Option[String], titles: Option[List[String]], username: Option[String], parameters: List[(String, String)]): SqlStmt = {
    /*val queryResult = DB.withConnection {
      implicit connection =>
        /SQL(stmt).on(
          (List(
            "param" -> param.getOrElse(""),
            "username" -> username.getOrElse("")
          )
            ++ parameters)
            .map {
            p => p._1 -> ParameterValue(p._2, ToStatement.anyParameter)
          }: _*
        ).apply().map(_.asMap).toList
    }*/
    SqlStmt(null, titles)
  }

  def runSelect(stmt: String): SqlStmt = runSelect(stmt, None, None, None, List())
}
