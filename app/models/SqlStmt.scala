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

  def values(): List[List[Any]] = result.map(_.values.toList)

  def getTitles(): List[String] = titles match {
    case Some(theTitles) => theTitles
    case None => columns()
  }

  def getResult(): List[Map[String, Any]] = titles match {
    case None => result
    case Some(theTitles) => result map (row =>
      (row zip theTitles).map(item => item._2 -> item._1._2)
      )
  }
}

object SqlStmt {
  def runSelect(stmt: String, titles: Option[List[String]]): SqlStmt = {
    val queryResult = DB.withConnection {
      implicit connection =>
        SQL(stmt).apply().map(_.asMap).toList
    }
    SqlStmt(queryResult, titles)
  }

  def runSelect(stmt: String, param: String, titles: Option[List[String]]): SqlStmt = {
    val queryResult = DB.withConnection {
      implicit connection =>
        SQL(stmt).on("param" -> param).apply().map(_.asMap).toList
    }
    SqlStmt(queryResult, titles)
  }

  def runSelect(stmt: String): SqlStmt = runSelect(stmt, None)
}
