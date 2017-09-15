package models

import java.util.UUID

import models.AuthType.AuthType
import org.joda.time.{DateTime, DateTimeZone}

case class RequestedAuthority(clientId: String,
                              scopes: Seq[String],
                              redirectUri: String,
                              authType: AuthType.AuthType,
                              code: Option[AuthorizationCode] = None,
                              userId: Option[String] = None,
                              createdAt: DateTime = DateTime.now(),
                              id: UUID = UUID.randomUUID())

object RequestedAuthority {
  def apply(req: AuthorityRequest): RequestedAuthority = RequestedAuthority(req.clientId, req.scopes, req.redirectUri, req.authType)
}

object AuthType extends Enumeration {
  type AuthType = Value
  val PRODUCTION, SANDBOX = Value
}

case class AuthorizationCode(code: String,
                             createdAt: DateTime = DateTime.now(DateTimeZone.UTC)) {
  require(code.trim.nonEmpty, "code cannot be empty")
}


case class AuthorityRequest(clientId: String,
                            scopes: Seq[String],
                            redirectUri: String,
                            authType: AuthType)

case class AuthorityUpdateRequest(userId: String)
