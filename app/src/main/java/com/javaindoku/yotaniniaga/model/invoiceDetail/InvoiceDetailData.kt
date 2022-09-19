package com.javaindoku.yotaniniaga.model.invoiceDetail


import com.google.gson.annotations.SerializedName

data class InvoiceDetailData(
    @SerializedName("bank_atas_nama")
    var bankAtasNama: String? = "",
    @SerializedName("bank_code")
    var bankCode: String? = "",
    @SerializedName("bank_name")
    var bankName: String? = "",
    @SerializedName("biaya_platform")
    var biayaPlatform: String? = "",
    @SerializedName("created_date")
    var createdDate: String? = "",
    @SerializedName("due_date")
    var dueDate: String? = "",
    @SerializedName("harga_tbs")
    var hargaTbs: String? = "",
    @SerializedName("harga_tbs_fee")
    var hargaTbsFee: String? = "",
    @SerializedName("harga_total")
    var hargaTotal: String? = "",
    @SerializedName("id")
    var id: String? = "",
    @SerializedName("jumlah_pembayaran")
    var jumlahPembayaran: String? = "",
    @SerializedName("nama_pabrik")
    var namaPabrik: String? = "",
    @SerializedName("nama_parent_pabrik")
    var namaParentPabrik: String? = "",
    @SerializedName("no_grn")
    var noGrn: String? = "",
    @SerializedName("no_invoice")
    var noInvoice: String? = "",
    @SerializedName("no_rekening")
    var noRekening: String? = "",
    @SerializedName("no_reservasi")
    var noReservasi: String? = "",
    @SerializedName("no_truk")
    var noTruk: String? = "",
    @SerializedName("pabrik_id")
    var pabrikId: String? = "",
    @SerializedName("parent_pabrik_id")
    var parentPabrikId: String? = "",
    @SerializedName("row_status")
    var rowStatus: String? = "",
    @SerializedName("status")
    var status: String? = "",
    @SerializedName("status_name")
    var statusName: String? = "",
    @SerializedName("supplier_id")
    var supplierId: String? = "",
    @SerializedName("supplier_name")
    var supplierName: String? = "",
    @SerializedName("tanggal_pembayaran")
    var tanggalPembayaran: String? = "",
    @SerializedName("tanggal_terima")
    var tanggalTerima: String? = "",
    @SerializedName("total_tonasi")
    var totalTonasi: String? = ""
)