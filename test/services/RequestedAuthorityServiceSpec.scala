package services

import models.{AuthType, RequestedAuthority, RequestedAuthorityNotFoundException}
import org.mockito.BDDMockito.given
import org.mockito.Matchers.any
import org.mockito.Mockito.{verify, when}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.mockito.MockitoSugar
import repository.RequestedAuthorityRepository
import utils.UnitSpec

import scala.concurrent.Future
import scala.concurrent.Future.successful

class RequestedAuthorityServiceSpec extends UnitSpec with MockitoSugar with BeforeAndAfterAll {

  val requestedAuthority = RequestedAuthority("clientId", Seq("scope"), "/redirectUri", AuthType.PRODUCTION)

  trait Setup {
    val mockRequestedAuthorityRepository = mock[RequestedAuthorityRepository]
    val underTest = new RequestedAuthorityService(mockRequestedAuthorityRepository)

    when(mockRequestedAuthorityRepository.save(any())).thenAnswer(returnSame[RequestedAuthority])
  }

  "create" should {

    "create a requested authority and save it in the repository" in new Setup {

      val result = await(underTest.createAuthority(requestedAuthority))

      result shouldBe requestedAuthority
      verify(mockRequestedAuthorityRepository).save(requestedAuthority)
    }

    "propagate exceptions thrown by the repository" in new Setup {
      when(mockRequestedAuthorityRepository.save(requestedAuthority)).thenReturn(Future.failed(new RuntimeException("test error")))

      intercept[RuntimeException]{await(underTest.createAuthority(requestedAuthority))}
    }
  }

  "updateAuthorityUser" should {
    "fetch the requested authority and update the userId" in new Setup {
      val expectedRequestedAuthority = requestedAuthority.copy(userId = Some("userId"))

      given(mockRequestedAuthorityRepository.fetch(requestedAuthority.id.toString)).willReturn(successful(Some(requestedAuthority)))
      given(mockRequestedAuthorityRepository.save(expectedRequestedAuthority)).willReturn(successful(expectedRequestedAuthority))

      val result = await(underTest.updateAuthorityUser(requestedAuthority.id.toString, "userId"))

      result shouldBe expectedRequestedAuthority
      verify(mockRequestedAuthorityRepository).save(expectedRequestedAuthority)
    }

    "fail with RequestedAuthorityNotFoundException when the requested authority does not exist" in new Setup {
      given(mockRequestedAuthorityRepository.fetch(requestedAuthority.id.toString)).willReturn(successful(None))

      intercept[RequestedAuthorityNotFoundException]{await(underTest.updateAuthorityUser(requestedAuthority.id.toString, "userId"))}
    }

    "propagate exceptions thrown by the repository" in new Setup {
      when(mockRequestedAuthorityRepository.save(any())).thenReturn(Future.failed(new RuntimeException("test error")))

      intercept[RuntimeException]{await(underTest.updateAuthorityUser(requestedAuthority.id.toString, "userId"))}
    }
  }
}
