package models

import play.api.libs.json._

case class User(id: Option[Long], name: String, email: String)

object User {
    implicit val format: OFormat[User] = Json.format[User]
}
