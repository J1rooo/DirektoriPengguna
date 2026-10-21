package com.example.direktoripengguna

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.direktoripengguna.data.User
import com.example.direktoripengguna.network.RetrofitClient
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var progress: ProgressBar
    private lateinit var btnUlang: Button
    private lateinit var listUser: ListView

    private var daftarUser: List<User> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        progress = findViewById(R.id.progress)
        btnUlang = findViewById(R.id.btnUlang)
        listUser = findViewById(R.id.listUser)

        btnUlang.setOnClickListener { muatUsers() }

        listUser.setOnItemClickListener { _, _, position, _ ->
            val user = daftarUser[position]
            val intent = Intent(this, PostActivity::class.java)
            intent.putExtra(PostActivity.EXTRA_USER_ID, user.id)
            intent.putExtra(PostActivity.EXTRA_USER_NAME, user.name)
            startActivity(intent)
        }

        muatUsers()
    }

    private fun muatUsers() {
        progress.visibility = View.VISIBLE
        btnUlang.visibility = View.GONE
        tvStatus.text = "Memuat data..."

        lifecycleScope.launch {
            try {
                daftarUser = RetrofitClient.api.getUsers()
                listUser.adapter = UserAdapter(this@MainActivity, daftarUser)
                tvStatus.text = "${daftarUser.size} pengguna dimuat"
            } catch (e: HttpException) {
                tampilkanError("Server error: HTTP ${e.code()}")
            } catch (e: IOException) {
                tampilkanError("Tidak ada koneksi internet. Periksa jaringan Anda, lalu coba lagi.")
            } finally {
                progress.visibility = View.GONE
            }
        }
    }

    // Status di header + tombol Coba Lagi + dialog (sesuai gambar kondisi error)
    private fun tampilkanError(pesan: String) {
        tvStatus.text = "Status: gagal memuat"
        btnUlang.visibility = View.VISIBLE
        AlertDialog.Builder(this)
            .setTitle("Gagal memuat data")
            .setMessage(pesan)
            .setNegativeButton("TUTUP", null)
            .setPositiveButton("COBA LAGI") { _, _ -> muatUsers() }
            .show()
    }
}
