package models

import play.api.db._

import play.api.Play.current
import scala.collection.Map
import anorm._

case class SqlStmt(result:List[Map[String, Any]]) {
  def columns():List[String] = result.head.keys.toList
  def values():List[List[Any]] = result.map(_.values.toList)
}

object SqlStmt {
  def runSelect(stmt: String):SqlStmt = {
    val queryResult = DB.withConnection {
      implicit connection =>
        SQL(stmt).apply().map(_.asMap).toList
    }
    SqlStmt(queryResult)
  }

  def runSelect(stmt: String, param: String):SqlStmt = {
    val queryResult = DB.withConnection {
      implicit connection =>
        SQL(stmt).on("param" -> param).apply().map(_.asMap).toList
    }
    SqlStmt(queryResult)
  }
}
