import org.specs2.mutable._
import models._
import models.Page._
import models.DSL._

class DSL extends Specification {

  "The WebSQL page-dsl" should {
    
    "Create a page" in {
      page("test") must not beNull
    }

    "Create a Simple Query" in {
      val query:Query = "select * from T"
      query.queryString.length must be greaterThan(0)
    }

    "Create a Named Query" in {
      val query:NamedQuery = "test" -> "select * from T"
      query.queryString.length must be greaterThan(0)
      query.name must be equalTo("test")
    }

   "Create a page with simple query" in {
      val res = page("test") withQuery "select * from T"
      res.defaultQuery must beSome
    }

    "Create a page with named query" in {
      val res = page("test") withQuery "list" -> "select * from T"
      res.namedQueries.size must be equalTo(1)
    }

    "Create a page with multiples queries" in {
      val res = 
        page("test") withQuery 
          "select * from T" withQuery 
          "test1" ->  "select * from T1" withQuery
          "test2" -> "select * from T2"

      res.namedQueries.size must be equalTo(2)
      res.defaultQuery must beSome
    }

    "Create a page with queries and titles" in {
      val res = 
        page("test") withQuery
          "select c from T" withQuery
          "first" -> "select a,b from T" withColumn "a" -> "Col A" and "b" -> "Col B" withQuery
          "last" -> "select c from T" withColumn "c" -> "Col C"
      
      res.namedQueries.size must be equalTo(2)
      res.namedQueries(0).columns.size must be equalTo(2)
    }

    "Create a page with parameters" in {
      val res = 
        page("test") withParameter
          url("id") and
          form("name")

      res.parameters.size must be equalTo(2)
    }

    "Create a page with a simple link" in {
      val res = 
        page("test") withQuery
          "select c from T" withColumn "a" -> "A" linkedTo "otherPage"

      res.defaultQuery must beSome
      res.defaultQuery.get.columns.size must be equalTo(1)
      res.defaultQuery.get.columns(0).link must beSome
    }

    "Create a page with a multiple links" in {
      val res = 
        page("test") withQuery
          "select a,b,c,d from T" withColumn "a" -> "Title of A" linkedTo "otherPage" and "b" -> "Title of B" and "c" -> "Title of C" linkedTo "destPage" and "d" -> "Title of D"

      res.defaultQuery must beSome
      res.defaultQuery.get.columns.size must be equalTo(4)
      res.defaultQuery.get.columns(0).link must beSome
      res.defaultQuery.get.columns(1).link must beNone
      res.defaultQuery.get.columns(2).link must beSome
      res.defaultQuery.get.columns(3).link must beNone
    }

    "Create a page with link with custom parameter name" in {
      val res =
        page("test") withQuery 
          "select a,b,c from T" withColumn "a" -> "Col A" linkedTo "aPage" and "b" -> "Col B" linkedTo "anotherPage" as "newParam" and "c" -> "Col C"
      res.defaultQuery.get.columns(0).link must beSome
      res.defaultQuery.get.columns(0).link.get.parameterName must beNone
      res.defaultQuery.get.columns(1).link must beSome
      res.defaultQuery.get.columns(1).link.get.parameterName must beSome
    }

    "Create a page with link with custom title" in {
      val res =
        page("test") withQuery 
          "select a,b,c from T" withColumn "a" -> "Col A" linkedTo "aPage" and "b" -> "Col B" linkedTo "anotherPage" named "newParam" and "c" -> "Col C"
      res.defaultQuery.get.columns(0).link must beSome
      res.defaultQuery.get.columns(0).link.get.title must beNone
      res.defaultQuery.get.columns(1).link.get.title must beSome
    }

    "Put all things together" in {
      val res =

        page("test") withParameter
          url("min") and
          url("max") and 
          form("user") withQuery
          "select * from users where min > {min} and max < {max}" withQuery
          "secondQuery" -> "select fName,lName,id from user where user = {user}" withColumn
            "fName" -> "First name" and "lName" -> "Last name" and "id" -> "Detail" linkedTo "detailPage" as "id" named "Detail"

      res.defaultQuery must beSome
      res.namedQueries.size must be equalTo(1)
      res.parameters.size must be equalTo(3)
    }
  }

}