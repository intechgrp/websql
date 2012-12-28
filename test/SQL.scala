import org.specs2.mutable._
import models._
import play.api.test.FakeRequest
import play.api.test._
import play.api.test.Helpers._
import slick.session.{Database}
import slick.jdbc.{StaticQuery => Q}
import Database.threadLocalSession
import play.api.Play.current
  
class SQL extends Specification {

  implicit val db = Database.forURL("jdbc:h2:mem:websql", driver = "org.h2.Driver")

  "The WebSQL SQL engine" should {

    "Execute queries from pages" in {


      running(fakeApplication) {
        db withSession {
          (Q.u + "CREATE TABLE T (a VARCHAR, b VARCHAR)").execute
          (Q.u + "INSERT INTO T VALUES ('1','First')").execute
          (Q.u + "INSERT INTO T VALUES ('1','Premier')").execute
          (Q.u + "INSERT INTO T VALUES ('2','Second')").execute
          (Q.u + "INSERT INTO T VALUES ('2','Second')").execute
          (Q.u + "INSERT INTO T VALUES ('3','Third')").execute
          (Q.u + "INSERT INTO T VALUES ('3','Troisieme')").execute
          (Q.u + "INSERT INTO T VALUES ('4','Last')").execute
          (Q.u + "INSERT INTO T VALUES ('4','Dernier')").execute


          PageResult.processPageQueries(
            PageRequest.fold(
              Page("test",Some(SimpleQuery("select a,b from T where a={g1}",Seq())),Seq(),Seq(GetParameter("g1"))),
              FakeRequest("GET","/controller?g1=1")
            )
          ).defaultQuery must beSome.which(_.result.size == 2)


          PageResult.processPageQueries(
            PageRequest.fold(
              Page("test",Some(SimpleQuery("select a,b from T where a={g1}",Seq())),Seq(),Seq(GetParameter("g1"))),
              FakeRequest("GET","/controller?g1=0")
            )
          ).defaultQuery must beSome.which(_.result.size == 0)

          PageResult.processPageQueries(
            PageRequest.fold(
              Page("test",Some(SimpleQuery("select a from T",Seq())),Seq(),Seq()),
              FakeRequest("GET","/controller")
            )
          ).defaultQuery must beSome.which(r=>r.result.size == 8 && r.result(0).size == 1)


          var result=PageResult.processPageQueries(
            PageRequest.fold(
              Page("test",
                Some(SimpleQuery("select a from T",Seq())),
                Seq(NamedQuery("second","select a,b from T where a='4'",Seq())),
              Seq()),
              FakeRequest("GET","/controller")
            )
          )
          result.defaultQuery must beSome.which(r=>r.result.size == 8 && r.result(0).size == 1)
          result.namedQueries.get("second") must beSome.which(r=>r.result.size == 2)

        }
      }

    }

  }

  private val fakeApplication = FakeApplication(
    additionalConfiguration = inMemoryDatabase() + ("evolutionplugin" -> "disabled")
  )


}