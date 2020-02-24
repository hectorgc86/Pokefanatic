package com.hector.proyectofinalbloque2.servicios

import com.hector.proyectofinalbloque2.modelos.DetallesRespuesta
import com.hector.proyectofinalbloque2.modelos.Pokemon

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

//Interfaz de conexíon ala API de Pokemon
interface IPokemonAPI {

    fun detallesPokemonAPI(numeroPokemon: Int): DetallesRespuesta

    fun listaPokemonsAPI(): MutableList<Pokemon>
}