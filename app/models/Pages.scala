package models

case class Column(queryCol:String, title:String, link:Option[Link]=None)

case class Link(toPage:String, parameterName:Option[String]=None, title:Option[String]=None)

trait Query{
  def queryString:String
  def columns:Seq[Column]
  def columns(cols:Seq[Column]):Query
}

case class NamedQuery(name:String, queryString:String,columns:Seq[Column]) extends Query{
  def columns(cols:Seq[Column])=copy(columns = cols)
}
case class SimpleQuery(queryString:String,columns:Seq[Column]) extends Query{
  def columns(cols:Seq[Column])=copy(columns = cols)
}

trait Parameter{
  def name:String
}

case class PostParameter(name:String) extends Parameter
case class GetParameter(name:String) extends Parameter

case class Page(
    id:String,
    queries:Seq[Query]=Seq(),
    parameters:Seq[Parameter]=Seq()
){
  def html(param: Option[String], username:Option[String], parameters:List[(String,String)]) = "TODO"
  def xml(param: Option[String]) = "TODO"
  def json(param: Option[String]) = "TODO"
  def csv(param: Option[String]) = "TODO"
}

object Page{

  def page(id:String) = new Page(id)

  implicit class PageWithQueryOp(val page:Page) extends AnyVal{
    def withQuery(query: SimpleQuery) = new PageQueryOp(page,query)
    def withQuery(query: NamedQuery) = new PageQueryOp(page,query)
    def and(newQuery:SimpleQuery) = new PageQueryOp(page,newQuery)
    def and(newQuery:NamedQuery) = new PageQueryOp(page,newQuery)
    def withParameter(parameter:Parameter) = page.copy(parameters = page.parameters :+ parameter)
    def and(parameter:Parameter) = page.copy(parameters = page.parameters :+ parameter)  
  }

  private[Page] class PageQueryOp[A <: Query](val page:Page, val query:A) {
    def withColumn(col:(String,String)) = 
      new QueryWithColumnOp(page,query,Column(col._1,col._2))
    def withQuery(newQuery:Query) = new PageQueryOp(page.copy(queries = page.queries :+ query),newQuery)
    def and(col:(String,String))=withColumn(col)
  }

  private[Page] class QueryWithColumnOp[A <: Query](val page:Page, val query:A, val col:Column){
    def and(c2:(String,String)) = new QueryWithColumnOp(page,query.columns(query.columns :+ col),Column(c2._1,c2._2))
    def withQuery(newQuery:Query) = new PageQueryOp(page.copy(queries = page.queries :+ query.columns(query.columns :+ col)),newQuery)
    def linkedTo(toPage:String) = new ColumnLinkOp(page,query, col,Link(toPage))
  }

  implicit def pageOpToPage[A <: Query](op:PageQueryOp[A]) = op.page.copy(queries = op.page.queries :+ op.query)
  implicit def queryWithcolumnOpToPage[A <: Query](op:QueryWithColumnOp[A]) = op.page.copy(queries = op.page.queries :+ op.query.columns(op.query.columns :+ op.col))
  implicit def columnLinkOpToPage[A <: Query](op:ColumnLinkOp[A]) = op.page.copy(queries = op.page.queries :+ op.query.columns(op.query.columns :+ op.column.copy(link=Some(op.link))))

  implicit def stringToSimpleQuery(queryString:String) = SimpleQuery(queryString,Seq())

  implicit def tupleStringToNamedQuery(query:(String,String)) = NamedQuery(query._1,query._2,Seq())

  def url(name:String) = GetParameter(name)
  def form(name:String) = PostParameter(name)


  private[Page] class ColumnLinkOp[A <: Query](val page:Page, val query:A, val column:Column, val link:Link){
    def as(pName:String) = new ColumnLinkOp(page, query, column, link.copy(parameterName = Some(pName)))
    def named(title:String) = new ColumnLinkOp(page, query, column, link.copy(title = Some(title)))
    def and(c2:(String,String)) = new QueryWithColumnOp(page, query.columns(query.columns :+ column.copy(link=Some(link))),Column(c2._1,c2._2))
  }
  
}