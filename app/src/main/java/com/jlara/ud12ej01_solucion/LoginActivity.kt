package com.jlara.ud12ej01_solucion

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jlara.ud12ej01_solucion.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var nick:String
    private lateinit var binding:ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val typeface = Typeface.createFromAsset(assets, "pixel.ttf")
        binding.etNick.typeface=typeface
        binding.btnStart.typeface=typeface
        binding.btnStart.setOnClickListener {
            nick = binding.etNick.text.toString()
            when {
                nick.isEmpty() -> {
                    binding.etNick.error="El nombre de usuario es obligatorio"
                }
                nick.length < 3 -> {
                    binding.etNick.error="Debe tener al menos 3 caracteres"
                }
                else -> {
                    val intent = Intent(this@LoginActivity, GameActivity::class.java)
                    intent.putExtra(Constantes.EXTRA_NICK, nick)
                    startActivity(intent)
                }
            }
        }
    }
}