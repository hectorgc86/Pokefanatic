package com.hector.proyectofinalbloque2.modelos

import java.io.Serializable

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

//Modelo para la respuesta recibida de un Pokemon
class Pokemon (name: String, url: String): Serializable{

    var numero: Int = 0
    var name: String
    var url: String = ""
    var imagen: String = ""
    var favorito: Boolean = false

        init {
            this.url = url
            this.name = name
        }
}