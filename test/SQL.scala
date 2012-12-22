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

    "Execute default query and get result for default page query" in {

      val page = Page("test",Some(SimpleQuery("select a,b from T where a={g1}",Seq())),Seq(),Seq(GetParameter("g1")))

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
        val pageResult = PageResult.processPageQueries(
          PageRequest.fold(
            page,
            FakeRequest("GET","/controller?g1=1")
          )
        )
        pageResult.defaultQuery must beSome
        pageResult.defaultQuery.get.result.size must be equalTo(2)
      }

    }

  }

  private val fakeApplication = FakeApplication(
    additionalConfiguration = inMemoryDatabase() + ("evolutionplugin" -> "disabled")
  )


}