package com.fathurrohman.piquizapps

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fathurrohman.piquizapps.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    lateinit var binding : ActivityRegisterBinding
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.tvToLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.edtEmailRegister.text.toString()
            val password = binding.edtPasswordRegister.text.toString()
            //validate email
            if (email.isEmpty()){
                binding.edtEmailRegister.error = "Email Harus diisi"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }
            //email not match
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtEmailRegister.error = "Email tidak valid"
                binding.edtEmailRegister.requestFocus()
                return@setOnClickListener
            }
            //validate password
            if (password.isEmpty()){
                binding.edtPasswordRegister.error = "Password Harus diisi"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }
            //length pw
            if (password.length < 6){
                binding.edtPasswordRegister.error = "Password minimal 6 karakter"
                binding.edtPasswordRegister.requestFocus()
                return@setOnClickListener
            }

            RegisterFirebase(email,password)
        }
    }

    private fun RegisterFirebase(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Register berhasil!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Register gagal: ${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}