package repositories

import anorm.SqlParser.*
import anorm.*
import play.api.db.DBApi

import javax.inject.Inject
import models.{Barang, BarangWithKategori, Kategori}
import org.postgresql.util.PSQLException

//case class Barang(id: Option[Int], nama: String, kategori: String, harga: Double, stok: Int, isDelete: Boolean)

@javax.inject.Singleton
class BarangRepository @Inject() (dbapi: DBApi) {
  val barangParser: RowParser[Barang] = {
    get[Int]("id") ~
      get[String]("nama") ~
      get[Int]("kategori_id") ~
      get[Double]("harga") ~
      get[Int]("stok") ~
      get[Boolean]("is_delete") map { case id ~ nama ~ kategoriId ~ harga ~ stok ~ isDelete =>
        Barang(id, nama, kategoriId, Some(harga), Some(stok), isDelete)
      }
  }

  val barangWithKategoriParser: RowParser[BarangWithKategori] = {
    get[Int]("barang.id") ~
      get[String]("barang.nama") ~
      get[Double]("barang.harga") ~
      get[Int]("barang.stok") ~
      get[Boolean]("barang.is_delete") ~
      get[Int]("kategori.id") ~
      get[String]("kategori.nama") ~
      get[Boolean]("kategori.is_delete") map {
        case barangId ~ barangNama ~ harga ~ stok ~ barangDelete ~
            kategoriId ~ kategoriNama ~ kategoriDelete =>
          BarangWithKategori(
            id = barangId,
            nama = barangNama,
            harga = Some(harga),
            stok = Some(stok),
            isDelete = barangDelete,
            kategori = Kategori(
              id = kategoriId,
              nama = kategoriNama,
              isDelete = kategoriDelete
            )
          )
      }
  }

  private val db        = dbapi.database("default")
  private val tableName = "barang"

  def all(): List[BarangWithKategori] = db.withConnection { implicit connection =>
//        SQL"SELECT * FROM $tableName WHERE is_delete = false".as(barangParser.*)

    SQL"""
            SELECT
                barang.id, barang.nama, barang.harga, barang.stok, barang.is_delete,
                kategori.id, kategori.nama, kategori.is_delete
            FROM barang
            JOIN kategori ON barang.kategori_id = kategori.id
            WHERE barang.is_delete = false AND kategori.is_delete = false
        """.as(barangWithKategoriParser.*)
  }

  def create(barang: Barang): Option[Long] = db.withConnection(implicit connection => {
    var id: Option[Long] = None

    val stmQuery: String =
      s"""
                | INSERT INTO barang (nama, kategori_id, harga, stok)
                | VALUES ({nama}, {kategoriId}, {harga}, {stok})
                |""".stripMargin

    try {
      id = SQL(stmQuery)
        .on(
          "nama"       -> barang.nama,
          "kategoriId" -> barang.kategoriId,
          "harga"      -> barang.harga.getOrElse(0.0),
          "stok"       -> barang.stok.getOrElse(0L)
        )
        .executeInsert(SqlParser.scalar[Long].singleOpt)
    } catch {
      case e: PSQLException => id = None
    }

    id
  })

  def findById(id: Int): Option[BarangWithKategori] = db.withConnection { implicit connection =>
    val stmQuery =
      s"""
               |SELECT
               |barang.id, barang.nama, barang.harga, barang.stok, barang.is_delete,
               |                kategori.id, kategori.nama, kategori.is_delete
               |            FROM $tableName
               |            JOIN kategori ON barang.kategori_id = kategori.id
               |            WHERE barang.id = $id AND barang.is_delete = false AND kategori.is_delete = false
               |""".stripMargin

    println(s"repo : $stmQuery")

    SQL(stmQuery).as(barangWithKategoriParser.singleOpt)
//        SQL(s"SELECT * FROM $tableName WHERE id = 2 AND is_delete = false").as(barangParser.singleOpt)
  }

  def update(barang: Barang): Option[Long] = db.withConnection { implicit connection =>
    val stmQuery: String =
      s"""
            |UPDATE barang SET
            |nama = {nama},
            |kategori_id = {kategoriId},
            |harga = {harga},
            |stok = {stok}
            |WHERE id = {id}
            |""".stripMargin

    try {
      val updatedCount: Int = SQL(stmQuery)
        .on(
          "nama"       -> barang.nama,
          "kategoriId" -> barang.kategoriId,
          "harga"      -> barang.harga.getOrElse(0.0),
          "stok"       -> barang.stok.getOrElse(0L),
          "id"         -> barang.id
        )
        .executeUpdate()

      if (updatedCount > 0) Some(barang.id.toLong) else None

    } catch {
      case e: Exception =>
        // log error kalau perlu
        None
    }
  }

  def softDelete(id: Int): Int = db.withConnection { implicit connection =>
    SQL"UPDATE barang SET is_delete = true WHERE id = $id".executeUpdate()
  }
}
