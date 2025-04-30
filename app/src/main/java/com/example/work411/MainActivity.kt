package com.example.work411

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.work411.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sensorManager: SensorManager
    private var currentSensor: Sensor? = null
    private var currentSensorType: Int = Sensor.TYPE_LIGHT
    private var dataSensor: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // По умолчанию выбран датчик освещенности
        binding.l.isChecked = true
        setSensor(Sensor.TYPE_LIGHT)

        binding.sensorsGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.l -> setSensor(Sensor.TYPE_LIGHT)
                R.id.r -> setSensor(Sensor.TYPE_ROTATION_VECTOR)
                R.id.a -> setSensor(Sensor.TYPE_ACCELEROMETER)
            }
        }
    }

    private fun setSensor(sensorType: Int) {
        sensorManager.unregisterListener(this)
        currentSensorType = sensorType
        currentSensor = sensorManager.getDefaultSensor(sensorType)
        if (currentSensor == null) {
            val toastText = when (sensorType) {
                Sensor.TYPE_LIGHT -> getString(R.string.sensorAbsentL)
                Sensor.TYPE_ROTATION_VECTOR -> getString(R.string.sensorAbsentR)
                Sensor.TYPE_ACCELEROMETER -> getString(R.string.sensorAbsentA)
                else -> "Нет такого датчика"
            }
            Toast.makeText(this, toastText, Toast.LENGTH_LONG).show()
            dataSensor = ""
            binding.sensText = dataSensor
        } else {
            dataSensor = when (sensorType) {
                Sensor.TYPE_LIGHT -> getString(R.string.light)
                Sensor.TYPE_ROTATION_VECTOR -> getString(R.string.rotor)
                Sensor.TYPE_ACCELEROMETER -> getString(R.string.accelerometer)
                else -> ""
            }
            binding.sensText = dataSensor
            sensorManager.registerListener(this, currentSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onResume() {
        super.onResume()
        currentSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != currentSensorType) return

        val sensorName = when (event.sensor.type) {
            Sensor.TYPE_LIGHT -> getString(R.string.light)
            Sensor.TYPE_ROTATION_VECTOR -> getString(R.string.rotor)
            Sensor.TYPE_ACCELEROMETER -> getString(R.string.accelerometer)
            else -> "Датчик"
        }

        val formattedValues = when (event.sensor.type) {
            Sensor.TYPE_LIGHT -> "%.2f".format(event.values[0])
            Sensor.TYPE_ROTATION_VECTOR -> {
                val x = event.values.getOrNull(0) ?: 0f
                val y = event.values.getOrNull(1) ?: 0f
                val z = event.values.getOrNull(2) ?: 0f
                "x=%.2f, y=%.2f, z=%.2f".format(x, y, z)
            }
            Sensor.TYPE_ACCELEROMETER -> {
                val x = event.values.getOrNull(0) ?: 0f
                val y = event.values.getOrNull(1) ?: 0f
                val z = event.values.getOrNull(2) ?: 0f
                "x=%.2f, y=%.2f, z=%.2f".format(x, y, z)
            }
            else -> ""
        }

        dataSensor = "$sensorName: $formattedValues"
        binding.sensText = dataSensor
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}

