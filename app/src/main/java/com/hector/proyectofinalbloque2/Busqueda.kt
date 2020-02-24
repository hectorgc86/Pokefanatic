package com.hector.proyectofinalbloque2

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.hector.proyectofinalbloque2.Principal.Companion.servicioPokemonAPI
import com.hector.proyectofinalbloque2.adapters.RecyclerAdapter
import com.hector.proyectofinalbloque2.modelos.Pokemon
import kotlinx.android.synthetic.main.fragment_modo_busqueda.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

class Busqueda : Fragment() {

    //Declaración de variables
    private var recyclerAdapter: RecyclerAdapter = RecyclerAdapter()
    private lateinit var listaPokemons: MutableList<Pokemon>
    private var listaFiltrada: MutableList<Pokemon> = ArrayList()
    private lateinit var fragmentContext: Context

    //Para poder conseguir el contexto del fragment
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)

        //Llamada a función de descarga
        asyncDescargaPokemons()

        return inflater.inflate(
            R.layout.fragment_modo_busqueda, container,
            false
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_busqueda, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val searchItem = menu.findItem(R.id.lupa_busqueda)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            //Por cada pulsación de tecla filtro los resultados y se los paso al recyclerview
            override fun onQueryTextChange(query: String): Boolean {

                listaFiltrada =
                    listaPokemons.filter { p -> p.name.toLowerCase(Locale.ENGLISH).contains(query.toLowerCase(Locale.ENGLISH)) }
                            as MutableList<Pokemon>

                setUpRecyclerView(listaFiltrada)

                return false
            }
        })
    }

    //Código para mostrar solo los favoritos
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_favoritos -> {

                if (item.isChecked) {
                    listaFiltrada = listaPokemons.filter { p -> p.favorito } as MutableList<Pokemon>

                    if(listaFiltrada.size == 0){
                        Toast.makeText(
                            fragmentContext,
                            "Todavía no se han añadido \n pokemons a favoritos", Toast.LENGTH_LONG
                        ).show()
                    }

                    item.isChecked = false
                    item.setIcon(R.drawable.pokeball)

                    setUpRecyclerView(listaFiltrada)
                } else {
                    item.isChecked = true
                    item.setIcon(R.drawable.pokeball_abierta)

                    setUpRecyclerView(listaPokemons)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun asyncDescargaPokemons() {


        doAsync {

            //LLamo a la API para traerme la lista completa de Pokemons
            listaPokemons = servicioPokemonAPI.listaPokemonsAPI()

            uiThread {
            //Limpio la información recibida y genero la url de cada imagen
                for (i in 0 until listaPokemons.size - 1) {

                    val pokemon = listaPokemons[i]

                    pokemon.name = pokemon.name.toCharArray()[0].toUpperCase() +
                            pokemon.name.substring(1)
                    pokemon.numero = (pokemon.url.substring(34, (pokemon.url.length) - 1)).toInt()
                    pokemon.imagen =
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" +
                                pokemon.numero + ".png"

                }
                //Asigno la lista al recyclerview
                setUpRecyclerView(listaPokemons)
            }
        }
    }

    private fun setUpRecyclerView(listaPokemons: MutableList<Pokemon>) {

        rv_busqueda.setHasFixedSize(true)

        rv_busqueda.layoutManager = LinearLayoutManager(fragmentContext)
        recyclerAdapter.recyclerAdapter(listaPokemons, fragmentContext)

        rv_busqueda.adapter = recyclerAdapter
    }



}