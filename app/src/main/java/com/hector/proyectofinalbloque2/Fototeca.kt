package com.hector.proyectofinalbloque2

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.hector.proyectofinalbloque2.Principal.Companion.servicioDatabase
import com.hector.proyectofinalbloque2.utils.GestionPermisos
import kotlinx.android.synthetic.main.fragment_modo_fototeca.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL

//Héctor Granja Cortés
//2ºDAM Semipresencial
//PMDM

class Fototeca : Fragment() {

    //Declaración de variables
    private lateinit var fragmentContext: Context
    private var idFotoClicada: String = ""
    private lateinit var bitmapSelected:Bitmap
    private val MY_PERMISSIONS_SET_WALLPAPER= 234
    private lateinit var gestionPermisos: GestionPermisos

    //Para saber el context desde el activity
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentContext = context

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(
            R.layout.fragment_modo_fototeca, container,
            false
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {

        val inflater: MenuInflater = menuInflater

        inflater.inflate(R.menu.menu_fototeca, menu)
        super.onCreateOptionsMenu(menu, inflater)

        //Limpio la lista e importo fotos de la Firebase
        ll_fototeca.removeAllViews()

        importarFotosFirebase()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {

        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = activity!!.menuInflater
        inflater.inflate(R.menu.menu_fototeca_contextual, menu)

    }

    override fun onContextItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            //Pido permisos si pulso sobre guardar como fondo de pantalla del dispositivo
            R.id.contextual_fondopantalla_fototeca -> {
                if (ContextCompat.checkSelfPermission( fragmentContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    asignarWallpaper()
                }else{
                    gestionPermisos =
                        GestionPermisos(
                            super.getActivity() as Activity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            MY_PERMISSIONS_SET_WALLPAPER
                        )
                    gestionPermisos.checkPermissions()
                }
                return true
            }
            R.id.contextual_borrar -> {

                //LLamo al dialog para que recordar al usuario si quiere borrar imágen
                mostrarDialogBorrar()

                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun asignarWallpaper(){
        val manager = WallpaperManager.getInstance(fragmentContext)
        manager.setBitmap(bitmapSelected)
        }


    //Dialog de borrar foto: Si acepta, llamo a la API y borro foto, actualizo e importo fotos de BD
    private fun mostrarDialogBorrar() {

        val builder = AlertDialog.Builder(fragmentContext, R.style.tema_dialog)
        builder.setTitle("Borrar")
        builder.setMessage("¿Desea borrar foto?")
        builder.setPositiveButton(android.R.string.ok) { dialog, which ->
            servicioDatabase.borrarFoto(idFotoClicada)

            ll_fototeca.removeAllViews()

            importarFotosFirebase()

            Toast.makeText(
                fragmentContext,
                "Imagen borrada de fototeca", Toast.LENGTH_LONG
            ).show()
        }

        builder.setNeutralButton(android.R.string.cancel, null)
        builder.show()
    }


    private fun importarFotosFirebase() {

        //Muestro progressbar
        progressBar.visibility = View.VISIBLE

        val listaImageViews: MutableList<ImageView> = ArrayList()

        var imageView: ImageView

        //Recibo el DatabaseReference de la API con las fotos
        val dbFotos = servicioDatabase.importarFotos()

        dbFotos.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                Log.e("onCancelled", "Error!", p0.toException())
            }

            override fun onDataChange(p0: DataSnapshot) {

                doAsync {

                    //Recorro y recojo la id de la BD y la URL de cada foto
                    p0.children.forEach {
                        val key: String = it.key.toString()
                        val value: String = it.value.toString()

                        val urlImage = URL(value)

                        val input = urlImage.openStream()

                        /*Adjusto imagen a los márgenes, seteo el bitmap de la url al ImageView,
                        le añado estilo,le pongo como tag la id que tiene en la base de datos,
                        y lo hago clickable y finalmente añado a una lista
                         */
                        imageView = ImageView(fragmentContext)
                        imageView.adjustViewBounds = true
                        imageView.setImageBitmap(BitmapFactory.decodeStream(input))
                        imageView.setBackgroundResource(R.drawable.imageview2_background)
                        imageView.setPadding(25)
                        imageView.tag = key
                        imageView.isClickable = true

                        listaImageViews.add(imageView)
                    }

                    uiThread {

                        //Oculto progressbar
                        progressBar.visibility = View.INVISIBLE

                        /*Recorro la lista para añadirle un listener donde obtendré el bitmap de
                        la foto, y registro su menu contextual
                         */
                        for (imaView in listaImageViews) {
                            ll_fototeca.addView(imaView)

                            imaView.setOnClickListener {
                                idFotoClicada = imaView.tag.toString()
                                bitmapSelected = imaView.drawToBitmap()
                                registerForContextMenu(imaView)
                            }
                        }
                    }
                }

            }

        })

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
}