package services

import models.{Environment, RequestedAuthority, RequestedAuthorityNotFoundException}
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

  val requestedAuthority = RequestedAuthority("clientId", Seq("scope"), "/redirectUri", Environment.PRODUCTION)
  val completedRequestedAuthority = requestedAuthority.complete("userId")
  val authorizationCode = completedRequestedAuthority.authorizationCode.get.code

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

  "fetch" should {

    "return the requested authority when it exists" in new Setup {
      given(mockRequestedAuthorityRepository.fetch(requestedAuthority.id.toString)).willReturn(successful(Some(requestedAuthority)))

      val result = await(underTest.fetch(requestedAuthority.id.toString))

      result shouldBe Some(requestedAuthority)
    }

    "return None when it does not exist" in new Setup {
      given(mockRequestedAuthorityRepository.fetch(requestedAuthority.id.toString)).willReturn(successful(None))

      val result = await(underTest.fetch(requestedAuthority.id.toString))

      result shouldBe None
    }

    "propagate exceptions thrown by the repository" in new Setup {
      given(mockRequestedAuthorityRepository.fetch(requestedAuthority.id.toString)).willReturn(Future.failed(new RuntimeException("test error")))

      intercept[RuntimeException]{await(underTest.fetch(requestedAuthority.id.toString))}
    }
  }

  "fetchByCode" should {

    "return the requested authority when it exists" in new Setup {
      given(mockRequestedAuthorityRepository.fetchByCode(authorizationCode)).willReturn(successful(Some(completedRequestedAuthority)))

      val result = await(underTest.fetchByCode(authorizationCode))

      result shouldBe Some(completedRequestedAuthority)
    }

    "return None when it does not exist" in new Setup {
      given(mockRequestedAuthorityRepository.fetchByCode(authorizationCode)).willReturn(successful(None))

      val result = await(underTest.fetchByCode(authorizationCode))

      result shouldBe None
    }

    "propagate exceptions thrown by the repository" in new Setup {
      given(mockRequestedAuthorityRepository.fetchByCode(authorizationCode)).willReturn(Future.failed(new RuntimeException("test error")))

      intercept[RuntimeException]{await(underTest.fetchByCode(authorizationCode))}
    }
  }

  "completeRequestedAuthority" should {
    "fetch the requested authority, update the userId and assign an authorization code" in new Setup {

      given(mockRequestedAuthorityRepository.fetch(requestedAuthority.id.toString)).willReturn(successful(Some(requestedAuthority)))

      val result = await(underTest.completeRequestedAuthority(requestedAuthority.id.toString, "userId"))

      result shouldBe requestedAuthority.copy(userId = Some("userId"), authorizationCode = result.authorizationCode)
      result.authorizationCode.isDefined shouldBe true
      verify(mockRequestedAuthorityRepository).save(result)
    }

    "fail with RequestedAuthorityNotFoundException when the requested authority does not exist" in new Setup {
      given(mockRequestedAuthorityRepository.fetch(requestedAuthority.id.toString)).willReturn(successful(None))

      intercept[RequestedAuthorityNotFoundException]{await(underTest.completeRequestedAuthority(requestedAuthority.id.toString, "userId"))}
    }

    "propagate exceptions thrown by the repository" in new Setup {
      when(mockRequestedAuthorityRepository.save(any())).thenReturn(Future.failed(new RuntimeException("test error")))

      intercept[RuntimeException]{await(underTest.completeRequestedAuthority(requestedAuthority.id.toString, "userId"))}
    }
  }
}
