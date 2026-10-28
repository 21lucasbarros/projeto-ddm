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

class LoginActivity : BaseActivity() {

    private val authRepository = AuthRepository()

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        progress = findViewById(R.id.progress)

        findViewById<TextView>(R.id.tvGoRegister).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            fazerLogin()
        }
    }

    private fun fazerLogin() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Preencha e-mail e senha.", Toast.LENGTH_SHORT).show()
            return
        }

        mostrarCarregando(true)
        authRepository.login(email, password,
            onSuccess = {
                irParaHome()
            },
            onError = { mensagem ->
                mostrarCarregando(false)
                Toast.makeText(this, mensagem, Toast.LENGTH_LONG).show()
            }
        )
    }

    private fun mostrarCarregando(carregando: Boolean) {
        btnLogin.isEnabled = !carregando
        progress.visibility = if (carregando) View.VISIBLE else View.GONE
    }

    private fun irParaHome() {
        val intent = Intent(this, HomeActivity::class.java)
        // limpa a pilha: o botão voltar não retorna para o login
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}