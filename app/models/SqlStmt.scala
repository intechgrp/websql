package models

import play.api.db._

import play.api.Play.current
import scala.collection.Map
import anorm._

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
    val queryResult = DB.withConnection {
      implicit connection =>
        SQL(stmt).on(
          (List(
            "param" -> param.getOrElse(""),
            "username" -> username.getOrElse("")
          )
            ++ parameters)
            .map {
            p => p._1 -> ParameterValue(p._2, ToStatement.anyParameter)
          }: _*
        ).apply().map(_.asMap).toList
    }
    SqlStmt(queryResult, titles)
  }

  def runSelect(stmt: String): SqlStmt = runSelect(stmt, None, None, None, List())
}
