package models

import play.api.mvc.Content
import play.api.templates._

object Template{

 type Template[A <: Content] = PageResult => A

 val defaultHtmlListTemplate:Template[Html]   = views.html.templates.list.apply _
 val defaultHtmlDetailTemplate:Template[Html] = views.html.templates.detail.apply _

 val defaultXmlTemplate:Template[Xml] = new Template[Xml]{
  def apply(result:PageResult) = 
    Xml("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" +
    "<" + result.page.id + ">" +
    (result.defaultQuery.map{query=>
      query.data.map(row => 
        "<item>" +
          row.map(el => "<" + el._1.replace(' ', '_') + ">" + el._2 + "</" + el._1.replace(' ', '_') + ">").mkString + "</item>"
      ).mkString
    }.getOrElse("")) + 
    "</" + result.page.id + ">")
 }

 val defaultCsvTemplate = new Template[Txt]{
  def apply(result:PageResult) = 
    Txt(result.defaultQuery.map{query=>
      query.titles.map(_._2).mkString(";") + "\n" +
      query.data.map(_.map(_._2).mkString(";")).mkString("\n")
    }.getOrElse(""))
  }

  val defaultJsonTemplate = new Template[Txt]{
    def apply(result:PageResult) = 
      Txt("{\"" + result.page.id + "\":[" +
      (
        result.defaultQuery.map{query=>
          query.data.map(row => "{" +
            row.map(el => "\"" + el._1 + "\":\"" + el._2 + "\"").mkString(",") + "}"
          ).mkString(",")
        }.getOrElse("")
      ) + "]}")
  }

}