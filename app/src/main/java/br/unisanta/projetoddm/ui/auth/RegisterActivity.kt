package br.unisanta.projetoddm.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.unisanta.projetoddm.R
import br.unisanta.projetoddm.data.repository.AuthRepository
import br.unisanta.projetoddm.ui.BaseActivity
import br.unisanta.projetoddm.ui.home.HomeActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RegisterActivity : BaseActivity() {

    private val authRepository = AuthRepository()

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirm: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirm = findViewById(R.id.etConfirm)
        btnRegister = findViewById(R.id.btnRegister)
        progress = findViewById(R.id.progress)

        findViewById<TextView>(R.id.tvGoLogin).setOnClickListener {
            finish() // volta para o login
        }

        btnRegister.setOnClickListener {
            cadastrar()
        }
    }

    private fun cadastrar() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val confirm = etConfirm.text.toString()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            mostrarMensagem("Preencha todos os campos.")
            return
        }
        if (password.length < 6) {
            mostrarMensagem("A senha precisa ter pelo menos 6 caracteres.")
            return
        }
        if (password != confirm) {
            mostrarMensagem("As senhas não conferem.")
            return
        }

        mostrarCarregando(true)
        authRepository.register(name, email, password,
            onSuccess = {
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            },
            onError = { mensagem ->
                mostrarCarregando(false)
                mostrarMensagem(mensagem)
            }
        )
    }

    private fun mostrarCarregando(carregando: Boolean) {
        btnRegister.isEnabled = !carregando
        progress.visibility = if (carregando) View.VISIBLE else View.GONE
    }

    private fun mostrarMensagem(mensagem: String) {
        Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
    }
}