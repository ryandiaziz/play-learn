package controllers.api

import models.User
import play.api.libs.json._
import play.api.mvc._
import repositories.UserRepository
import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class UserController @Inject()(
        cc: ControllerComponents,
        userRepo: UserRepository
    )(implicit ec: ExecutionContext) extends AbstractController(cc) {

    def list = Action {
        val users = userRepo.list()
        Ok(Json.toJson(users))
    }

    def create = Action(parse.json) { request =>
        request.body.validate[User].map { user =>
            userRepo.create(user.copy(id = None)) match {
                case Some(id) => Created(Json.obj("id" -> id))
                case None => InternalServerError("Insert failed")
            }
        }.getOrElse(BadRequest("Invalid JSON"))
    }

    def get(id: Long) = Action {
        userRepo.get(id).map { user =>
            Ok(Json.toJson(user))
        }.getOrElse(NotFound)
    }

    def update(id: Long) = Action(parse.json) { request =>
        request.body.validate[User].map { user =>
            if (userRepo.update(id, user)) Ok
            else NotFound
        }.getOrElse(BadRequest("Invalid JSON"))
    }

    def delete(id: Long) = Action {
        if (userRepo.delete(id)) NoContent
        else NotFound
    }
}