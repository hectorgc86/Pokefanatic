package com.hector.proyectofinalbloque2.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hector.proyectofinalbloque2.Detalles
import com.hector.proyectofinalbloque2.R
import com.hector.proyectofinalbloque2.modelos.Pokemon
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

//Adapter para recyclerview
class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    companion object{
        const val EXTRA_POKEMON = "EXTRA_POKEMON"
    }

    private var listaPokemon: MutableList<Pokemon> = ArrayList()
    private lateinit var context: Context

    fun recyclerAdapter(listaPokemon: MutableList<Pokemon>, context: Context) {
        this.context = context
        this.listaPokemon = listaPokemon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        return ViewHolder(
            inflater.inflate(
                R.layout.item_recyclerview, parent,
                false
            )
        )

    }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = listaPokemon[position]
            holder.bind(item, this.context)
        }

    override fun getItemCount(): Int {
        return listaPokemon.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val nombrePokemon = view.findViewById(
            R.id.tv_rv_nombre
        ) as TextView

        private val numeroPokemon = view.findViewById(
            R.id.tv_rv_numero
        ) as TextView

        private val imagenPokemon = view.findViewById(
            R.id.iv_animalImage
        ) as ImageView

        private val esFavorito = view.findViewById(
            R.id.btn_favorito
        ) as Button

        fun bind(pokemon: Pokemon, context: Context) {

            nombrePokemon.text = pokemon.name
            numeroPokemon.text = pokemon.numero.toString()

            //Compruebo si es favorito o no y cambio el icono
            if(!pokemon.favorito){
                esFavorito.setBackgroundResource(R.drawable.pokeball_abierta)
            }else{
                esFavorito.setBackgroundResource(R.drawable.pokeball)
            }

            //Hilo para traer la foto en base a la url de imagen
            doAsync {

                val urlImage = URL(pokemon.imagen)
                val input = urlImage.openStream()


                uiThread {
                    imagenPokemon.setImageBitmap(BitmapFactory.decodeStream(input))
                }
            }

            //Si pulso sobre el icono, le seteo si es o no favorito y cambio el icono
            esFavorito.setOnClickListener {
                if(!pokemon.favorito){
                    pokemon.favorito = true
                    esFavorito.setBackgroundResource(R.drawable.pokeball)
                }else{
                    pokemon.favorito = false
                    esFavorito.setBackgroundResource(R.drawable.pokeball_abierta)
                }

            }

            //Si pulso sobre el item que se lanze un intent con un pokemon serializado a detalles
            itemView.setOnClickListener {
                val intent1 = Intent(context, Detalles::class.java).apply {
                    putExtra(EXTRA_POKEMON, pokemon)
                }

                context.startActivity(intent1)
            }
        }


    }


}