package repository

import models.{AuthType, RequestedAuthority}
import org.scalatest.BeforeAndAfterEach
import play.api.inject.guice.GuiceApplicationBuilder
import utils.UnitSpec
import scala.concurrent.ExecutionContext.Implicits.global

class RequestedAuthorityRepositorySpec extends UnitSpec with BeforeAndAfterEach {

  val requestedAuthority = RequestedAuthority("clientId", Seq("scope"), "redirectUri", AuthType.PRODUCTION)

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
}