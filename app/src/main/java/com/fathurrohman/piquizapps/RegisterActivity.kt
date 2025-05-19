package com.fathurrohman.piquizapps

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.fathurrohman.piquizapps.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.tvToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.edtEmailRegister.text.toString()
            val password = binding.edtPasswordRegister.text.toString()
            val name = binding.edtName.text.toString()
            val studentClass = binding.edtClass.text.toString()

            // Validasi email
            if (email.isEmpty()) {
                binding.edtEmailRegister.error = "Email harus diisi"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.edtEmailRegister.error = "Email tidak valid"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }

            // Validasi password
            if (password.isEmpty()) {
                binding.edtPasswordRegister.error = "Password harus diisi"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }
            if (password.length < 6) {
                binding.edtPasswordRegister.error = "Password minimal 6 karakter"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            // Validasi nama dan kelas
            if (name.isEmpty()) {
                binding.edtName.error = "Nama harus diisi"
                binding.edtName.requestFocus()
                return@setOnClickListener
            }

            if (studentClass.isEmpty()) {
                binding.edtClass.error = "Kelas harus diisi"
                binding.edtClass.requestFocus()
                return@setOnClickListener
            }

            RegisterFirebase(email, password, name, studentClass)
        }
    }

    private fun RegisterFirebase(email: String, password: String, name: String, studentClass: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        val db = FirebaseFirestore.getInstance()
                        val user = hashMapOf(
                            "uid" to userId,
                            "email" to email,
                            "name" to name,
                            "class" to studentClass
                        )
                        db.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Register berhasil!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, LoginActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Gagal simpan user: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Register gagal: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
