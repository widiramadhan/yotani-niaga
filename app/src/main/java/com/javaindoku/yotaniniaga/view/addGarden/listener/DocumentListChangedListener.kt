package com.javaindoku.yotaniniaga.view.addGarden.listener

import com.javaindoku.yotaniniaga.model.gardenPetani.DocumentGarden

interface DocumentListChangedListener {
    fun onDataChanged(documentList: List<DocumentGarden>)
}
