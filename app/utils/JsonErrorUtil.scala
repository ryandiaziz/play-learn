package utils

import play.api.libs.json._
import play.api.mvc.{Result, Results}

object JsonErrorUtil extends Results {

    def jsonMissingField(field: String): Result = {
        BadRequest(Json.obj(
            "status" -> "error",
            "message" -> s"Field '$field' harus ada dan tidak boleh kosong"
        ))
    }

    def jsonFormatError(detail: String): Result = {
        BadRequest(Json.obj(
            "status" -> "error",
            "message" -> "Format JSON tidak valid",
            "detail" -> detail
        ))
    }

    def jsonExpected(): Result = {
        BadRequest(Json.obj(
            "status" -> "error",
            "message" -> "Body harus dalam format JSON"
        ))
    }

    def dbError(): Result = {
        BadRequest(Json.obj(
            "status" -> "error",
            "message" -> "Gagal menyimpan ke database"
        ))
    }
}
