import org.specs2.mutable._
import models._
import play.api.test.FakeRequest
import play.api.test._
import play.api.test.Helpers._
import anorm._
import play.api.db._
import play.api.Play.current
  
class SQL extends Specification {

  "The WebSQL SQL engine" should {

    "Execute queries from pages" in {


      running(fakeApplication) {
        DB.withConnection { implicit connection =>
          SQL("CREATE TABLE T (a VARCHAR, b VARCHAR)").executeUpdate
          SQL("INSERT INTO T VALUES ('1','First')").executeUpdate
          SQL("INSERT INTO T VALUES ('1','Premier')").executeUpdate
          SQL("INSERT INTO T VALUES ('2','Second')").executeUpdate
          SQL("INSERT INTO T VALUES ('2','Second')").executeUpdate
          SQL("INSERT INTO T VALUES ('3','Third')").executeUpdate
          SQL("INSERT INTO T VALUES ('3','Troisieme')").executeUpdate
          SQL("INSERT INTO T VALUES ('4','Last')").executeUpdate
          SQL("INSERT INTO T VALUES ('4','Dernier')").executeUpdate
        }

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

  private val fakeApplication = FakeApplication(
    additionalConfiguration = inMemoryDatabase() + ("evolutionplugin" -> "disabled")
  )


}