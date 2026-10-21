package com.example.direktoripengguna

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.direktoripengguna.data.PostBaru
import com.example.direktoripengguna.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class PostActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_USER_ID = "user_id"
        const val EXTRA_USER_NAME = "user_name"
    }

    private var userId = 0

    private lateinit var etJudul: EditText
    private lateinit var btnTambah: Button
    private lateinit var tvStatus: TextView
    private lateinit var listPost: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        userId = intent.getIntExtra(EXTRA_USER_ID, 0)
        val namaUser = intent.getStringExtra(EXTRA_USER_NAME) ?: "Postingan"
        title = namaUser

        findViewById<TextView>(R.id.tvNamaPengguna).text = namaUser.uppercase()
        etJudul = findViewById(R.id.etJudul)
        btnTambah = findViewById(R.id.btnTambah)
        tvStatus = findViewById(R.id.tvStatus)
        listPost = findViewById(R.id.listPost)

        btnTambah.setOnClickListener { kirimPost() }

        muatPosts()
    }

    private fun muatPosts() {
        tvStatus.text = "Memuat postingan..."

        lifecycleScope.launch {
            try {
                val posts = RetrofitClient.api.getPostsByUser(userId)
                listPost.adapter = PostAdapter(this@PostActivity, posts)
                tvStatus.text = "${posts.size} postingan"
            } catch (e: HttpException) {
                tampilkanError("Server error: HTTP ${e.code()}", ulang = { muatPosts() })
            } catch (e: IOException) {
                tampilkanError(
                    "Tidak ada koneksi internet. Periksa jaringan Anda, lalu coba lagi.",
                    ulang = { muatPosts() }
                )
            }
        }
    }

    // Status di header + dialog dengan tombol TUTUP dan COBA LAGI
    private fun tampilkanError(pesan: String, judul: String = "Gagal memuat data", ulang: () -> Unit) {
        tvStatus.text = "Status: gagal memuat"
        AlertDialog.Builder(this)
            .setTitle(judul)
            .setMessage(pesan)
            .setNegativeButton("TUTUP", null)
            .setPositiveButton("COBA LAGI") { _, _ -> ulang() }
            .show()
    }

    private fun kirimPost() {
        val judul = etJudul.text.toString().trim()
        if (judul.isEmpty()) {
            etJudul.error = "Judul tidak boleh kosong"
            return
        }

        btnTambah.isEnabled = false
        lifecycleScope.launch {
            try {
                val baru = PostBaru(
                    title = judul,
                    body = "Dibuat dari aplikasi Direktori Pengguna",
                    userId = userId
                )
                val hasil = RetrofitClient.api.buatPost(baru)
                tvStatus.text = "Tersimpan dengan id ${hasil.id}"
                etJudul.text.clear()
            } catch (e: HttpException) {
                tvStatus.text = "Gagal menyimpan: HTTP ${e.code()}"
            } catch (e: IOException) {
                tampilkanError(
                    "Tidak ada koneksi internet. Postingan belum terkirim, coba lagi.",
                    judul = "Gagal mengirim postingan",
                    ulang = { kirimPost() }
                )
            } finally {
                btnTambah.isEnabled = true
            }
        }
    }
}
