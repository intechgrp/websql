import org.specs2.mutable._
import models._
import play.api.test.FakeRequest
import play.api.test._
import play.api.test.Helpers._


class Parameters extends Specification {

  "The WebSQL parameter system" should {

    "Return a value for a get parameter" in {
      val result = ParameterValue.fromRequest(
        GetParameter("p1"),
        FakeRequest("GET","/controller?p1=v1")
      )
      result must be equalTo(ParameterValue("p1",Some("v1")))
    }

    "Return None for a non-existant get parameter" in {
      val result = ParameterValue.fromRequest(
        GetParameter("p1"),
        FakeRequest("GET","/controller?p2=v2")
      )
      result must be equalTo(ParameterValue("p1",None))
    }

    "Return a value for a path parameter" in {
      running(fakeApplication) {
        val result = ParameterValue.fromRequest(
          PathParameter("test"),
          FakeRequest("GET","/controller/value")
        )
        result must be equalTo(ParameterValue("test",Some("value")))
      }
    }

    "Return None for a non-existant path parameter" in {
      running(fakeApplication) {
        val result = ParameterValue.fromRequest(
          PathParameter("test"),
          FakeRequest("GET","/controller")
        )
        result must be equalTo(ParameterValue("test",None))
      }
    }

    "Return values for a list of get parameters" in {
      val results = ParameterValue.fromRequest(
        Seq(
          GetParameter("p1"),
          GetParameter("p2")
        ),
        FakeRequest("GET","/controller?p1=v1&p2=v2")
      )
      results.size must be equalTo(2)

      getParameterValue("p1",results) must beSome.which(_ == ParameterValue("p1",Some("v1")))
      getParameterValue("p2",results) must beSome.which(_ == ParameterValue("p2",Some("v2")))
    }

    "Return a value for a post parameter" in {
      val result = ParameterValue.fromRequest(
        PostParameter("p1"),
        FakeRequest("POST","/controller").withFormUrlEncodedBody("p1" ->  "v1")
      )

      result must be equalTo(ParameterValue("p1",Some("v1")))
    }

    "Return None for a non-existant post parameter" in {
      val result = ParameterValue.fromRequest(
        PostParameter("p1"),
        FakeRequest("POST","/controller").withFormUrlEncodedBody("p2" ->  "v2")
      )
      result must be equalTo(ParameterValue("p1",None))
    }

    "Return values for a list of post parameters" in {
      val results = ParameterValue.fromRequest(
        Seq(
          PostParameter("p1"),
          PostParameter("p2")
        ),
        FakeRequest("POST","/controller").withFormUrlEncodedBody(
          "p1" -> "v1",
          "p2" -> "v2",
          "p3" -> "v3"
        )
      )

      results.size must be equalTo(2)

      getParameterValue("p1",results) must beSome.which(_ == ParameterValue("p1",Some("v1")))
      getParameterValue("p2",results) must beSome.which(_ == ParameterValue("p2",Some("v2")))
    }

    "Return correct values for get, post and path parameters" in {
      val results = ParameterValue.fromRequest(
        Seq(
          GetParameter("g1"),
          GetParameter("g2"),
          GetParameter("g4"),
          PostParameter("p1"),
          PostParameter("p2"),
          PostParameter("p4"),
          PathParameter("path")
        ),
        FakeRequest("POST", "/controller/pathValue?g1=vg1&g2=vg2&g3=vg3").withFormUrlEncodedBody(
          "p1" -> "vp1",
          "p2" -> "vp2",
          "p3" -> "vp3"
        )
      )
      results.size must be equalTo(7)
      
      getParameterValue("g1",results) must beSome.which(_ == ParameterValue("g1",Some("vg1")))
      getParameterValue("g2",results) must beSome.which(_ == ParameterValue("g2",Some("vg2")))
      getParameterValue("g4",results) must beSome.which(_ == ParameterValue("g4",None))
      getParameterValue("p1",results) must beSome.which(_ == ParameterValue("p1",Some("vp1")))
      getParameterValue("p2",results) must beSome.which(_ == ParameterValue("p2",Some("vp2")))
      getParameterValue("p4",results) must beSome.which(_ == ParameterValue("p4",None))
      getParameterValue("path",results) must beSome.which(_ == ParameterValue("path",Some("pathValue")))
    }

    "Fold a PageRequest from a Page and a FakeRequest" in {
      val result = PageRequest.fold(
        Page(
          "test",
          Some(SimpleQuery("select * from T",Seq())),
          Seq(),
          Seq(
            GetParameter("g1"),
            GetParameter("g2"),
            PostParameter("p1"),
            PostParameter("p2")
          )
        ),
        FakeRequest("POST", "/controller?g1=vg1").withFormUrlEncodedBody("p1" -> "vp1")
      )
      result.parameters.size must be equalTo(4)
      getParameterValue("g1",result.parameters) must beSome.which(_ == ParameterValue("g1",Some("vg1")))
      getParameterValue("g2",result.parameters) must beSome.which(_ == ParameterValue("g2",None))
      getParameterValue("p1",result.parameters) must beSome.which(_ == ParameterValue("p1",Some("vp1")))
      getParameterValue("p2",result.parameters) must beSome.which(_ == ParameterValue("p2",None))
    }

  }

  private def getParameterValue(name:String, results:Seq[ParameterValue]):Option[ParameterValue] = results.find(_.name == name)

  private val fakeApplication = FakeApplication(
    additionalConfiguration = inMemoryDatabase() + ("evolutionplugin" -> "disabled")
  )

}