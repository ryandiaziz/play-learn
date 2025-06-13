package models

case class BarangWithKategori(
    id: Int,
    nama: String,
    harga: Option[Double],
    stok: Option[Long],
    isDelete: Boolean,
    kategori: Kategori
)
