package com.example.ourbook

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ourbook.databinding.ActivityFrontpageBinding

class frontpage : AppCompatActivity() {
    private lateinit var binding: ActivityFrontpageBinding
    private lateinit var db: DatabaseHelper
    private lateinit var Adapter: BooksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFrontpageBinding.inflate(layoutInflater)
        db = DatabaseHelper(this)
        Adapter = BooksAdapter(db.getAllBooks(), this)
        setContentView(binding.root)

        binding.booksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.booksRecyclerView.adapter = Adapter

        binding.addButton.setOnClickListener{
            val intent = Intent(this, add::class.java)
            startActivity(intent)
        }

        binding.aboutUsButton.setOnClickListener{
            val intent = Intent(this, about_book::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Adapter.refreshData(db.getAllBooks())
    }
}
