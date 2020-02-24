package com.hector.proyectofinalbloque2.servicios

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

class Database: IDatabase {

    //Guardo una única foto en la Realtime Database
     override fun guardarFoto(urlFoto: String) {

        val database = FirebaseDatabase.getInstance().reference

        database.child("fotos").child("urlfoto").push().setValue(urlFoto)
    }

    //Borro una única foto en la Realtime Database
    override fun borrarFoto(urlFoto: String) {

        val database = FirebaseDatabase.getInstance().reference

        val dbFotos = database.child("fotos").child("urlfoto").
            child(urlFoto).removeValue()
    }

    //Traigo un set de todas las fotos de la Realtime Database
    override fun importarFotos(): DatabaseReference {

        val database = FirebaseDatabase.getInstance().reference

        return database.child("fotos").child("urlfoto")

    }

}