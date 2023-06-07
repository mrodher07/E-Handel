package com.example.eHandel

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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.eHandel.adminActivities.AdminAddNewProductActivity
import com.example.eHandel.databinding.ActivityUserAddNewProductBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException

class UserAddNewProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserAddNewProductBinding
    private lateinit var auth: FirebaseAuth
    val storage = FirebaseStorage.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val db = Firebase.firestore
    private var tipo = "";
    private var imageUri: Uri? = null
    private var imageByteArray: ByteArray? = null
    var downloadUrl: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        comprobarSiEsVendedor()

        binding.ivNewProductPhoto.setOnClickListener {
            productImage()
        }

        binding.toolbarSettingsClose.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        binding.btnAddNewProduct.setOnClickListener {
            addNewProduct()
        }
    }

    fun comprobarSiEsVendedor(){
        var email = auth.currentUser?.email
        val users = db.collection("user")
        val loggedUser = users.document(email.toString())
        loggedUser.get()
            .addOnSuccessListener { document ->
                if(document != null){
                    if(document.getBoolean("isVendor") == true){
                        binding.rlAddNewProduct.visibility = View.VISIBLE
                        binding.rlNoPermission.visibility = View.GONE
                    }else{
                        binding.rlAddNewProduct.visibility = View.GONE
                        binding.rlNoPermission.visibility = View.VISIBLE
                    }
                }
            }
    }

    companion object {
        const val PICK_IMAGE_REQUEST_GALERIA=20
        const val PICK_IMAGE_REQUEST_CAMERA=21
    }

    private fun productImage(){
        var options = listOf(resources.getString(R.string.productImagePopupCamera), resources.getString(R.string.productImagePopupGallery))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.productImagePopup))
        builder.setItems(options.toTypedArray()){_, item ->
            when{
                options[item] == resources.getString(R.string.productImagePopupCamera)-> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent,
                        AdminAddNewProductActivity.PICK_IMAGE_REQUEST_CAMERA
                    )
                }
                options[item] == resources.getString(R.string.productImagePopupGallery)-> {
                    val intent = Intent(Intent.ACTION_PICK)
                    intent.type = "image/*"
                    startActivityForResult(intent,
                        AdminAddNewProductActivity.PICK_IMAGE_REQUEST_GALERIA
                    )
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == AdminAddNewProductActivity.PICK_IMAGE_REQUEST_GALERIA && resultCode == Activity.RESULT_OK){
            imageUri = data?.data
            imageByteArray = null
            binding.ivNewProductPhoto.setImageURI(imageUri)
            binding.ivNewProductPhoto.setTag(imageUri)
        } else if(requestCode == AdminAddNewProductActivity.PICK_IMAGE_REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
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

    private fun addNewProduct(){
        if (imageUri != null){
            val storageRef = storage.reference.child("photos")
            val imageRef = storageRef.child("Image_${System.currentTimeMillis()}.jpg")
            imageRef.putFile(imageUri!!).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val firestoreDB = FirebaseFirestore.getInstance()
                    val db = Firebase.firestore
                    var email = auth.currentUser?.email
                    db.collection("user").document(email.toString()).get()
                        .addOnSuccessListener { documentSnapshot ->
                            val data = hashMapOf(
                                "name" to binding.etAddNewProductName.text.toString(),
                                "type" to binding.etAddNewProductType.text.toString(),
                                "image" to uri.toString(),
                                "vendor" to documentSnapshot.getString("username"),
                                "price" to binding.etAddNewProductPrice.text.toString().toDouble(),
                                "quantity" to binding.etAddNewProductQuantity.text.toString().toInt(),
                                "country" to documentSnapshot.getString("country"),
                            )
                            firestoreDB.collection("products")
                                .document(binding.etAddNewProductName.text.toString())
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
                                            db.collection("products").document(binding.etAddNewProductName.text.toString())
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
            }
        } else if (imageByteArray != null) {
            val storageRef = storage.reference.child("photos")
            val imageRef = storageRef.child("Image_${System.currentTimeMillis()}.jpg")
            imageRef.putBytes(imageByteArray!!).addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val firestoreDB = FirebaseFirestore.getInstance()
                    val db = Firebase.firestore
                    var email = auth.currentUser?.email
                    db.collection("user").document(email.toString()).get()
                        .addOnSuccessListener { documentSnapshot ->
                            val data = hashMapOf(
                                "name" to binding.etAddNewProductName.text.toString(),
                                "type" to binding.etAddNewProductType.text.toString(),
                                "image" to uri.toString(),
                                "vendor" to documentSnapshot.getString("username"),
                                "price" to binding.etAddNewProductPrice.text.toString().toDouble(),
                                "quantity" to binding.etAddNewProductQuantity.text.toString().toInt(),
                                "country" to documentSnapshot.getString("country"),
                            )
                            firestoreDB.collection("products")
                                .document(binding.etAddNewProductName.text.toString())
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
                                            db.collection("products").document(binding.etAddNewProductName.text.toString())
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
            }.addOnFailureListener {
                println("Ha dado fallo")
            }.addOnCanceledListener {
                println("Se ha cancelado")
            }
        } else if (imageUri == null && imageByteArray == null){
            val firestoreDB = FirebaseFirestore.getInstance()
            val db = Firebase.firestore
            var email = auth.currentUser?.email
            db.collection("user").document(email.toString()).get()
                .addOnSuccessListener { documentSnapshot ->
                    val data = hashMapOf(
                        "name" to binding.etAddNewProductName.text.toString(),
                        "type" to binding.etAddNewProductType.text.toString(),
                        "image" to "https://firebasestorage.googleapis.com/v0/b/e-handel-8838a.appspot.com/o/photos%2Fimagenotfound.png?alt=media&token=b98faff4-7c09-46ae-9632-dd46730870d3",
                        "vendor" to documentSnapshot.getString("username"),
                        "price" to binding.etAddNewProductPrice.text.toString().toDouble(),
                        "quantity" to binding.etAddNewProductQuantity.text.toString().toInt(),
                        "country" to documentSnapshot.getString("country"),
                    )
                    firestoreDB.collection("products")
                        .document(binding.etAddNewProductName.text.toString())
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
                                    db.collection("products").document(binding.etAddNewProductName.text.toString())
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
    }
}