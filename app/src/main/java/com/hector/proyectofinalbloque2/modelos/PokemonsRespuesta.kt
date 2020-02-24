package com.hector.proyectofinalbloque2.modelos

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

//Modelo para la respuesta recibida de todos los Pokemon
class PokemonsRespuesta(results: MutableList<Pokemon>) {

    var results: MutableList<Pokemon> = ArrayList()

    init {
        this.results = results
    }
}