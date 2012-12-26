package models

import models.Template._
import play.api.templates._

object DSL{

  def listPage(id:String) = new Page(id,None, html = defaultHtmlListTemplate)
  def detailPage(id:String) = new Page(id,None, html = defaultHtmlDetailTemplate)
  def masterDetailPage(id:String) = new Page(id,None, html = views.html.templates.masterDetail.apply _)
  def customPage(id:String,template:Template[Html]) = new Page(id,None,html=template)

  implicit class PageWithQueryOp(val page:Page) extends AnyVal{
    def withQuery(query: SimpleQuery) = new PageSimpleQueryOp(page,query)
    def withQuery(query: NamedQuery) = new PageNamedQueryOp(page,query)
    def and(newQuery:SimpleQuery) = new PageSimpleQueryOp(page,newQuery)
    def and(newQuery:NamedQuery) = new PageNamedQueryOp(page,newQuery)
    def withParameter(parameter:Parameter) = page.copy(parameters = page.parameters :+ parameter)
    def and(parameter:Parameter) = page.copy(parameters = page.parameters :+ parameter)  
    // TODO : must be upgraded
    def withAuthentication = page.copy(secured=true)
  }

  private[DSL] class PageNamedQueryOp(val page:Page, val query:NamedQuery) {
    def withColumn(col:(String,String)) =  new NamedQueryWithColumnOp(page,query,Column(col._1,col._2))
    def withQuery(newQuery:NamedQuery) = new PageNamedQueryOp(page.copy(namedQueries = page.namedQueries :+ query),newQuery)
    def withQuery(newQuery:SimpleQuery) = new PageSimpleQueryOp(page.copy(namedQueries = page.namedQueries :+ query),newQuery)
    def and(col:(String,String))=withColumn(col)
  }

  private[DSL] class PageSimpleQueryOp(val page:Page, val query:SimpleQuery) {
    def withColumn(col:(String,String)) =  new SimpleQueryWithColumnOp(page,query,Column(col._1,col._2))
    def withQuery(newQuery:NamedQuery) = new PageNamedQueryOp(page.copy(defaultQuery = Some(query)),newQuery)
    def withQuery(newQuery:SimpleQuery) = new PageSimpleQueryOp(page.copy(defaultQuery = Some(query)),newQuery)
    def and(col:(String,String))=withColumn(col)
  }

  private[DSL] class SimpleQueryWithColumnOp(val page:Page, val query:SimpleQuery, val col:Column){
    def and(c2:(String,String)) = new SimpleQueryWithColumnOp(page,query.copy(columns=query.columns :+ col),Column(c2._1,c2._2))
    def withQuery(newQuery:SimpleQuery) = new PageSimpleQueryOp(page.copy(defaultQuery = Some(query.copy(columns=query.columns :+ col))),newQuery)
    def withQuery(newQuery:NamedQuery) = new PageNamedQueryOp(page.copy(defaultQuery = Some(query.copy(columns=query.columns :+ col))),newQuery)
    def linkedTo(toPage:String) = new SimpleColumnLinkOp(page,query, col,Link(toPage))
  }

  private[DSL] class NamedQueryWithColumnOp(val page:Page, val query:NamedQuery, val col:Column){
    def and(c2:(String,String)) = new NamedQueryWithColumnOp(page,query.copy(columns=query.columns :+ col),Column(c2._1,c2._2))
    def withQuery(newQuery:SimpleQuery) = new PageSimpleQueryOp(page.copy(namedQueries = page.namedQueries :+ query.copy(columns=query.columns :+ col)),newQuery)
    def withQuery(newQuery:NamedQuery) = new PageNamedQueryOp(page.copy(namedQueries = page.namedQueries :+ query.copy(columns=query.columns :+ col)),newQuery)
    def linkedTo(toPage:String) = new NamedColumnLinkOp(page,query, col,Link(toPage))
  }

  implicit def pageSimpleOpToPage(op:PageSimpleQueryOp) = op.page.copy(defaultQuery = Some(op.query))
  implicit def pageNamedOpToPage(op:PageNamedQueryOp) = op.page.copy(namedQueries = op.page.namedQueries :+ op.query)
  implicit def simpleQueryWithcolumnOpToPage(op:SimpleQueryWithColumnOp) = op.page.copy(defaultQuery = Some(op.query.copy(columns=op.query.columns :+ op.col)))
  implicit def namedQueryWithcolumnOpToPage(op:NamedQueryWithColumnOp) = op.page.copy(namedQueries = op.page.namedQueries :+ op.query.copy(columns=op.query.columns :+ op.col))
  implicit def simpleColLinkOpToPage(op:SimpleColumnLinkOp) = op.page.copy(defaultQuery = Some(op.query.copy(columns=op.query.columns :+ op.column.copy(link=Some(op.link)))))
  implicit def namedColLinkOpToPage(op:NamedColumnLinkOp) = op.page.copy(namedQueries = op.page.namedQueries :+ op.query.copy(columns=op.query.columns :+ op.column.copy(link=Some(op.link))))

  implicit def stringToSimpleQuery(queryString:String) = SimpleQuery(queryString,Seq())

  implicit def tupleStringToNamedQuery(query:(String,String)) = NamedQuery(query._1,query._2,Seq())

  def url(name:String) = GetParameter(name)
  def form(name:String) = PostParameter(name)


  private[DSL] class SimpleColumnLinkOp(val page:Page, val query:SimpleQuery, val column:Column, val link:Link){
    def as(pName:String) = new SimpleColumnLinkOp(page, query, column, link.copy(parameterName = Some(pName)))
    def named(title:String) = new SimpleColumnLinkOp(page, query, column, link.copy(title = Some(title)))
    def and(c2:(String,String)) = new SimpleQueryWithColumnOp(page, query.copy(columns=query.columns :+ column.copy(link=Some(link))),Column(c2._1,c2._2))
  }

  private[DSL] class NamedColumnLinkOp(val page:Page, val query:NamedQuery, val column:Column, val link:Link){
    def as(pName:String) = new NamedColumnLinkOp(page, query, column, link.copy(parameterName = Some(pName)))
    def named(title:String) = new NamedColumnLinkOp(page, query, column, link.copy(title = Some(title)))
    def and(c2:(String,String)) = new NamedQueryWithColumnOp(page, query.copy(columns=query.columns :+ column.copy(link=Some(link))),Column(c2._1,c2._2))
  }
  
}