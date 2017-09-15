package services

import javax.inject.{Inject, Singleton}

import models.{AuthorityRequest, RequestedAuthority, RequestedAuthorityNotFoundException}
import repository.RequestedAuthorityRepository
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

@Singleton
class RequestedAuthorityService @Inject()(requestedAuthorityRepository: RequestedAuthorityRepository) {

  def createAuthority(requestedAuthority: RequestedAuthority): Future[RequestedAuthority] = {
    requestedAuthorityRepository.save(requestedAuthority)
  }

  def fetch(id: String): Future[Option[RequestedAuthority]] = ???

  def updateAuthorityUser(id: String, userId: String): Future[RequestedAuthority] = {
    for {
      maybeRequestedAuthority <- requestedAuthorityRepository.fetch(id)
      requestedAuthority = maybeRequestedAuthority.getOrElse(throw new RequestedAuthorityNotFoundException(id))
      updatedRequestedAuthority <- requestedAuthorityRepository.save(requestedAuthority.copy(userId = Some(userId)))
    } yield updatedRequestedAuthority
  }
}
