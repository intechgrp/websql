package views.helpers

import models.SqlStmt
import util.parsing.combinator.RegexParsers
import scala.collection.Map
import play.api.templates.Html

/**
 * Created with IntelliJ IDEA.
 * User: antoine
 * Date: 01/06/12
 * Time: 10:43
 */

object Table {
  import Parsers._

  private case class Column(header: String, name: String)
  private case class Link(column:String, pageDest:String, columnId:String)

  private def table(sql: SqlStmt, headersString: String, columnsString: String, tableClass: Option[String], linksString: Option[String]): Html = {
    play.api.templates.Html(
      // Parse input strings
       (parseColumns(headersString),
        parseColumns(columnsString),
        linksString.map{parseLinks(_)}
        ) match {
        // Check columns, headers and links lists
        case (ColsParser.Failure(err, _),_ ,_) => error("Cannot parse table headers : " + err)
        case (_, ColsParser.Failure(err, _),_) => error("Cannot parse table columns : " + err)
        case (_,_,Some(LinksParser.Failure(err,_))) => error("Cannot parse table links : " + err)
        case (ColsParser.Success(headers, _), ColsParser.Success(columnNames, _), parsedLinks) =>
          assert(headers.size == columnNames.size, "table headers and table columns must have same number of elements")
          // Construct the list of columns
          val columns = headers.zip(columnNames).map {
            parsed => Column(parsed._1, parsed._2)
          }
          // Construct the map of links
          val links:Map[String,Link] = parsedLinks match {
            case Some(LinksParser.Success(result,_)) => result.map{l=>(l.column,l)}.toMap
            case _ => Map()
          }

          // Render
          startTable(tableClass) +
          tableHeader(columns) +
          tableBody(columns, links, sql.result) +
          endTable
      }
    )
  }

  // Public exposed function, with tuned byname-parameters (Options removed)
  def table(sql: SqlStmt, headersString: String, columnsString: String, withClass: String=null, withLinks:String=null): Html =
    table(sql, headersString, columnsString,
      withClass match{case null => None case _ => Some(withClass)},
      withLinks match {case null => None case _ => Some(withLinks)}
    )

  private def startTable(className: Option[String]) = "<table class=\"" + className.getOrElse("") + "\">"
  private def tableHeader(headers: List[Column]) =
    "<thead><tr>" +
      headers.foldLeft("") {(acc, col) => acc + "<th>" + col.header + "</th>"} +
    "</tr></thead>"

  private def tableBody(columns: List[Column], links: Map[String,Link], data: List[Map[String, Any]]) =
    "<tbody>" +
      data.foldLeft("") {(acc, line) => acc + tableLine(columns, links, line)} +
    "</tbody>"

  private def tableLine(columns: List[Column], links:Map[String, Link], data: Map[String, Any]) =
    "<tr>" +
      columns.foldLeft("") {(acc, col) =>
        acc +
        "<td>" +
        links.get(col.name).map{link=>
          "<a href=\"/"+link.pageDest+"/"+data.get(link.columnId).getOrElse("")+"\">"+data.get(col.name).getOrElse("")+"</a>"}
        .getOrElse(data.get(col.name).getOrElse("")) +
        "</td>"
      } +
    "</tr>"

  private def endTable = "</table>"

  private def error(msg: String) = "<h3 class=\"error\">" + msg + "</h3>"


  // Expression parsers
  private object Parsers{
    object ColsParser extends RegexParsers {
      def start: Parser[String] = """\[""".r    // Start with [
      def end: Parser[String] = """\]""".r      // End with ]
      def sep: Parser[String] = """\|""".r      // Fields separator
      def col: Parser[String] = """[^\[\]|]+""".r <~ (sep | end) ^^ (_.trim)

      def columns: Parser[List[String]] = start ~> col.* // Extract all columns
    }

    def parseColumns(str:String)=ColsParser.parse(ColsParser.columns,str)

    object LinksParser extends RegexParsers {
      def start: Parser[String] = """\[""".r    // Start with [
      def end: Parser[String] = """\]""".r      // End with ]
      def sep: Parser[String] = """\|""".r      // Fields separator

      def colFrom: Parser[String] = """((?!->).)+""".r ^^ (_.trim)
      def pageName: Parser[String] = """[^\(]+""".r ^^ (_.trim)
      def colId: Parser[String]= """[^\)]+""".r ^^ (_.trim)
      def arrow: Parser[String]="""->""".r
      def parOpen: Parser[String]="""\(""" .r
      def parClosed: Parser[String]="""\)""" .r

      // Extract one link
      def link:Parser[Link]=(colFrom <~ arrow) ~ (pageName <~ parOpen) ~ (colId <~ parClosed) <~ (sep | end) ^^ {
        case  from ~ page ~ id=> Link(from,page,id)
      }

      // Extract all links
      def links:Parser[List[Link]]=start ~> link.*
    }

    def parseLinks(str:String)=LinksParser.parse(LinksParser.links,str)

  }

}

