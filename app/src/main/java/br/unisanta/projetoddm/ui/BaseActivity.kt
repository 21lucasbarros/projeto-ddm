package br.unisanta.projetoddm.ui

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    companion object {
        // se alguem algum dia quiser mudar o tamanho da fonte
        // 1.0 = normal | 1.1 = 10% maior | 1.2 = 20% maior | 0.9 = 10% menor
        const val ESCALA_FONTE = 1.15f
    }

    override fun attachBaseContext(newBase: Context) {
        val config = Configuration(newBase.resources.configuration)
        // questao de acessibilidade
        config.fontScale = config.fontScale * ESCALA_FONTE
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }
}