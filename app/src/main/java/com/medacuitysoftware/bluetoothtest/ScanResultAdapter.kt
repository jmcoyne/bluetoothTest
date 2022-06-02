package com.medacuitysoftware.bluetoothtest

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.TextView


class ScanResultAdapter (
    private val items: List<ScanResult>,
    private val onClickListener: ((device: ScanResult) -> Unit)
    ) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.row_scan_result,
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
            private val onClickListener: ((device: ScanResult) -> Unit)
        ) : RecyclerView.ViewHolder(view) {
            val name_text  = view.findViewById(R. id.device_name) as TextView
            val mac_address_text  = view.findViewById(R. id.mac_address) as TextView
            val signal_strength_text  = view.findViewById(R. id.signal_strength) as TextView
            @SuppressLint("MissingPermission")
            fun bind(result: ScanResult) {
                name_text.text = result.device.name ?: "Unnamed"
                mac_address_text.text = result.device.address
                signal_strength_text.text = "${result.rssi} dBm"
                view.setOnClickListener { onClickListener.invoke(result) }
            }
        }
}