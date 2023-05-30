package com.example.eHandel.adminActivities

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.eHandel.R
import com.example.eHandel.databinding.ActivityAdminAddNewProductBinding
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class AdminAddNewProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminAddNewProductBinding
    private lateinit var auth: FirebaseAuth
    val storage = FirebaseStorage.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    private var tipo = "";
    private var imageUri: Uri? = null
    private var imageByteArray: ByteArray? = null
    var downloadUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recogerTipo()

        binding.ivNewProductPhoto.setOnClickListener {
            productImage()
        }

        binding.btnAddNewProduct.setOnClickListener {
            var name = binding.etAddNewProductName.text.toString().trim()
            var vendor = binding.etAddNewProductVendor.text.toString().trim()
            var price = binding.etAddNewProductPrice.text.toString().toDouble()
            var quantity = binding.etAddNewProductQuantity.text.toString().toInt()
            addNewProduct(name, vendor, price, quantity)
        }

        // Esto es provisional quitar cuando se empiece a crear la activity
        auth = FirebaseAuth.getInstance()
        var nombre = auth.currentUser?.email
        //Toast.makeText(this, "Ha entrado el ADMIN", Toast.LENGTH_SHORT).show()
        //auth.signOut()
        //startActivity(Intent(this, MainActivity::class.java))
    }

    companion object {
        const val PICK_IMAGE_REQUEST_GALERIA=20
        const val PICK_IMAGE_REQUEST_CAMERA=21
    }

    private fun recogerTipo(){
        val mibundle = intent.extras
        tipo = mibundle?.getString("type").toString()
        binding.tvAddNewProductType.text = binding.tvAddNewProductType.text.toString().trim() + tipo

    }

    private fun productImage(){
        var options = listOf(resources.getString(R.string.productImagePopupCamera), resources.getString(R.string.productImagePopupGallery))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.productImagePopup))
        builder.setItems(options.toTypedArray()){_, item ->
            when{
                options[item] == resources.getString(R.string.productImagePopupCamera)-> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, PICK_IMAGE_REQUEST_CAMERA)
                }
                options[item] == resources.getString(R.string.productImagePopupGallery)-> {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent, PICK_IMAGE_REQUEST_GALERIA)
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST_GALERIA && resultCode == Activity.RESULT_OK){
            imageUri = data?.data
            imageByteArray = null
            binding.ivNewProductPhoto.setImageURI(imageUri)
            binding.ivNewProductPhoto.setTag(imageUri)
        } else if(requestCode == PICK_IMAGE_REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageUri = null
            imageByteArray = changeBitmap(applicationContext, imageBitmap)
            binding.ivNewProductPhoto.setImageBitmap(imageBitmap)
            binding.ivNewProductPhoto.setTag(imageBitmap)
            binding.ivNewProductPhoto.scaleX = 1.0F
            binding.ivNewProductPhoto.scaleY = 1.0F

        }
    }

    private fun changeBitmap(context: Context, bitmap: Bitmap): ByteArray? {
        var byteArrayOutputStream = ByteArrayOutputStream()
        var byteArray : ByteArray? = null
        try {
            var img = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
            byteArray = byteArrayOutputStream.toByteArray()
        }catch (e: IOException){
            e.printStackTrace()
        }finally {
            try {
               byteArrayOutputStream?.close()
            }catch (e: IOException){
                e.printStackTrace()
            }
        }
        return byteArray
    }

    private fun addNewProduct(name: String, vendor: String, price: Double, quantity: Number) {
        if (imageUri != null) {
            val storageRef = storage.reference.child("photos")
            val imageRef = storageRef.child("Image_${System.currentTimeMillis()}.jpg")
            imageRef.putFile(imageUri!!).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val firestoreDB = FirebaseFirestore.getInstance()
                    val db = Firebase.firestore
                    val data = hashMapOf(
                        "name" to name,
                        "type" to tipo,
                        "image" to uri.toString(), "vendor" to vendor,
                        "price" to price,
                        "quantity" to quantity,
                    )
                    firestoreDB.collection("products")
                        .document(name)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document.exists()) {
                                    Toast.makeText(
                                        this,
                                        "El producto creado ya existe",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    db.collection("products").document(name)
                                        .set(data)
                                        .addOnSuccessListener {
                                            Log.d(
                                                ContentValues.TAG,
                                                "DocumentSnapshot successfully written!"
                                            )
                                        }
                                        .addOnFailureListener { e -> println(e.message) }
                                }
                            }
                        }
                }
            }
        } else if (imageByteArray != null) {
            val storageRef = storage.reference.child("photos")
            val imageRef = storageRef.child("Image_${System.currentTimeMillis()}.jpg")
            imageRef.putBytes(imageByteArray!!).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    println(uri.toString())
                    val firestoreDB = FirebaseFirestore.getInstance()
                    val db = Firebase.firestore

                    val data = hashMapOf(
                        "name" to name,
                        "type" to tipo,
                        "image" to uri.toString(), "vendor" to vendor,
                        "price" to price,
                        "quantity" to quantity,
                    )

                    firestoreDB.collection("products")
                        .document(name)
                        .get()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val document = task.result
                                if (document.exists()) {
                                    Toast.makeText(
                                        this,
                                        "El producto creado ya existe",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    db.collection("products").document(name)
                                        .set(data)
                                        .addOnSuccessListener {
                                            Log.d(
                                                ContentValues.TAG,
                                                "DocumentSnapshot successfully written!"
                                            )
                                        }
                                        .addOnFailureListener { e -> println(e.message) }
                                }
                            }
                        }
                }
            }.addOnFailureListener {
                println("Ha dado fallo")
            }.addOnCanceledListener {
                println("Se ha cancelado")
            }
        }
    }
}