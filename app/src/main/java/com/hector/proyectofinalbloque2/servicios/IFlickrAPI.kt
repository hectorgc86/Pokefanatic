package com.hector.proyectofinalbloque2.servicios

import android.content.Context
import android.widget.ImageView
import com.hector.proyectofinalbloque2.modelos.Pokemon

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

//Interfaz de conexíon a la API de Flickr
interface IFlickrAPI {

    fun fotosPokemonAPI(pokemon: Pokemon, context: Context): MutableList<ImageView>
}