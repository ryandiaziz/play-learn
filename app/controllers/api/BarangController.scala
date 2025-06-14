package controllers.api

import play.api.libs.json.*
import play.api.mvc.*

import javax.inject.*
import repositories.*
import models.{Barang, BarangWithKategori, Kategori}

@Singleton
class BarangController @Inject() (cc: ControllerComponents, barangRepo: BarangRepository)
    extends AbstractController(cc) {

  implicit val barangFormat: OFormat[Barang]                         = Json.format[Barang]
  implicit val kategoriFormat: OFormat[Kategori]                     = Json.format[Kategori]
  implicit val barangWithKategoriFormat: OFormat[BarangWithKategori] = Json.format[BarangWithKategori]

  def list: Action[AnyContent] = Action {
    val data = barangRepo.all()
    Ok(Json.toJson(data))
  }

  def create: Action[AnyContent] = Action { request =>
    val params = request.body.asJson.get

    val paramsObj = Barang(
      id = -1,
      nama = (params \ "nama").as[String],
      kategoriId = (params \ "kategori_id").as[Int],
      harga = (params \ "harga").asOpt[Double],
      stok = (params \ "stok").asOpt[Long]
    )

    val resId = barangRepo.create(paramsObj)

    resId match {
      case Some(id) =>
        Ok(
          Json.obj(
            "message" -> "alhamdulillah",
            "data" -> Json.obj(
              "id" -> id
            )
          )
        )
      case None => BadRequest(Json.obj("message" -> "anak nakal!"))
    }
  }

  def detail(id: Int): Action[AnyContent] = Action {
    println(id)
    barangRepo
      .findById(id)
      .map(b => Ok(Json.toJson(b)))
      .getOrElse(NotFound("Not found"))
  }

  def update(): Action[AnyContent] = Action { implicit request =>
    request.body.asJson match {
      case Some(json) =>
        try {
          val barang = Barang(
            id = (json \ "id").as[Int],
            nama = (json \ "nama").as[String],
            kategoriId = (json \ "kategori_id").as[Int],
            harga = (json \ "harga").asOpt[Double],
            stok = (json \ "stok").asOpt[Long]
          )

          barangRepo.update(barang) match {
            case Some(updatedId) =>
              Ok(
                Json.obj(
                  "message" -> "alhamdulillah",
                  "data"    -> Json.obj("id" -> updatedId)
                )
              )
            case None =>
              BadRequest(Json.obj("message" -> "anak nakal!"))
          }

        } catch {
          case e: Exception =>
            BadRequest(
              Json.obj(
                "status"  -> "error",
                "message" -> "Gagal membaca data JSON",
                "detail"  -> e.getMessage
              )
            )
        }

      case None =>
        BadRequest(
          Json.obj(
            "status"  -> "error",
            "message" -> "Body bukan JSON",
            "detail"  -> None
          )
        )
    }
  }

  def delete(id: Int): Action[AnyContent] = Action {
    barangRepo.softDelete(id)
    Ok("Soft Deleted")
  }
}
