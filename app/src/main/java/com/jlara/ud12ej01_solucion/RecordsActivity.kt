package com.jlara.ud12ej01_solucion

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.jlara.ud12ej01_solucion.databinding.ActivityLoginBinding
import com.jlara.ud12ej01_solucion.databinding.ActivityRecordsBinding

class RecordsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var binding: ActivityRecordsBinding
        super.onCreate(savedInstanceState)
        binding = ActivityRecordsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val typeface = Typeface.createFromAsset(assets, "pixel.ttf")

        binding.recordText.typeface = typeface
        binding.playerRecordOne.typeface = typeface
        binding.playerRecordTwo.typeface = typeface
        binding.playerRecordThree.typeface = typeface
        binding.playerRecordFour.typeface = typeface
        binding.playerRecordFive.typeface = typeface


        val jugadores = intent.getSerializableExtra("jugadores") as? ArrayList<User>

        if (jugadores != null) {
            // Ordenar la lista de jugadores por puntuación de mayor a menor
            val jugadoresOrdenados = jugadores.sortedByDescending { it.points }

            // Asignar los nombres ordenados a los TextViews
            val textViews = arrayOf(
                binding.playerRecordOne,
                binding.playerRecordTwo,
                binding.playerRecordThree,
                binding.playerRecordFour,
                binding.playerRecordFive
            )

            for ((index, jugador) in jugadoresOrdenados.withIndex()) {
                if (index < textViews.size) {
                    textViews[index].text = "${jugador.points} - ${jugador.name}"
                }
            }
        }
    }
}