package website

import java.io.FileWriter
import play.api.libs.json._


object SiteUtils {

  val sourceUrl = "./app/website/SiteDesc.scala"

  // Return the source of the site description in Scala
  def siteDescSource = scala.io.Source.fromFile(sourceUrl).mkString

  def flushSiteDescSource(source: String) = {
    val json = Json.parse(source).as[List[String]]

    val fw = new FileWriter(sourceUrl)
    json.map(line =>
        fw.write(line + "\r\n")
      )
    fw.close()
  }

}
