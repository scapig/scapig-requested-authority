package services

import javax.inject.{Inject, Singleton}

import models.{RequestedAuthority, RequestedAuthorityNotFoundException}
import repository.RequestedAuthorityRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class RequestedAuthorityService @Inject()(requestedAuthorityRepository: RequestedAuthorityRepository) {

  def createAuthority(requestedAuthority: RequestedAuthority): Future[RequestedAuthority] = {
    requestedAuthorityRepository.save(requestedAuthority)
  }

  def fetch(id: String): Future[Option[RequestedAuthority]] = requestedAuthorityRepository.fetch(id)

  def fetchByCode(code: String): Future[Option[RequestedAuthority]] = requestedAuthorityRepository.fetchByCode(code)

  def completeRequestedAuthority(id: String, userId: String): Future[RequestedAuthority] = {
    for {
      maybeRequestedAuthority <- requestedAuthorityRepository.fetch(id)
      completedRequestedAuthority = maybeRequestedAuthority.getOrElse(throw new RequestedAuthorityNotFoundException(id)).complete(userId)
      _ <- requestedAuthorityRepository.save(completedRequestedAuthority)
    } yield completedRequestedAuthority
  }
}
