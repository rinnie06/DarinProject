package com.example.ourbook

import android.Manifest
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.ourbook.databinding.ActivityUpdateBinding
import java.io.ByteArrayOutputStream
import com.squareup.picasso.Picasso
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageActivity
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageIntentChooser
import com.canhub.cropper.CropImageOptions
import com.example.ourbook.MainActivity
import java.util.Calendar


class update : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private lateinit var db: DatabaseHelper
    private var bookId: Int = -1
    val CAMERA_REQUEST = 100
    val STORAGE_PERMISSION = 101

    val cameraPermissions: Array<String> =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    val storagePermissions: Array<String> = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    val cropImageLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri: Uri? = result.uriContent
            Picasso.get().load(uri).into(binding.editPhoto)
        } else {
            val error = result.error
            error?.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        db = DatabaseHelper(this)
        setContentView(binding.root)

        bookId = intent.getIntExtra("book_id", -1)
        if (bookId == -1) {
            finish()
            return
        }

        val book = db.getBookByID(bookId)
        binding.editNama.setText(book.name)
        binding.editPanggilan.setText(book.surename)
        binding.editEmail.setText(book.email)
        binding.editAlamat.setText(book.address)
        binding.editTglLahir.setText(book.date)
        binding.editNotelp.setText(book.hp)

        if (book.image != null) {
            val bmp = BitmapFactory.decodeByteArray(book.image, 0, book.image.size)
            binding.editPhoto.setImageBitmap(bmp)
        }

        binding.editPhoto.setOnClickListener {
            var avatar = 0
            if (avatar == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPersmission()
                } else {
                    pickFromGallery()
                }
            } else if (avatar == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }
            }
        }

        binding.editTglLahir.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    binding.editTglLahir.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        binding.doneButton.setOnClickListener {
            val newName = binding.editNama.text.toString()
            val newSurename = binding.editPanggilan.text.toString()
            val newEmail = binding.editEmail.text.toString()
            val newAddress = binding.editAlamat.text.toString()
            val newDate = binding.editTglLahir.text.toString()
            val newHP = binding.editNotelp.text.toString()
            val emailPattern = "^[\\w.-]+@[\\w.-]+\\.com$".toRegex()

            when {
                newName.isEmpty() || newSurename.isEmpty() || newEmail.isEmpty() || newAddress.isEmpty() ||
                        newDate.isEmpty() || newHP.isEmpty() -> {
                    Toast.makeText(this, "Book cannot be empty!", Toast.LENGTH_SHORT).show()
                }

                !emailPattern.matches(newEmail) -> {
                    Toast.makeText(
                        this,
                        "Please enter a valid email with '@' and '.com'",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    val updateBook = Book(
                        bookId,
                        newName,
                        newSurename,
                        newEmail,
                        newAddress,
                        newDate,
                        newHP,
                        db.ImageViewToByte(binding.editPhoto)
                    )
                    db.updateBook(updateBook)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun requestStoragePermission() {
        requestPermissions(storagePermissions, STORAGE_PERMISSION)
    }

    private fun checkStoragePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
        return result
    }

    private fun pickFromGallery() {
        cropImageLauncher.launch(CropImageContractOptions(null, CropImageOptions()))
    }

    private fun requestCameraPersmission() {
        requestPermissions(cameraPermissions, CAMERA_REQUEST)
    }

    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == (PackageManager.PERMISSION_GRANTED)
        val result2 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == (PackageManager.PERMISSION_GRANTED)
        return result && result2
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.size > 0) {
                    val cameraAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccept) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(
                            this,
                            "Enable Camera and Storage Permissions",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            STORAGE_PERMISSION -> {
                if (grantResults.size > 0) {
                    val storegaAccept = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storegaAccept) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(this, "Enable Storage Permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}