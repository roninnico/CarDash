
package cl.sm3dash

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    private val bg = Color.rgb(12, 16, 27)
    private val accent = Color.rgb(0, 220, 255)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 8, 12, 8)
            setBackgroundColor(bg)
        }

        val header = TextView(this).apply {
            text = "SM3 DASH  •  RENAULT SAMSUNG"
            textSize = 20f
            setTextColor(accent)
            gravity = Gravity.CENTER
        }
        root.addView(header, LinearLayout.LayoutParams(-1, 48))

        val content = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        content.addView(panel("MÚSICA", "Reproductor"), weight())
        content.addView(panel("MAPA", "Abrir navegación"), weight())
        content.addView(panel("VEHÍCULO", "Renault Samsung SM3"), weight())

        root.addView(content, LinearLayout.LayoutParams(-1, 0, 1f))

        val speed = TextView(this).apply {
            text = "GPS  •  0 km/h"
            textSize = 30f
            gravity = Gravity.CENTER
            setTextColor(accent)
        }
        root.addView(speed, LinearLayout.LayoutParams(-1, 70))

        val shortcuts = TextView(this).apply {
            text = "RADIO     BLUETOOTH     APPS     YOUTUBE     AJUSTES"
            textSize = 15f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
        }
        root.addView(shortcuts, LinearLayout.LayoutParams(-1, 42))

        setContentView(root)
    }

    private fun panel(title: String, detail: String): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(6, 6, 6, 6)
            setBackgroundColor(Color.rgb(25, 34, 50))

            addView(TextView(this@MainActivity).apply {
                text = title
                textSize = 19f
                gravity = Gravity.CENTER
                setTextColor(accent)
            })

            addView(TextView(this@MainActivity).apply {
                text = detail
                textSize = 14f
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
            })
        }
    }

    private fun weight(): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(0, -1, 1f)
}
