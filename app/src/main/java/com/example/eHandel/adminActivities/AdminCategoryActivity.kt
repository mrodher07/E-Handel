package com.example.eHandel.adminActivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.eHandel.R
import com.example.eHandel.databinding.ActivityAdminCategoryBinding

class AdminCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminCategoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.LinearLayoutAddNewClothes.setOnClickListener {
            intentNewProduct("Clothes")
        }
        binding.LinearLayoutAddNewFood.setOnClickListener{
            intentNewProduct("Food")
        }
        binding.LinearLayoutAddNewElectronics.setOnClickListener{
            intentNewProduct("Electronics")
        }
        binding.LinearLayoutAddNewFurniture.setOnClickListener{
            intentNewProduct("Furniture")
        }
    }

    private fun intentNewProduct(type: String){
        val intent = Intent(this, AdminAddNewProductActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
    }
}