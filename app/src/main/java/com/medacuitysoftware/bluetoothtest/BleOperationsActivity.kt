package com.medacuitysoftware.bluetoothtest

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.medacuitysoftware.bluetoothtest.ble.ConnectionManager
import com.medacuitysoftware.bluetoothtest.ble.ConnectionEventListener
import com.medacuitysoftware.bluetoothtest.ble.isIndicatable
import com.medacuitysoftware.bluetoothtest.ble.isNotifiable
import com.medacuitysoftware.bluetoothtest.ble.isReadable
import com.medacuitysoftware.bluetoothtest.ble.isWritable
import com.medacuitysoftware.bluetoothtest.ble.isWritableWithoutResponse
import com.medacuitysoftware.bluetoothtest.ble.toHexString
import com.medacuitysoftware.bluetoothtest.databinding.ActivityBleOperationsBinding

import java.text.SimpleDateFormat
import java.util.*

class BleOperationsActivity : AppCompatActivity() {

    private lateinit var device: BluetoothDevice
    private lateinit var binding: ActivityBleOperationsBinding


    private val dateFormatter = SimpleDateFormat("MMM d, HH:mm:ss", Locale.US)
    private val characteristics by lazy {
            ConnectionManager.servicesOnDevice(device)?.flatMap { service ->
                service.characteristics ?: listOf()
            } ?: listOf()
        }
        private val characteristicProperties by lazy {
            characteristics.map { characteristic ->
                characteristic to mutableListOf<CharacteristicProperty>().apply {
                    if (characteristic.isNotifiable()) add(CharacteristicProperty.Notifiable)
                    if (characteristic.isIndicatable()) add(CharacteristicProperty.Indicatable)
                    if (characteristic.isReadable()) add(CharacteristicProperty.Readable)
                    if (characteristic.isWritable()) add(CharacteristicProperty.Writable)
                    if (characteristic.isWritableWithoutResponse()) {
                        add(CharacteristicProperty.WritableWithoutResponse)
                    }
                }.toList()
            }.toMap()
        }
        private val characteristicAdapter: CharacteristicAdapter by lazy {
            CharacteristicAdapter(characteristics) { characteristic ->
                showCharacteristicOptions(characteristic)
            }
        }
        private var notifyingCharacteristics = mutableListOf<UUID>()

        override fun onCreate(savedInstanceState: Bundle?) {
            ConnectionManager.registerListener(connectionEventListener)
            super.onCreate(savedInstanceState)



            device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                ?: error("Missing BluetoothDevice from MainActivity!")
            binding = ActivityBleOperationsBinding.inflate(layoutInflater)
            val view = binding.root
            setContentView(view)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowTitleEnabled(true)
                title = "Ble Test App"
            }
            setupRecyclerView()
            binding.requestMtuButton.setOnClickListener {
                if (binding.mtuField.text.isNotEmpty() && binding.mtuField.text.isNotBlank()) {
                    binding.mtuField.text.toString().toIntOrNull()?.let { mtu ->
                        log("Requesting for MTU value of $mtu")
                        ConnectionManager.requestMtu(device, mtu)
                    } ?: log("Invalid MTU value: ${binding.mtuField.text}")
                } else {
                    log("Please specify a numeric value for desired ATT MTU (23-517)")
                }
                hideKeyboard()
            }
        }

        override fun onDestroy() {
            ConnectionManager.unregisterListener(connectionEventListener)
            ConnectionManager.teardownConnection(device)
            super.onDestroy()
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                android.R.id.home -> {
                    onBackPressed()
                    return true
                }
            }
            return super.onOptionsItemSelected(item)
        }

        private fun setupRecyclerView() {
            binding.characteristicsRecyclerView.apply {
                adapter = characteristicAdapter
                layoutManager = LinearLayoutManager(
                    this@BleOperationsActivity,
                    RecyclerView.VERTICAL,
                    false
                )
                isNestedScrollingEnabled = false
            }

            val animator =  binding.characteristicsRecyclerView.itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }
        }

        @SuppressLint("SetTextI18n")
        private fun log(message: String) {
            val formattedMessage = String.format("%s: %s", dateFormatter.format(Date()), message)
            runOnUiThread {
                val currentLogText = if (binding.logTextView.text.isEmpty()) {
                    "Beginning of log."
                } else {
                    binding.logTextView.text
                }
                binding.logTextView.text = "$currentLogText\n$formattedMessage"
                binding.logScrollView.post { binding.logScrollView.fullScroll(View.FOCUS_DOWN) }
            }
        }

        private fun showCharacteristicOptions(characteristic: BluetoothGattCharacteristic) {
            characteristicProperties[characteristic]?.let { properties ->
//
                Log.i("BleOps", "in char code")
            }
        }

        @SuppressLint("InflateParams")
        private fun showWritePayloadDialog(characteristic: BluetoothGattCharacteristic) {
            Toast.makeText(this,
                "I'm not implementing this yet",
                Toast.LENGTH_LONG).show()


        }

        private val connectionEventListener by lazy {
            ConnectionEventListener().apply {
                onDisconnect = {

                    log("disconnected")


                }

                onCharacteristicRead = { _, characteristic ->
                    log("Read from ${characteristic.uuid}: ${characteristic.value.toHexString()}")
                }

                onCharacteristicWrite = { _, characteristic ->
                    log("Wrote to ${characteristic.uuid}")
                }

                onMtuChanged = { _, mtu ->
                    log("MTU updated to $mtu")
                }

                onCharacteristicChanged = { _, characteristic ->
                    log("Value changed on ${characteristic.uuid}: ${characteristic.value.toHexString()}")
                }

                onNotificationsEnabled = { _, characteristic ->
                    log("Enabled notifications on ${characteristic.uuid}")
                    notifyingCharacteristics.add(characteristic.uuid)
                }

                onNotificationsDisabled = { _, characteristic ->
                    log("Disabled notifications on ${characteristic.uuid}")
                    notifyingCharacteristics.remove(characteristic.uuid)
                }
            }
        }

        private enum class CharacteristicProperty {
            Readable,
            Writable,
            WritableWithoutResponse,
            Notifiable,
            Indicatable;

            val action
                get() = when (this) {
                    Readable -> "Read"
                    Writable -> "Write"
                    WritableWithoutResponse -> "Write Without Response"
                    Notifiable -> "Toggle Notifications"
                    Indicatable -> "Toggle Indications"
                }
        }

        private fun Activity.hideKeyboard() {
            hideKeyboard(currentFocus ?: View(this))
        }

        private fun Context.hideKeyboard(view: View) {
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        private fun EditText.showKeyboard() {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            requestFocus()
            inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }

        private fun String.hexToBytes() =
            this.chunked(2).map { it.toUpperCase(Locale.US).toInt(16).toByte() }.toByteArray()
    }
