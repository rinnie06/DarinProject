
package com.example.ourbook

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class BooksAdapter(private var book: List<Book>, context: Context): RecyclerView.Adapter<BooksAdapter.BookViewHolder>() {

    private val db: DatabaseHelper = DatabaseHelper(context)

    class BookViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nama : TextView = itemView.findViewById(R.id.txtNama)
        val alias : TextView = itemView.findViewById(R.id.txtNamaPanggilan)
        val email : TextView = itemView.findViewById(R.id.txtEmail)
        val alamat : TextView = itemView.findViewById(R.id.txtAlamat)
        val tglLahir : TextView = itemView.findViewById(R.id.txtTglLahir)
        val hp : TextView = itemView.findViewById(R.id.txtHP)
        val foto : ImageView = itemView.findViewById(R.id.Photo)
        val updateButton : ImageView = itemView.findViewById(R.id.btnEdit)
        val deleteButton : ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = book.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = book[position]
        holder.nama.text = book.name
        holder.alias.text = book.surename
        holder.email.text = book.email
        holder.alamat.text = book.address
        holder.tglLahir.text = book.date
        holder.hp.text = book.hp

        if (book.image != null) {
            val bmp = BitmapFactory.decodeByteArray(book.image, 0, book.image.size)
            holder.foto.setImageBitmap(bmp)
        } else {
            holder.foto.setImageResource(R.drawable.baseline_insert_photo_24)
        }

        holder.updateButton.setOnClickListener{
            val intent = Intent(holder.itemView.context, update::class.java).apply {
                putExtra("book_id", book.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            db.deleteBook(book.id)
            refreshData(db.getAllBooks())
            Toast.makeText(holder.itemView.context, "Note Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshData(newBooks : List<Book>) {
        book = newBooks
        notifyDataSetChanged()
    }
}