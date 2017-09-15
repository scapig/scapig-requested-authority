package tapi

import models._
import play.api.http.Status
import play.api.libs.json.Json
import play.mvc.Http.HeaderNames.CONTENT_TYPE
import models.JsonFormatters._

import scalaj.http.Http

class RequestedAuthoritySpec extends BaseFeatureSpec {

  val authorityRequest = AuthorityRequest("clientId", Seq("scope"), "/redirectUri", AuthType.PRODUCTION)
  val authorityUpdateRequest = AuthorityUpdateRequest("userId")

  feature("create and fetch requested authority") {

    scenario("happy path") {

      When("A requested authority create request is received")
      val createdResponse = Http(s"$serviceUrl/authority")
        .headers(Seq(CONTENT_TYPE -> "application/json"))
        .postData(Json.toJson(authorityRequest).toString()).asString

      Then("I receive a 200 (Ok) with the new requested authority")
      createdResponse.code shouldBe Status.OK
      val newRequestedAuthority = Json.parse(createdResponse.body).as[RequestedAuthority]

      And("The requested authority can be retrieved")
      val fetchResponse = Http(s"$serviceUrl/authority/${newRequestedAuthority.id}").asString
      fetchResponse.code shouldBe Status.OK
      Json.parse(fetchResponse.body) shouldBe Json.toJson(newRequestedAuthority)
    }

    scenario("update requested authority") {
      Given("A requested authority exists")
      val createdResponse = Http(s"$serviceUrl/authority")
        .headers(Seq(CONTENT_TYPE -> "application/json"))
        .postData(Json.toJson(authorityRequest).toString()).asString
      val requestedAuthority = Json.parse(createdResponse.body).as[RequestedAuthority]

      When("I update the requested authority with the userId")
      val updateResponse = Http(s"$serviceUrl/authority/${requestedAuthority.id}")
        .headers(Seq(CONTENT_TYPE -> "application/json"))
        .postData("""{"userId":"aUserId"}""").asString

      Then("I receive a 200 (Ok) with the updated requested authority")
      updateResponse.code shouldBe Status.OK
      Json.parse(updateResponse.body) shouldBe Json.toJson(requestedAuthority.copy(userId = Some("aUserId")))

      And("I can retrieve it from mongo")
      val fetchResponse = Http(s"$serviceUrl/authority/${requestedAuthority.id}").asString
      Json.parse(fetchResponse.body).as[RequestedAuthority] shouldBe requestedAuthority.copy(userId = Some("aUserId"))
    }
  }
}