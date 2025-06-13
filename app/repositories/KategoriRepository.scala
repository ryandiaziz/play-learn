package repositories

import models.Kategori
import anorm.SqlParser.*
import anorm.*
import play.api.db.DBApi

import javax.inject.Inject
import org.postgresql.util.PSQLException

@javax.inject.Singleton
class KategoriRepository@Inject()(dbapi: DBApi) {
    private val db = dbapi.database("default")
    private val tableName = "kategori"
    
    def create(kategori: Kategori): Option[Long] = db.withConnection(implicit connection => {
        var id: Option[Long] = None

        val query: String =
            s"""
                | INSERT INTO $tableName (nama)
                | VALUES ({nama})
                |""".stripMargin

        try {
            id = SQL(query).on(
                "nama" -> kategori.nama,
            ).executeInsert(SqlParser.scalar[Long].singleOpt)
        } catch {
            case e: PSQLException => id = None
        }

        id
    })
}
