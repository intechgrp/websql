package models

import play.api.mvc.{Request,AnyContent}

case class ParameterValue(name:String, value:Option[String])

object ParameterValue{

  private val PathParameterExtractor = "/.*/([^\\?]*).*".r

  // TODO : handle other request bodies, like JSON, XML,...
  def fromRequest(parameter:Parameter,request:Request[AnyContent]):ParameterValue = 
    ParameterValue(
      parameter.name,
      parameter match {
        case GetParameter(paramName,default) =>
          request.queryString.get(paramName) match {
            case None =>
              default
            case Some(list) if list.size == 0 =>
              default
            case Some(Seq("")) =>
              default
            case Some(list) =>
              Some(list.head)
          }

        case PostParameter(paramName,_) =>
          request.body.asFormUrlEncoded.map{formData=>
              for (values <- formData.get(paramName)) yield values.head
            }.getOrElse(None)
        case PathParameter(paramName,_) =>
          request.uri match {
            case PathParameterExtractor(value)  => Some(value)
            case _                              =>  None
          }
        case _ => None
      }
    )

  def fromRequest(parameters:Seq[Parameter],request:Request[AnyContent]):Seq[ParameterValue] = parameters.map(fromRequest(_,request))

}

case class PageRequest(page:Page,parameters:Seq[ParameterValue],user:Option[String])

object PageRequest{

  def fold(page:Page, request:Request[AnyContent]) = PageRequest(
    page,
    ParameterValue.fromRequest(page.parameters,request)
    ++ (
    // If page request authentication, add the username parameter from request session
        if(page.secured)
          List(ParameterValue("username",request.session.get("username")))
        else 
          List()
    ),
    request.session.get("username")
  )

}