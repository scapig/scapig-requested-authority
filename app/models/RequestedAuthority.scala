package models

import java.util.UUID

import org.joda.time.{DateTime, DateTimeZone}

case class RequestedAuthority(clientId: String,
                              scopes: Seq[String],
                              redirectUri: String,
                              environment: Environment.Environment,
                              authorizationCode: Option[AuthorizationCode] = None,
                              userId: Option[String] = None,
                              createdAt: DateTime = DateTime.now(),
                              id: UUID = UUID.randomUUID()) {

  def complete(userId: String) = this.copy(userId = Some(userId), authorizationCode = Some(AuthorizationCode()))
}

object RequestedAuthority {
  def apply(req: AuthorityRequest): RequestedAuthority = RequestedAuthority(req.clientId, req.scopes, req.redirectUri, req.environment)
}

object Environment extends Enumeration {
  type Environment = Value
  val PRODUCTION, SANDBOX = Value
}

case class AuthorizationCode(code: String = UUID.randomUUID().toString,
                             createdAt: DateTime = DateTime.now()) {
  require(code.trim.nonEmpty, "code cannot be empty")
}


case class AuthorityRequest(clientId: String,
                            scopes: Seq[String],
                            redirectUri: String,
                            environment: Environment.Environment)

case class AuthorityCompleteRequest(userId: String)
