package com.hector.proyectofinalbloque2

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import com.hector.proyectofinalbloque2.Principal.Companion.servicioDatabase
import com.hector.proyectofinalbloque2.Principal.Companion.servicioFlickrAPI
import com.hector.proyectofinalbloque2.adapters.RecyclerAdapter.Companion.EXTRA_POKEMON
import com.hector.proyectofinalbloque2.modelos.Pokemon
import com.hector.proyectofinalbloque2.utils.GestionPermisos
import kotlinx.android.synthetic.main.content_fotos_internet.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

class FotosInternet : AppCompatActivity() {

    //Declaración de variables
    private lateinit var pokemonSeleccionado: Pokemon
    private var idFotoClicada: String = ""
    private val MY_PERMISSIONS_SET_WALLPAPER= 234
    private lateinit var gestionPermisos: GestionPermisos
    private lateinit var bitmapSelected: Bitmap
    private val CHANNEL1_ID = "com.hector.proyectofinalbloque2"
    private val notificationId1 = 123456
    private lateinit var builder1: NotificationCompat.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotos_internet)

        //Proceso de notificación
        createNotificationChannel(
            CHANNEL1_ID,
            R.string.channel1_name,
            R.string.channel1_description,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        builder1 = NotificationCompat.Builder(this, CHANNEL1_ID)

        builder1.apply {
            setSmallIcon(R.mipmap.ic_launcher_round)
            setContentTitle(getString(R.string.channel1_name))
            setContentText(getString(R.string.channel1_description))
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(true)
        }

        //Pokemon recibido desde Detalle
        pokemonSeleccionado = intent.getSerializableExtra(EXTRA_POKEMON) as Pokemon

        //LLamada a descargar las fotos de la API de Flickr
        descargarFotosApiFlickr(pokemonSeleccionado, this)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater

        inflater.inflate(R.menu.menu_fotos_internet, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            /*Si se pulsa el boton de recarga, hago otra llamada a la API que me mostrará
            otras 10 imágenes*/

            R.id.menu_recargar -> {
                ll_fotosinternet.removeAllViews()
                descargarFotosApiFlickr(pokemonSeleccionado, this)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_fotos_internet_contextual, menu)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {

        return when (item!!.itemId) {

            /*Si se pulsa guardar foto en el menu contextual, llamo a la API y guardo la foto en
            la Firebase, genero una notificación y un Toast*/

            R.id.contextual_guardar -> {

                servicioDatabase.guardarFoto(idFotoClicada)

                with(NotificationManagerCompat.from(this)) {
                    notify(notificationId1, builder1.build())
                }
                Toast.makeText(
                    this,
                    "Imagen guardada en fototeca", Toast.LENGTH_LONG
                ).show()
                return true
            }

            /*Si selecciono establecer fondo pantalla dispositivo, pido permisos y si son aceptados
            llamo a asignaWallpaper
            */
             R.id.contextual_fondopantalla -> {

                 if (ContextCompat.checkSelfPermission( this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                     Log.d("DEBUG", "El permiso ya está concedido.")
                     asignarWallpaper()
                 }else{
                     gestionPermisos =
                         GestionPermisos(
                             this,
                             Manifest.permission.WRITE_EXTERNAL_STORAGE,
                             MY_PERMISSIONS_SET_WALLPAPER
                         )
                     gestionPermisos.checkPermissions()
                 }

                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    //Asigna el bitmap de la foto del pokemon actual al wallpaper del teléfono
    private fun asignarWallpaper(){
        val manager = WallpaperManager.getInstance(this)

                manager.setBitmap(bitmapSelected)
        }


    private fun descargarFotosApiFlickr(pokemon: Pokemon, contexto: Context) {

        //Muestro el progressbar
        progressBar.visibility = View.VISIBLE

        doAsync {

            //Recojo la lista de ImageViews
            val listaImageViews = servicioFlickrAPI.fotosPokemonAPI(pokemon,contexto)

            uiThread {

                //Oculto progressbar
                progressBar.visibility = View.INVISIBLE

                /*A cada imageview de la lista le asigno un listener, un tag con la url, me quedo
                con el bitmap de la imagen, y le asigno que pueda ser desplegado en el
                su contextmenu*/

                for (imgView in listaImageViews) {
                    ll_fotosinternet.addView(imgView)

                    imgView.setOnClickListener {
                        idFotoClicada = imgView.tag.toString()
                        bitmapSelected = imgView.drawToBitmap()
                        registerForContextMenu(imgView)
                    }
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray ){
        when (requestCode) { MY_PERMISSIONS_SET_WALLPAPER -> {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                asignarWallpaper()
            }
            return
        }

        }
    }


    private fun createNotificationChannel(channel: String, name: Int, desc: Int, importance: Int) {
            val name1 = getString(name)
            val descriptionText = getString(desc)
            val channel1 = NotificationChannel(channel, name1, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }


}