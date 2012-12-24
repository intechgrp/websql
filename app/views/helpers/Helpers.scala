package views.helpers

import models._

object Helpers{

  import play.api.templates.Html
  def showLink(link:Link,col:(String,String)):Html = 
    Html("""<a href="%s?%s">%s</a>""".format(
        link.toPage,
        link.parameterName.getOrElse(col._1)+"="+col._2,
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
  

}