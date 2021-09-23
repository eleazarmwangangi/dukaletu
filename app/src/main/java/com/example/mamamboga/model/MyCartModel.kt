package com.example.mamamboga.model

data class MyCartModel(
    var name: String?= "",
    var price: Double,
    var quantity: Double,
    var address: String?="",
    var foodImage: String?="",
    var amount:Double
    ){}