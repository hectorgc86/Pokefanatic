package com.hector.proyectofinalbloque2.servicios

import com.google.firebase.database.DatabaseReference

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

//Interfaz de conexíon a Firebase
interface IDatabase {

    fun guardarFoto(urlFoto: String)

    fun borrarFoto(urlFoto: String)

    fun importarFotos(): DatabaseReference
}