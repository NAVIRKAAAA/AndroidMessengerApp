package com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth

import android.bluetooth.BluetoothManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.rhorbachevskyi.viewpager.databinding.FragmentBluetoothDeviceListBinding
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.adapter.BluetoothDeviceAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BluetoothDevicesFragment :
    BaseFragment<FragmentBluetoothDeviceListBinding>(FragmentBluetoothDeviceListBinding::inflate) {

    private val viewModel: BluetoothDevicesViewModel by viewModels()

    private val bluetoothManager by lazy {
        requireActivity().getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }
    private val adapter: BluetoothDeviceAdapter = BluetoothDeviceAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.startScan()

        setDeviceList()
    }

    private fun setDeviceList() {
        with(binding) {
            recyclerViewDevices.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewDevices.adapter = adapter
        }
    }

    override fun setObservers() {
        lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect {
               adapter.submitList(it.pairedDevices)
            }
        }
    }
}