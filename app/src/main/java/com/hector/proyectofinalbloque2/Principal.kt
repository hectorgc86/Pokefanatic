package com.hector.proyectofinalbloque2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hector.proyectofinalbloque2.adapters.ViewPagerAdapter
import com.hector.proyectofinalbloque2.servicios.*
import kotlinx.android.synthetic.main.activity_principal.*

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

class Principal : AppCompatActivity() {

    //Declaración de distintos servicios
    companion object{
        lateinit var servicioPokemonAPI: IPokemonAPI
        lateinit var servicioFlickrAPI: IFlickrAPI
        lateinit var servicioDatabase: IDatabase
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        //Inicializo los servicios
        servicioPokemonAPI = PokemonAPI()
        servicioFlickrAPI = FlickrAPI()
        servicioDatabase = Database()

        //Genero y asigno los fragments a los tabs
        val adapter = ViewPagerAdapter(supportFragmentManager)

        adapter.addFragment(Busqueda(), "Busqueda")
        adapter.addFragment(Fototeca(), "Fototeca")

        viewPager.adapter = adapter

        tabs.setupWithViewPager(viewPager)

    }
}


