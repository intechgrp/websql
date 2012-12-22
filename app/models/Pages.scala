package models

case class Column(queryCol:String, title:String, link:Option[Link]=None)

case class Link(toPage:String, parameterName:Option[String]=None, title:Option[String]=None)

trait Query{
  def queryString:String
  def columns:Seq[Column]
}

case class NamedQuery(name:String, queryString:String,columns:Seq[Column]) extends Query
case class SimpleQuery(queryString:String,columns:Seq[Column]) extends Query

trait Parameter{
  def name:String
}

case class PostParameter(name:String) extends Parameter
case class GetParameter(name:String) extends Parameter

case class Page(
    id:String,
    defaultQuery:Option[SimpleQuery],
    namedQueries:Seq[NamedQuery]=Seq[NamedQuery](),
    parameters:Seq[Parameter]=Seq()
){
  def html(param: Option[String], username:Option[String], parameters:List[(String,String)]) = "TODO"
  def xml(param: Option[String]) = "TODO"
  def json(param: Option[String]) = "TODO"
  def csv(param: Option[String]) = "TODO"
}
