package com.hector.proyectofinalbloque2.modelos

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

//Modelo para la respuesta recibida de los detalles de un Pokemon
class DetallesRespuesta (descripcion:String, tipo:String){
    var descripcion: String = ""
    var tipo: String = ""

    init {
        this.descripcion = descripcion
        this.tipo = tipo
    }
}
