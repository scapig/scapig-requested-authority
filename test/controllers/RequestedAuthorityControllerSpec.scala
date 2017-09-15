package controllers

import models._
import org.joda.time.DateTimeUtils
import org.mockito.BDDMockito.given
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, verifyZeroInteractions}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.mockito.MockitoSugar
import play.api.http.Status
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.{FakeRequest, Helpers}
import services.RequestedAuthorityService
import utils.UnitSpec
import models.JsonFormatters._

import scala.concurrent.Future.successful

class RequestedAuthorityControllerSpec extends UnitSpec with MockitoSugar with BeforeAndAfterAll {

  val authorityRequest = AuthorityRequest("clientId", Seq("scope"), "/redirectUri", AuthType.PRODUCTION)
  val requestedAuthority = RequestedAuthority(authorityRequest.clientId, authorityRequest.scopes, authorityRequest.redirectUri, authorityRequest.authType)

  trait Setup {
    val mockRequestedAuthorityService: RequestedAuthorityService = mock[RequestedAuthorityService]
    val underTest = new RequestedAuthorityController(Helpers.stubControllerComponents(), mockRequestedAuthorityService)

    val request = FakeRequest()

    given(mockRequestedAuthorityService.createAuthority(any())).willAnswer(returnSame[RequestedAuthority])
  }

  override def beforeAll {
    DateTimeUtils.setCurrentMillisFixed(10000)
  }

  override def afterAll {
    DateTimeUtils.setCurrentMillisSystem()
  }

  "create" should {

    "succeed with a 200 with the requested authority when payload is valid and service responds successfully" in new Setup {

      val result: Result = await(underTest.create()(request.withBody(Json.toJson(authorityRequest))))

      status(result) shouldBe Status.OK
      val createdRequestedAuthority = jsonBodyOf(result).as[RequestedAuthority]
      createdRequestedAuthority shouldBe createdRequestedAuthority.copy(id = createdRequestedAuthority.id)

      verify(mockRequestedAuthorityService).createAuthority(createdRequestedAuthority)
    }

    "fail with a 400 (Bad Request) when the json payload is invalid for the request" in new Setup {

      val body = """{ "invalid": "json" }"""

      val result: Result = await(underTest.create()(request.withBody(Json.parse(body))))

      status(result) shouldBe Status.BAD_REQUEST
      jsonBodyOf(result) shouldBe Json.parse("""{"code":"INVALID_REQUEST","message":"scopes is required"}""")
      verifyZeroInteractions(mockRequestedAuthorityService)
    }
  }

  "fetch" should {

    "succeed with a 200 (Ok) with the requested authority when the requested authority exists" in new Setup {

      given(mockRequestedAuthorityService.fetch(requestedAuthority.id.toString))
        .willReturn(successful(Some(requestedAuthority)))

      val result: Result = await(underTest.fetch(requestedAuthority.id.toString)(request))

      status(result) shouldBe Status.OK
      jsonBodyOf(result) shouldBe Json.toJson(requestedAuthority)
    }

    "fail with a 404 (Not Found) when the api-definition does not exist" in new Setup {
      given(mockRequestedAuthorityService.fetch(requestedAuthority.id.toString)).willReturn(successful(None))

      val result: Result = await(underTest.fetch(requestedAuthority.id.toString)(request))

      status(result) shouldBe Status.NOT_FOUND
      jsonBodyOf(result) shouldBe Json.parse(s"""{"code": "NOT_FOUND", "message": "requested authority not found"}""")
    }
  }
}
