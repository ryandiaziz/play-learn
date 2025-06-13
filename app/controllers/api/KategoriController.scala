package controllers.api

import models.Kategori
import play.api.libs.json.*
import play.api.mvc.*

import javax.inject.*
import repositories.*
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import repositories.KategoriRepository
import utils.JsonController

import javax.inject.Inject

class KategoriController @Inject()(cc: ControllerComponents, kategoriRepo: KategoriRepository) extends AbstractController(cc) with JsonController{
    def create: Action[AnyContent] = Action { request =>
        request.body.asJson match {
            case Some(json) =>
                val namaOpt = (json \ "nama").asOpt[String]

                namaOpt match {
                    case Some(nama) if nama.nonEmpty =>
                        val kategori = Kategori(-1, nama)
                        kategoriRepo.create(kategori) match {
                            case Some(id) =>
                                Ok(Json.obj(
                                    "message" -> "alhamdulillah",
                                    "data" -> Json.obj("id" -> id)
                                ))
                            case None => dbError()
                        }

                    case _ => jsonMissingField("nama")
                }

            case None => jsonExpected()
        }
    }
}
