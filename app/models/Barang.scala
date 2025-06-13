package models

case class Barang(
                     id: Int,
                     nama: String,
                     kategoriId: Int,
                     harga: Option[Double],
                     stok: Option[Long],
                     isDelete: Boolean = false
                 )
