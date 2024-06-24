package com.example.myfeelings

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myfeelings.utilities.CustomBarDrawable
import com.example.myfeelings.utilities.CustomCircleDrawable
import com.example.myfeelings.utilities.Emociones
import com.example.myfeelings.utilities.JSONFile
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var jsonFile: JSONFile? = null
    var veryHappy = 0.0F
    var happy = 0.0F
    var neutral = 0.0F
    var sad = 0.0F
    var verySad = 0.0F
    var data: Boolean = false
    var lista = ArrayList<Emociones>()

    lateinit var icon: ImageView
    lateinit var graph: View
    lateinit var graphVeryHappy: View
    lateinit var graphHappy: View
    lateinit var graphNeutral: View
    lateinit var graphSad: View
    lateinit var graphVerySad: View
    lateinit var guardarButton: Button
    lateinit var veryHappyButton: ImageButton
    lateinit var happyButton: ImageButton
    lateinit var neutralButton: ImageButton
    lateinit var sadButton: ImageButton
    lateinit var verySadButton: ImageButton

    fun fetchingData() {
        try {
            val json: String = jsonFile?.getData(context = this) ?: ""
            if (json != "") {
                this.data = true
                val jsonArray: JSONArray = JSONArray(json)
                this.lista = parseJson(jsonArray)
                for (i in lista) {
                    when (i.nombre) {
                        "Muy feliz" -> veryHappy = i.total
                        "Feliz" -> happy = i.total
                        "Neutral" -> neutral = i.total
                        "Triste" -> sad = i.total
                        "Muy triste" -> verySad = i.total
                    }
                }
            } else {
                this.data = false
            }
        } catch (exception: JSONException) {
            exception.printStackTrace()
        }
    }

    fun parseJson(jsonArray: JSONArray): ArrayList<Emociones> {
        val lista = ArrayList<Emociones>()
        for (i in 0..jsonArray.length()) {
            try {
                val nombre = jsonArray.getJSONObject(i).getString("nombre")
                val porcentaje = jsonArray.getJSONObject(i).getDouble("porcentaje").toFloat()
                val color = jsonArray.getJSONObject(i).getInt("color")
                val total = jsonArray.getJSONObject(i).getDouble("total").toFloat()
                val emocion = Emociones(nombre, porcentaje, color, total)
                lista.add(emocion)
            } catch (exception: JSONException) {
                exception.printStackTrace()
            }
        }
        return lista
    }

    fun actualizarGrafica() {
        val total = veryHappy + happy + neutral + sad + verySad
        val pVH: Float = (veryHappy * 100 / total).toFloat()
        val pH: Float = (happy * 100 / total).toFloat()
        val pN: Float = (neutral * 100 / total).toFloat()
        val pS: Float = (sad * 100 / total).toFloat()
        val pVS: Float = (verySad * 100 / total).toFloat()

        Log.d("porcentajes", "very happy $pVH")
        Log.d("porcentajes", "happy $pH")
        Log.d("porcentajes", "neutral $pN")
        Log.d("porcentajes", "sad $pS")
        Log.d("porcentajes", "very sad $pVS")

        lista.clear()
        lista.add(Emociones("Muy feliz", pVH, R.color.mustard, veryHappy))
        lista.add(Emociones("Feliz", pH, R.color.orange, happy))
        lista.add(Emociones("Neutral", pN, R.color.greenie, neutral))
        lista.add(Emociones("Triste", pS, R.color.blue, sad))
        lista.add(Emociones("Muy triste", pVS, R.color.deepBlue, verySad))

        val fondo = CustomCircleDrawable(this, lista)

        graphVeryHappy.background =
            CustomBarDrawable(this, Emociones("Muy feliz", pVH, R.color.mustard, veryHappy))
        graphHappy.background =
            CustomBarDrawable(this, Emociones("Feliz", pH, R.color.orange, happy))
        graphNeutral.background =
            CustomBarDrawable(this, Emociones("Neutral", pN, R.color.greenie, neutral))
        graphSad.background = CustomBarDrawable(this, Emociones("Triste", pS, R.color.blue, sad))
        graphVerySad.background =
            CustomBarDrawable(this, Emociones("Muy triste", pVS, R.color.deepBlue, verySad))
        graph.background = fondo
    }


    fun iconoMayoria() {
        if (happy > veryHappy && happy > neutral && happy > sad && happy > verySad) {
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_happy))
        } else if (veryHappy > happy && veryHappy > neutral && veryHappy > sad && veryHappy > verySad) {
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_veryhappy))
        } else if (neutral > veryHappy && neutral > happy && neutral > sad && neutral > verySad) {
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_neutral))
        } else if (sad > happy && sad > neutral && sad > veryHappy && sad > verySad) {
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_sad))
        } else if (verySad > happy && verySad > neutral && verySad > sad && veryHappy < verySad) {
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_verysad))
        }
    }

    fun guardar() {
        val jsonArray = JSONArray()
        var o = 0
        for (i in lista) {
            Log.d("objetos", i.toString())
            val j = JSONObject()
            j.put("nombre", i.nombre)
            j.put("porcentaje", i.porcentaje)
            j.put("color", i.color)
            j.put("total", i.total)
            jsonArray.put(o, j)
            o++
        }
        jsonFile?.saveData(this, jsonArray.toString())
        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        jsonFile = JSONFile()
        icon = findViewById(R.id.icon)
        graph = findViewById(R.id.graph)
        graphVeryHappy = findViewById(R.id.graphVeryHappy)
        graphHappy = findViewById(R.id.graphHappy)
        graphNeutral = findViewById(R.id.graphNeutral)
        graphSad = findViewById(R.id.graphSad)
        graphVerySad = findViewById(R.id.graphVerySad)
        guardarButton = findViewById(R.id.guardarButton)
        veryHappyButton = findViewById(R.id.veryHappyButton)
        happyButton = findViewById(R.id.happyButton)
        neutralButton = findViewById(R.id.neutralButton)
        sadButton = findViewById(R.id.sadButton)
        verySadButton = findViewById(R.id.verySadButton)

        fetchingData()
        if (data) {
            val emociones = ArrayList<Emociones>()
            val fondo = CustomCircleDrawable(this, emociones)
            graph.background = fondo
            graphVeryHappy.background =
                CustomBarDrawable(this, Emociones("Muy feliz", 0.0f, R.color.mustard, veryHappy))
            graphHappy.background =
                CustomBarDrawable(this, Emociones("Feliz", 0.0f, R.color.orange, happy))
            graphNeutral.background =
                CustomBarDrawable(this, Emociones("Neutral", 0.0f, R.color.greenie, neutral))
            graphSad.background =
                CustomBarDrawable(this, Emociones("Triste", 0.0f, R.color.blue, sad))
            graphVerySad.background =
                CustomBarDrawable(this, Emociones("Muy triste", 0.0f, R.color.deepBlue, verySad))
        } else {
            actualizarGrafica()
            iconoMayoria()
        }

        guardarButton.setOnClickListener {
            guardar()
        }

        veryHappyButton.setOnClickListener{
            veryHappy++
            iconoMayoria()
            actualizarGrafica()
        }
        happyButton.setOnClickListener{
            happy++
            iconoMayoria()
            actualizarGrafica()
        }
        neutralButton.setOnClickListener{
            neutral++
            iconoMayoria()
            actualizarGrafica()
        }
        sadButton.setOnClickListener{
            sad++
            iconoMayoria()
            actualizarGrafica()
        }
        verySadButton.setOnClickListener{
            verySad++
            iconoMayoria()
            actualizarGrafica()
        }

    }
}