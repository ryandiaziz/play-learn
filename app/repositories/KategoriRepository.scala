package repositories

import models.Kategori
import anorm.SqlParser.*
import anorm.*
import play.api.db.DBApi

import javax.inject.Inject
import org.postgresql.util.PSQLException

@javax.inject.Singleton
class KategoriRepository @Inject() (dbapi: DBApi) {
  val kategoriParser: RowParser[Kategori] = {
    get[Int]("id") ~
      get[String]("namaa") ~
      get[Boolean]("is_delete") map { case id ~ nama ~ isDelete =>
        Kategori(id, nama, isDelete)
      }
  }

  private val db        = dbapi.database("default")
  private val tableName = "kategori"

  def create(kategori: Kategori): Option[Long] = db.withConnection(implicit connection => {
    var id: Option[Long] = None

    val query: String =
      s"""
                | INSERT INTO kategori (nama)
                | VALUES ({nama})
                |""".stripMargin

    try {
      id = SQL(query)
        .on(
          "nama" -> kategori.nama
        )
        .executeInsert(SqlParser.scalar[Long].singleOpt)
    } catch {
      case e: PSQLException => id = None
    }

    id
  })

  def all(): List[Kategori] = db.withConnection { implicit connection =>
    SQL"""
                SELECT * FROM kategori WHERE is_delete = false
            """.as(kategoriParser.*)
  }
}
