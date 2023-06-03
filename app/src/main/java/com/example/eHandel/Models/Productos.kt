package com.example.eHandel.Models

class Productos : java.io.Serializable {
    var image: String = ""
    var name: String = ""
    var price: Double = 0.0
    var quantity: Int = 0
    var type: String = ""
    var vendor: String = ""

    constructor() {
        // Default no-argument constructor required by Firebase Firestore
    }

    constructor(
        image: String,
        name: String,
        price: Double,
        quantity: Int,
        type: String,
        vendor: String
    ) {
        this.image = image
        this.name = name
        this.price = price
        this.quantity = quantity
        this.type = type
        this.vendor = vendor
    }
}
