package com.example.eHandel.Models

class Order: java.io.Serializable {
    var name: String = ""
    var phoneNumber: String = ""
    var address: String = ""
    var cityName: String = ""
    var country: String = ""
    var products: HashMap<String,Productos> = HashMap<String,Productos>()

    constructor(){

    }
    constructor(
        name: String,
        phoneNumber: String,
        address: String,
        cityName: String,
        country: String,
        products: HashMap<String,Productos>
    ){
        this.name = name
        this.phoneNumber = phoneNumber
        this.address = address
        this.cityName = cityName
        this.country = country
        this.products = products
    }
}