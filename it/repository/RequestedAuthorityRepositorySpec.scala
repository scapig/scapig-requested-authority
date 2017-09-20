package repository

import models.{Environment, RequestedAuthority}
import org.scalatest.BeforeAndAfterEach
import play.api.inject.guice.GuiceApplicationBuilder
import utils.UnitSpec

import scala.concurrent.ExecutionContext.Implicits.global

class RequestedAuthorityRepositorySpec extends UnitSpec with BeforeAndAfterEach {

  val requestedAuthority = RequestedAuthority("clientId", Seq("scope"), "redirectUri", Environment.PRODUCTION)
  val completedRequestedAuthority = requestedAuthority.complete(userId = "userId")
  val code = completedRequestedAuthority.authorizationCode.get.code

  lazy val fakeApplication = new GuiceApplicationBuilder()
    .configure("mongodb.uri" -> "mongodb://localhost:27017/tapi-requested-authority-test")
    .build()

  lazy val underTest = fakeApplication.injector.instanceOf[RequestedAuthorityRepository]

  override def afterEach {
    await(underTest.repository).drop(failIfNotFound = false)
  }

  "save" should {
    "create a new requested authority" in {
      await(underTest.save(requestedAuthority))

      await(underTest.fetch(requestedAuthority.id.toString)) shouldBe Some(requestedAuthority)
    }

    "update an existing requested authority" in {
      val updatedRequestedAuthority = requestedAuthority.copy(userId = Some("userId"))
      await(underTest.save(requestedAuthority))

      await(underTest.save(updatedRequestedAuthority))

      await(underTest.fetch(requestedAuthority.id.toString)) shouldBe Some(updatedRequestedAuthority)
    }

  }

  "fetchByCode" should {
    "return the requested authority" in {
      await(underTest.save(completedRequestedAuthority))

      val result = await(underTest.fetchByCode(code))

      result shouldBe Some(completedRequestedAuthority)
    }

    "return None when the code does not match any requested authority" in {
      val result = await(underTest.fetchByCode("invalidCode"))

      result shouldBe None
    }
  }

  "delete" should {
    "remove the requested authority" in {
      await(underTest.save(requestedAuthority))

      await(underTest.delete(requestedAuthority.id.toString)) shouldBe ()

      await(underTest.fetch(requestedAuthority.id.toString)) shouldBe None
    }

    "not fail when the requested authority does not exist" in {
      await(underTest.delete("abcd")) shouldBe ()
    }

  }
}