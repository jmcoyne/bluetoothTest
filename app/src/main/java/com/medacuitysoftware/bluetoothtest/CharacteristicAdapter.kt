
package com.medacuitysoftware.bluetoothtest

import android.bluetooth.BluetoothGattCharacteristic
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.medacuitysoftware.bluetoothtest.ble.printProperties


class CharacteristicAdapter(
    private val items: List<BluetoothGattCharacteristic>,
    private val onClickListener: ((characteristic: BluetoothGattCharacteristic) -> Unit)
) : RecyclerView.Adapter<CharacteristicAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.row_characteristic,
            parent,
            false
        )
        return ViewHolder(view, onClickListener)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    class ViewHolder(
        private val view: View,
        private val onClickListener: ((characteristic: BluetoothGattCharacteristic) -> Unit)
    ) : RecyclerView.ViewHolder(view) {
        private var characteristic_uuid_text  = view.findViewById(R. id.characteristic_uuid) as TextView
        private val characteristic_properties_text  = view.findViewById(R. id.characteristic_properties) as TextView
        fun bind(characteristic: BluetoothGattCharacteristic) {
            characteristic_uuid_text.text = characteristic.uuid.toString()
            characteristic_properties_text.text = characteristic.printProperties()
            view.setOnClickListener { onClickListener.invoke(characteristic) }
        }
    }
}
