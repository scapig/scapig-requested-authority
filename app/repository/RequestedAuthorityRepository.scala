package repository

import javax.inject.{Inject, Singleton}

import models.RequestedAuthority
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.{UpdateWriteResult, WriteResult}
import reactivemongo.play.json.collection.JSONCollection
import reactivemongo.play.json._
import models.JsonFormatters._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class RequestedAuthorityRepository @Inject()(val reactiveMongoApi: ReactiveMongoApi)  {

  def repository: Future[JSONCollection] =
    reactiveMongoApi.database.map(_.collection[JSONCollection]("scapig-requested-authority"))

  def save(requestedAuthority: RequestedAuthority): Future[RequestedAuthority] = {
    repository.flatMap(collection =>
      collection.update(
        Json.obj("id"-> requestedAuthority.id), requestedAuthority, upsert = true) map {
        case result: UpdateWriteResult if result.ok => requestedAuthority
        case error => throw new RuntimeException(s"Failed to save requestedAuthority ${error.errmsg}")
      }
    )
  }

  def fetch(id: String): Future[Option[RequestedAuthority]] = {
    repository.flatMap(collection =>
      collection.find(Json.obj("id"-> id)).one[RequestedAuthority]
    )
  }

  def fetchByCode(code: String): Future[Option[RequestedAuthority]] = {
    repository.flatMap(collection =>
      collection.find(Json.obj("authorizationCode.code"-> code)).one[RequestedAuthority]
    )
  }

  def delete(id: String): Future[Unit]= {
    repository.flatMap(collection =>
      collection.remove(Json.obj("id"-> id)) map {
        case result: WriteResult if result.ok => Unit
        case error => throw new RuntimeException(s"Failed to delete requestedAuthority ${error.writeErrors}")
      }
    )
  }
}
