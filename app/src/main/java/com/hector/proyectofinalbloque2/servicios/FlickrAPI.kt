package com.hector.proyectofinalbloque2.servicios

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.core.view.setPadding
import com.hector.proyectofinalbloque2.R
import com.hector.proyectofinalbloque2.modelos.Pokemon
import org.json.JSONObject
import java.net.URL
import kotlin.random.Random

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

class FlickrAPI: IFlickrAPI {

    //Método de descarga de las fotos de flicker de un pokemon
    override fun fotosPokemonAPI(pokemon: Pokemon, contexto: Context): MutableList<ImageView>{

        val listaImageViews: MutableList<ImageView> = ArrayList()

        var imageView: ImageView

        /*Recibo la respuesta y obtengo la cantidad de páginas por cada diez fotos mostradas de
           un pokemon*/
        var respuestaApiFlickr: String =
            URL(
                "https://www.flickr.com/services/rest/?method=flickr.photos" +
                        ".search&api_key=5d6cafbac39898b388199027194efd86&tags="
                        + pokemon.name +
                        "&per_page=10&format=json" +
                        "&nojsoncallback=1"
            ).readText()

        val numPaginas = JSONObject(respuestaApiFlickr).getJSONObject("photos")
            .getString("pages").toInt()

        /*Hago otra llamada con un random entre 1 y la cantidád máxima de páginas posibles, para
        que cada llamada me de una respuesta de fotos distinta*/
        respuestaApiFlickr =
            URL(
                "https://www.flickr.com/services/rest/?method=flickr.photos" +
                        ".search&api_key=5d6cafbac39898b388199027194efd86&tags="
                        + pokemon.name +
                        "&per_page=10&page=" +
                        Random.nextInt(0, numPaginas) + "&format=json" +
                        "&nojsoncallback=1"
            ).readText()

        val fotos = JSONObject(respuestaApiFlickr).getJSONObject("photos")
            .getJSONArray("photo")

        //Genero la url según lo recibido por la api en base a criterios de la misma para cada foto
        for (i in 0 until fotos.length() - 1) {

            val id = fotos.getJSONObject(i).getString("id")
            val farm = fotos.getJSONObject(i).getString("farm")
            val server = fotos.getJSONObject(i).getString("server")
            val secret = fotos.getJSONObject(i).getString("secret")

            val url =
                "https://farm" + farm + ".staticflickr.com/" + server + "/" +
                        id + "_" + secret + ".png"

            val urlImage = URL(url)
            val input = urlImage.openStream()

            //Doy propiedades al imageview que contendra la foto y guardo en lista que devuelvo
            imageView = ImageView(contexto)
            imageView.adjustViewBounds = true
            imageView.setImageBitmap(BitmapFactory.decodeStream(input))
            imageView.setBackgroundResource(R.drawable.imageview2_background)
            imageView.setPadding(25)
            imageView.tag = url
            imageView.isClickable = true

            listaImageViews.add(imageView)

        }

        return listaImageViews
    }
}