package models
import models.Template._
import play.api.templates._

case class Column(queryCol:String, title:String, link:Option[Link]=None)

case class Link(toPage:String, parameter:Option[Parameter]=None, title:Option[String]=None)

trait Query{
  def queryString:String
  def columns:Seq[Column]

  // Replace {nomvar} by ? in the query
  val parmPattern = "\\{[a-zA-Z0-9]*\\}".r
  lazy val normQuery = parmPattern.replaceAllIn(queryString, "?")
  //  Build a list of ordered parameter names
  lazy val paramNames = parmPattern.findAllIn(queryString).map(s => s.substring(1, s.length - 1)).toList

}

case class NamedQuery(name:String, queryString:String,columns:Seq[Column]) extends Query
case class SimpleQuery(queryString:String,columns:Seq[Column]) extends Query

trait Parameter{
  def name:String
  def defaultValue:Option[String]
}

case class PostParameter(name:String,defaultValue:Option[String]=None) extends Parameter
case class GetParameter(name:String,defaultValue:Option[String]=None) extends Parameter {
  def withDefaultValue(value:String) = this.copy(defaultValue=Some(value))
}
case class PathParameter(name:String,defaultValue:Option[String]=None) extends Parameter

case class Page(
    id:String,

    defaultQuery:Option[SimpleQuery],
    namedQueries:Seq[NamedQuery]=Seq[NamedQuery](),

    parameters:Seq[Parameter]=Seq(),
    secured:Boolean=false,
    
    html:Template[Html]=defaultHtmlListTemplate,
    xml:Template[Xml]=defaultXmlTemplate,
    csv:Template[Txt]=defaultCsvTemplate,
    json:Template[Txt]=defaultJsonTemplate
)
