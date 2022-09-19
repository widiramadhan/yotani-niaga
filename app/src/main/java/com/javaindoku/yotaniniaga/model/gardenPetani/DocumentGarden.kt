package com.javaindoku.yotaniniaga.model.gardenPetani

class DocumentGarden(
    var dokumen_id: String? = "",
    var nomor: String? = "",
    var foto: String? = "",
    //@Transient
    var dokumen_name: String? = "",
    var dokumen_jenis: String? = "",
    var dokumen_no_blanko: String? = ""
)