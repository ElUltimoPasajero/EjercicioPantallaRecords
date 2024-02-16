package com.jlara.ud12ej01_solucion

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.jlara.ud12ej01_solucion.databinding.ActivityGameBinding
import java.util.Random

class GameActivity : AppCompatActivity() {
    private lateinit var nick: String
    private var cazados: Long = 0
    private var anchoPantalla = 0
    private var altoPantalla = 0
    private var gameOver = false
    private var aleatorio: Random = Random()
    private lateinit var intentLaunch: ActivityResultLauncher<Intent>
    private lateinit var binding: ActivityGameBinding
    val db = Firebase.firestore
    private var userList = mutableListOf<User>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intentLaunch =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == RESULT_OK) {
                    val accion = result.data?.extras?.getString("accion")
                    when (accion) {
                        "Salir" -> finish()
                        "Reiniciar" -> {
                            nick = result.data?.extras?.getString("nick")!!
                            cazados = 0
                            binding.tvCounter.text = "0"
                            gameOver = false
                            initCuentaAtras()
                            moverPato()
                        }
                    }
                }
            }
        readData()
        initPantalla()
        inicializarComponentesVisuales()
        eventos()
        moverPato()
        initCuentaAtras()

    }

    private fun inicializarComponentesVisuales() {
        val typeface = Typeface.createFromAsset(assets, "pixel.ttf")
        binding.tvCounter.typeface = typeface
        binding.tvTimer.typeface = typeface
        binding.tvNick.typeface = typeface
        val extras = intent.extras
        nick = extras!!.getString(Constantes.EXTRA_NICK)!!
        binding.tvNick.text = nick
    }

    private fun eventos() {
        binding.ivPato.setOnClickListener {
            if (!gameOver) {
                cazados++
                binding.tvCounter.text = cazados.toString()
                binding.ivPato.setImageResource(R.drawable.duck_clicked)
                Handler().postDelayed({
                    binding.ivPato.setImageResource(R.drawable.duck)
                    moverPato()
                }, 500)
            }
        }
    }

    private fun initPantalla() {
        val dm1 = resources.displayMetrics
        anchoPantalla = dm1.widthPixels
        altoPantalla = dm1.heightPixels
    }

    private fun moverPato() {
        val maximoX = anchoPantalla - binding.ivPato.width * 2
        val maximoY = altoPantalla - binding.ivPato.height * 2
        //Generamos un número aleatorio para la coordenada X y otro para la Y
        val randomX = aleatorio.nextInt(maximoX + 1)
        val randomY = aleatorio.nextInt(maximoY + 1)
        //Utilizamos los números aleatorios para mover el pato
        binding.ivPato.x = randomX.toFloat()
        binding.ivPato.y = randomY.toFloat()
    }

    private fun initCuentaAtras() {
        object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val segundosRestantes = millisUntilFinished / 1000
                val texto = "${segundosRestantes}s"
                binding.tvTimer.text = texto
            }

            override fun onFinish() {
                binding.tvTimer.text = getString(R.string.ceroseg)
                gameOver = true
                transferData()
                val intent = Intent(this@GameActivity, RecordsActivity::class.java)
                intent.putExtra(
                    "jugadores",
                    ArrayList(userList)
                ) // userList es tu lista de usuarios
                startActivity(intent)

            }

        }.start()


    }

    private fun mostrarDialogoGameOver() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Has cazado $cazados patos").setTitle("Game Over")
        builder.setCancelable(false)
        builder.setPositiveButton(
            R.string.reiniciar
        ) { dialog, which ->
            cazados = 0
            binding.tvCounter.text = "0"
            gameOver = false
            initCuentaAtras()
            moverPato()
        }
        builder.setNegativeButton(
            R.string.salir
        ) { dialog, which ->
            dialog.dismiss()
            finish()
        }
        val dialogo = builder.create()
        dialogo.show()


    }


    private fun readData() {

        var name: String
        var points: Long
        var user: User
        val db = Firebase.firestore
        db.collection("puntuaciones").get().addOnSuccessListener { result ->
            for (document in result) {
                name = document.id
                points = document.data.get("puntos") as Long
                user = User(name, points)
                userList.add(user)


            }

        }.addOnFailureListener { error ->

            Log.e("FirebaseError", error.message.toString())

        }


    }

    private fun updateData(user: User) {
        val db = Firebase.firestore
        db.collection("puntuaciones").document(user.name).update("puntos", user.points)
            .addOnSuccessListener {
                Log.i("Firebase", "Upload Data Seccesfull")

            }.addOnFailureListener { error ->
                Log.e("FirebaseError", error.message.toString())

            }


    }

    private fun insertData(user: User) {
        val db = Firebase.firestore
        val data = hashMapOf("puntos" to user.points)
        db.collection("puntuaciones").document(user.name).set(data).addOnSuccessListener {
            Log.i("Firebase", "Insert Data Seccesfull")

        }.addOnFailureListener { error ->
            Log.e("FirebaseError", error.message.toString())

        }

    }

    /*  private fun transferData() {
        var update = false
        var insert = true

        if (userList.isNotEmpty()) {

            for (user in userList) {

                if (user.name.equals(nick)) {

                    insert = false
                    if (user.points < cazados) {

                        update = true
                    }

                }

            }

        }

        val player = User(nick, cazados)

        if (update) {
            updateData(player)

        }
        if (insert) {
            userList.add(player)
            insertData(player)


        }

    }*/

    private fun transferData(): Boolean {
        val currentUser = userList.find { it.name == nick }

        if (currentUser != null) {
            if (currentUser.points < cazados) {
                currentUser.points = cazados
                updateData(currentUser)
                return true
            } else {
                return false


            }

        }else{
            val newUser = User(nick,cazados)
            userList.add(newUser)
            insertData(newUser)
            return false
        }
    }
}





