package com.javaindoku.yotaniniaga.model.invoice

import com.google.gson.annotations.SerializedName

data class Invoice (
    var id: String? = "",
    @SerializedName("due_date")
    var dueDate: String? = "",
    @SerializedName("no_invoice")
    var invoiceNo: String? = "",
    @SerializedName("no_grn")
    var grnNumber: String? = "",
    @SerializedName("pabrik_id")
    var factoryId: Int? = 0,
    @SerializedName("nama_pabrik")
    var factoryName: String? = "",
    @SerializedName("parent_pabrik_id")
    var parentFactoryId: Int? = 0,
    @SerializedName("nama_parent_pabrik")
    var parentFactoryName: String? = "",
    @SerializedName("supplier_id")
    var supplierId: Int? = 0,
    @SerializedName("supplier_name")
    var supplierName: String = "",
    @SerializedName("harga_total")
    var totalPrice: String = "",
    @SerializedName("jumlah_pembayaran")
    var totalPaid: String = "",
    @SerializedName("tanggal_pembayaran")
    var paymentDate: String = "",
    var status: String = "",
    @SerializedName("status_name")
    var statusName: String = "",
    @SerializedName("row_status")
    var rowStatus: String = "",
    @SerializedName("bank_code")
    var bankCode: String = "",
    @SerializedName("bank_name")
    var bankName: String = "",
    @SerializedName("no_rekening")
    var noRekening: String = "",
    @SerializedName("bank_atas_nama")
    var bankAtasNama: String = "",
    @SerializedName("tanggal_terima")
    var tanggalTerima: String = "",
    @SerializedName("no_truk")
    var noTruk: String = "",
    @SerializedName("total_tonasi")
    var totalTonasi: String = "",
    @SerializedName("harga_tbs")
    var hargaTbs: String = "",
    @SerializedName("harga_tbs_fee")
    var hargaTbsFee: String = "",
    @SerializedName("biaya_platform")
    var biayaPlatform: String = ""
)