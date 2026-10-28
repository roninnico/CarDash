package cl.sm3dash

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : Activity() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var root: LinearLayout
    private lateinit var clock: TextView
    private lateinit var speedText: TextView
    private lateinit var statusText: TextView
    private var themeIndex = 0
    private var speedKmh = 0
    private val themes = listOf(
        Theme("CYBERPUNK", Color.rgb(12, 16, 27), Color.rgb(35, 43, 64), Color.rgb(76, 245, 218), Color.rgb(255, 64, 180)),
        Theme("EVANGELION", Color.rgb(18, 14, 31), Color.rgb(43, 34, 62), Color.rgb(154, 255, 61), Color.rgb(255, 135, 35)),
        Theme("PUNK", Color.rgb(20, 17, 18), Color.rgb(49, 37, 39), Color.rgb(255, 63, 74), Color.rgb(235, 225, 218)),
        Theme("ORIENTAL", Color.rgb(15, 18, 21), Color.rgb(36, 43, 48), Color.rgb(232, 67, 73), Color.rgb(235, 235, 225)),
        Theme("AMOLED", Color.BLACK, Color.rgb(18, 18, 18), Color.WHITE, Color.rgb(0, 220, 160)),
        Theme("CLARO", Color.rgb(228, 233, 238), Color.WHITE, Color.rgb(25, 83, 110), Color.rgb(190, 54, 67))
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        buildDashboard()
        updateClock()
        requestGpsIfNeeded()
    }

    private fun buildDashboard() {
        val t = themes[themeIndex]
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(12, 8, 12, 8)
            setBackgroundColor(t.background)
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        header.addView(label("SM3 // DASH", 20, t.accent, true), weight(1f))
        clock = label("--:--", 24, t.text, true)
        header.addView(clock)
        root.addView(header, LinearLayout.LayoutParams(-1, 42))

        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val music = panel("01  MÚSICA", "Spotify · Música · Radio\nControles multimedia", t.accent, t)
        music.setOnClickListener { showAppChoices() }
        row.addView(music, weight(1f, 1f))

        val car = panel("05  MI SM3", "RENAULT SAMSUNG SM3\nOdómetro: configurar\nAceite: configurar", t.secondary, t)
        car.setOnClickListener { editCarInfo() }
        row.addView(car, weight(1f, 1f))

        val map = panel("02  MAPA", "Abrir Google Maps / Waze\nToca para navegar", t.secondary, t)
        map.setOnClickListener { openMaps() }
        row.addView(map, weight(1f, 1f))
        root.addView(row, LinearLayout.LayoutParams(-1, 0, 1f))

        val bottomInfo = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val weather = panel("03  CLIMA", "Clima: pendiente de integrar", t.secondary, t)
        bottomInfo.addView(weather, weight(1f))
        val speed = panel("06  VELOCIDAD GPS", "GPS esperando señal", t.accent, t)
        speedText = speed.findViewWithTag("speedValue") ?: label("0 km/h", 27, t.text, true).also {
            it.tag = "speedValue"
            speed.addView(it)
        }
        statusText = label("Velocidad estimada por GPS", 10, t.muted, false)
        speed.addView(statusText)
        bottomInfo.addView(speed, weight(1.2f))
        val carQuick = panel("05  MANTENCIÓN", "Kilometraje y cambio de aceite", t.secondary, t)
        carQuick.setOnClickListener { editCarInfo() }
        bottomInfo.addView(carQuick, weight(1f))
        root.addView(bottomInfo, LinearLayout.LayoutParams(-1, 108))

        val shortcuts = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }
        listOf("RADIO", "BLUETOOTH", "APPS", "YOUTUBE", "AJUSTES").forEachIndexed { index, title ->
            val b = Button(this).apply {
                text = title
                isAllCaps = false
                setTextColor(t.text)
                setBackgroundTintList(android.content.res.ColorStateList.valueOf(t.secondary))
                textSize = 11f
                setOnClickListener {
                    when (index) {
                        0 -> showToast("La radio FM depende de la app del fabricante")
                        1 -> startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                        2 -> showAppChoices()
                        3 -> launchPackage("com.google.android.youtube")
                        4 -> openSettings()
                    }
                }
            }
            shortcuts.addView(b, LinearLayout.LayoutParams(0, 44, 1f).apply { setMargins(3, 3, 3, 0) })
        }
        root.addView(shortcuts, LinearLayout.LayoutParams(-1, 48))
        setContentView(root)
    }

    private fun panel(title: String, body: String, accent: Int, t: Theme): LinearLayout {
        val p = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(12, 8, 12, 8)
            setBackgroundColor(t.secondary)
            setOnClickListener { }
        }
        val heading = label(title, 12, accent, true)
        p.addView(heading)
        p.addView(label(body, 12, t.text, false).apply { setPadding(0, 5, 0, 0) })
        p.setPadding(12, 8, 12, 8)
        val params = p.layoutParams
        return p
    }

    private fun label(text: String, size: Int, color: Int, bold: Boolean): TextView =
        TextView(this).apply {
            this.text = text
            textSize = size.toFloat()
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
        }

    private fun weight(w: Float, h: Float = -1f): LinearLayout.LayoutParams =
        LinearLayout.LayoutParams(0, if (h < 0) -1 else 0, w).apply {
            setMargins(4, 4, 4, 4)
        }

    private fun updateClock() {
        if (isFinishing) return
        clock.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        handler.postDelayed({ updateClock() }, 30_000)
    }

    private fun requestGpsIfNeeded() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 41)
        } else {
            startGps()
        }
    }

    private fun startGps() {
        try {
            val lm = getSystemService(LOCATION_SERVICE) as LocationManager
            val provider = when {
                lm.isProviderEnabled(LocationManager.GPS_PROVIDER) -> LocationManager.GPS_PROVIDER
                lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> LocationManager.NETWORK_PROVIDER
                else -> null
            }
            if (provider == null) {
                statusText.text = "Activa ubicación para usar GPS"
                return
            }
            lm.requestLocationUpdates(provider, 1500L, 1f, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    speedKmh = if (location.hasSpeed()) (location.speed * 3.6f).toInt() else 0
                    speedText.text = "$speedKmh km/h"
                    statusText.text = if (location.hasSpeed()) "Velocidad GPS" else "Esperando velocidad GPS"
                }
                @Deprecated("Deprecated in Android")
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {
                    statusText.text = "GPS no disponible"
                }
            })
        } catch (_: SecurityException) {
            statusText.text = "Permiso GPS no concedido"
        } catch (_: Exception) {
            statusText.text = "GPS no disponible"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 41 && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) startGps()
    }

    private fun openMaps() {
        val intent = packageManager.getLaunchIntentForPackage("com.google.android.apps.maps")
        if (intent != null) startActivity(intent)
        else startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=navigation")))
    }

    private fun launchPackage(packageName: String) {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) startActivity(intent) else showToast("Aplicación no encontrada")
    }

    private fun showAppChoices() {
        val apps = arrayOf("Spotify", "YouTube", "Google Maps", "Ajustes Android")
        AlertDialog.Builder(this)
            .setTitle("Abrir aplicación")
            .setItems(apps) { _, which ->
                when (which) {
                    0 -> launchPackage("com.spotify.music")
                    1 -> launchPackage("com.google.android.youtube")
                    2 -> openMaps()
                    3 -> startActivity(Intent(Settings.ACTION_SETTINGS))
                }
            }.show()
    }

    private fun editCarInfo() {
        val options = arrayOf("Editar kilometraje (próxima versión)", "Configurar cambio de aceite (próxima versión)")
        AlertDialog.Builder(this).setTitle("Mi SM3").setItems(options) { _, _ ->
            showToast("La edición del vehículo se incorporará en la siguiente iteración.")
        }.setPositiveButton("Cerrar", null).show()
    }

    private fun openSettings() {
        AlertDialog.Builder(this)
            .setTitle("SM3 Dash · Ajustes")
            .setItems(themes.map { it.name }.toTypedArray()) { _, which ->
                themeIndex = which
                buildDashboard()
                requestGpsIfNeeded()
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    data class Theme(val name: String, val background: Int, val secondary: Int, val accent: Int, val text: Int) {
        val muted: Int get() = if (name == "CLARO") Color.DKGRAY else Color.LTGRAY
    }
}
