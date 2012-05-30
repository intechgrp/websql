package website

import models.Page
import models.Page._


object Site {
  val pages = List[Page](
    ListPage("clients") fromQuery "Select *  from Client",
    ListPage("comptes") fromQuery "Select * from Compte",
    DetailPage("client") fromQuery "Select * from Client where id = {param}"
  )

  val menu = Map[String, String](
    "Liste Clients" -> "clients",
    "Liste Comptes" -> "comptes"
  )

}
