package com.fathurrohman.piquizapps

data class MateriModel(
    val judul: String,
    val videoUrl: String,
    val penjelasan: String,
    val videoId: String
){
    constructor() : this("","","","")
}

