package com.hector.proyectofinalbloque2.servicios

import com.google.gson.Gson
import com.hector.proyectofinalbloque2.modelos.DetallesRespuesta
import com.hector.proyectofinalbloque2.modelos.Pokemon
import com.hector.proyectofinalbloque2.modelos.PokemonsRespuesta
import org.json.JSONObject
import java.net.URL

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

class PokemonAPI: IPokemonAPI {

    //Método de descarga detalles de pokemon e la API
   override fun detallesPokemonAPI(numeroPokemon: Int): DetallesRespuesta {

       val respuestaApi: String =
                URL("https://pokeapi.co/api/v2/pokemon-species/"+
                        numeroPokemon.toString()).readText()

            var descripcion = ""

            val descripciones = JSONObject(respuestaApi)
                .getJSONArray("flavor_text_entries")

            for (i in 0 until descripciones.length()-1){

                if(descripciones.getJSONObject(i).getJSONObject("language")
                        .getString("name") == "es"){
                    descripcion = descripciones.getJSONObject(i)
                        .getString("flavor_text")
                }

            }


            val tipos = JSONObject(respuestaApi)
                .getJSONArray("egg_groups")

            var cadenaTipos = ""


                val tipo = tipos.getJSONObject(0).getString("name")
                cadenaTipos += tipo.toCharArray()[0].toUpperCase() +
                        tipo.substring(1)+", "

            cadenaTipos = cadenaTipos.substring(0,cadenaTipos.length-2)


        return DetallesRespuesta(descripcion,cadenaTipos)
    }

    //Método de descarga de todos los Pokemon (los primeros 150)
   override fun listaPokemonsAPI(): MutableList<Pokemon>{

       val gson = Gson()

       val respuestaApi: String =
           URL("https://pokeapi.co/api/v2/pokemon/?limit=150").readText()

      return gson.fromJson(respuestaApi, PokemonsRespuesta::class.java).results
   }
}