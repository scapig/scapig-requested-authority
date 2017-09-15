package controllers

import javax.inject.{Inject, Singleton}

import models.{AuthorityRequest, RequestedAuthority, RequestedAuthorityNotFound}
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

  def fetch(id: String) = Action.async { implicit request =>
    requestedAuthorityService.fetch(id) map {
      case Some(requestedAuthority) => Ok(Json.toJson(requestedAuthority))
      case None => RequestedAuthorityNotFound.toHttpResponse
    }
  }
}
