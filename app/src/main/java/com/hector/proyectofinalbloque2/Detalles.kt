package com.hector.proyectofinalbloque2

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hector.proyectofinalbloque2.Principal.Companion.servicioPokemonAPI
import com.hector.proyectofinalbloque2.adapters.RecyclerAdapter.Companion.EXTRA_POKEMON
import com.hector.proyectofinalbloque2.modelos.Pokemon
import kotlinx.android.synthetic.main.content_detalles.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

class Detalles : AppCompatActivity() {


    private lateinit var pokemonSeleccionado: Pokemon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalles)

        //Pokemon que se ha seleccionado para el detalle
        pokemonSeleccionado = intent.getSerializableExtra(EXTRA_POKEMON) as Pokemon

        tv_nombre.text = pokemonSeleccionado.name
        tv_respuesta_num.text = pokemonSeleccionado.numero.toString()

        //LLamada a función que me trae el resto de detalles del pokemon y asigna a los textviews
        asyncDescargaDetalles()

        //LLamada que me trae la imagen del pokemon y la muestra en el imageview
        asyncDescargaImagen()

        //Botón que abre el modo de ver fotos del pokemon
        btn_fotos_internet.setOnClickListener {
            val intent2 = Intent(this, FotosInternet::class.java).apply {
                putExtra(EXTRA_POKEMON, pokemonSeleccionado)
            }

            startActivity(intent2)
            finish()
        }
    }


    private fun asyncDescargaDetalles() {
        doAsync {

            val result = servicioPokemonAPI.detallesPokemonAPI(pokemonSeleccionado.numero)

            uiThread {
                tv_respuesta_desc.text = result.descripcion
                tv_respuesta_tipo.text = result.tipo
            }
        }
    }

    private fun asyncDescargaImagen(){

        doAsync {

            val urlImage = URL(pokemonSeleccionado.imagen)
            val input = urlImage.openStream()


            uiThread {
                iv_foto.setImageBitmap(BitmapFactory.decodeStream(input))
                iv_foto.contentDescription = pokemonSeleccionado.name
            }
        }

    }
}
