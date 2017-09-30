package controllers

import javax.inject.{Inject, Singleton}

import models._
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.RequestedAuthorityService
import models.JsonFormatters._

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class RequestedAuthorityController  @Inject()(cc: ControllerComponents,
                                              requestedAuthorityService: RequestedAuthorityService) extends AbstractController(cc) with CommonControllers {

  def create() = Action.async(parse.json) { implicit request =>
    withJsonBody[AuthorityRequest] { authorityRequest: AuthorityRequest =>
      requestedAuthorityService.createAuthority(RequestedAuthority(authorityRequest)) map { authority => Ok(Json.toJson(authority))}
    }
  }

  def complete(id: String) =  Action.async(parse.json) { implicit request =>
    withJsonBody[AuthorityCompleteRequest] { authorityCompleteRequest: AuthorityCompleteRequest =>
      requestedAuthorityService.completeRequestedAuthority(id, authorityCompleteRequest.userId) map { authority => Ok(Json.toJson(authority))}
    } recover {
      case _: RequestedAuthorityNotFoundException => RequestedAuthorityNotFound.toHttpResponse
    }
  }

  def fetch(id: String) = Action.async { implicit request =>
    requestedAuthorityService.fetch(id) map {
      case Some(requestedAuthority) => Ok(Json.toJson(requestedAuthority))
      case None => RequestedAuthorityNotFound.toHttpResponse
    }
  }

  def delete(id: String) = Action.async { implicit request =>
    requestedAuthorityService.delete(id) map { _ => NoContent
    }
  }

  def fetchByCode(code: String) = Action.async { implicit request =>
    requestedAuthorityService.fetchByCode(code) map {
      case Some(requestedAuthority) => Ok(Json.toJson(requestedAuthority))
      case None => RequestedAuthorityNotFound.toHttpResponse
    }
  }

}
