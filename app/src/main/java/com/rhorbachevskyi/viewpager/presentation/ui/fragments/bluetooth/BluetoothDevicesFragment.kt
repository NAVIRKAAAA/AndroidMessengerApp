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
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.adapter.interfaces.DevicesClickListener
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.model.BluetoothDevice
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.bluetooth.model.BluetoothDeviceDomain
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.log
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
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
    private val adapter: BluetoothDeviceAdapter =
        BluetoothDeviceAdapter(listener = object : DevicesClickListener {
            override fun onDeviceClick(device: BluetoothDeviceDomain) {

                viewModel.connectToDevice(device)
            }
        })


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.startScan()

        setDeviceList()
    }

    override fun setListeners() {
        with(binding) {
            imageViewNavigationBack.setOnClickListener { navController.navigateUp() }
            textViewStartServer.setOnClickListener { viewModel.waitForIncomingConnections() }
        }
    }

    private fun setDeviceList() {
        with(binding) {
            recyclerViewDevices.layoutManager = LinearLayoutManager(requireContext())
            recyclerViewDevices.adapter = adapter
        }
    }

    override fun setObservers() {
        lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(viewLifecycleOwner.lifecycle).collect { state ->
                when {
                    state.isConnecting -> {
                        log("isConnecting")
                        binding.progressBar.visible()

                    }

                    state.isConnected -> {
                        log("isConnected")

                    }

                    else -> {
                        binding.progressBar.invisible()
                        adapter.submitList(merge(state.pairedDevices, state.scannedDevices))
                    }
                }
                state.errorMessage?.let { showSnackBar(it) }
            }
        }
    }

    private fun merge(
        pairedDevices: List<BluetoothDevice>,
        scannedDevices: List<BluetoothDevice>
    ): List<BluetoothDevice> {
        val mergedSet = linkedSetOf<BluetoothDevice>()
        mergedSet.addAll(pairedDevices)
        mergedSet.addAll(scannedDevices)
        return mergedSet.toList()
    }
}