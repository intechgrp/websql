package website

import models.Page
import models.Page._

object Site {
  val pages = List[Page](
    ListPage("clients") fromQuery "Select *  from Client",
    DetailPage("client") fromQuery "Select * from Client where id = {param}"
  )
}
