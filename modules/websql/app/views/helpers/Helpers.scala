package views.helpers

import models._

object Helpers{

  import play.api.templates.Html
  def showLink(link:Link,col:(String,String)):Html = 
    Html("""<a href="%s%s">%s</a>""".format(
        link.toPage,
        link.parameter.collect{
          case GetParameter(pName,_)  => "?" + pName + "=" + col._2
          case PathParameter(_,_)     => "/" + col._2
        }.getOrElse("?" + col._1 + "=" + col._2),
        link.title.getOrElse(col._2)
      ))

  def table(query:QueryResult):Html = 
    Html(""" <table class="table table-bordered">
          <thead>
            <tr>
    """ + 
    (for(title <- query.titles) yield "<td>%s</td>".format(title._2)).mkString("") +
    """
            </tr>
          </thead>
          <tbody>
    """ +
    (for (line <- query.data) yield (
    """     <tr>""" + 
      (for (col <- line) yield (
        "<td>" + (col._3 match {
            case Some(link) => showLink(link,(col._1,col._2))
            case _ => col._2
        }) + "</td>"
      )).mkString("\n") + 
    """     </tr>"""
    )).mkString("") + 
    """   </tbody>
      </table>
    """)

  def table(queryOpt:Option[QueryResult]):Html = queryOpt.map(table(_)).getOrElse(Html(""))
  
  def select(name:String, label:String, value:String, queryOpt:Option[QueryResult])(implicit pageResult:PageResult):Html = {
    val selectedValue = pageResult.parameter(name)
    Html("<select name=\"%s\">\n".format(name)
    +"<option value=\"\"></option>" // empty value
    + queryOpt.map{query=>
      (
        for(line <- query.result)
        yield {
          val colValue = line.get(value).map(_.toString).getOrElse("")
          val colLabel = line.get(label).map(_.toString).getOrElse("")
          "<option " + 
            (if (colValue == selectedValue) 
              "selected=\"selected\" " 
            else "") + "value=\"%s\">%s</option>\n".format(colValue,colLabel)
        }
      ).mkString("\n")
    }.getOrElse("")
    + "</select>\n")
  }

  def select(name:String,label:String, value:String, query:QueryResult)(implicit pageResult:PageResult):Html = select(name,label,value,Some(query))
  def select(name:String,value:String, queryOpt:Option[QueryResult])(implicit pageResult:PageResult):Html = select(name,value,value,queryOpt)
  def select(name:String,value:String, query:QueryResult)(implicit pageResult:PageResult):Html = select(name,value,Some(query))

}