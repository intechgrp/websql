package views.helpers

import models.Link

object Helpers{

  import play.api.templates.Html
  def showLink(link:Link,col:(String,String)) = 
    Html("""<a href="%s?%s">%s</a>""".format(
        link.toPage,
        link.parameterName.getOrElse(col._1)+"="+col._2,
        link.title.getOrElse(col._2)
      ))

}