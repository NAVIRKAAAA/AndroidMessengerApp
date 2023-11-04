package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rhorbachevskyi.viewpager.R
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.databinding.FragmentProfileBinding
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragmentDirections
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.adapter.ViewPagerAdapter
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.DataStore
import com.rhorbachevskyi.viewpager.presentation.utils.ext.invisible
import com.rhorbachevskyi.viewpager.presentation.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.presentation.utils.ext.log
import com.rhorbachevskyi.viewpager.presentation.utils.ext.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
@AndroidEntryPoint
class UserProfile : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel: UserProfileViewModel by viewModels()
    private lateinit var userData: UserResponse.Data

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialUser()
        setUserProfile()

    }

    private fun initialUser() {
        userData = viewModel.getUser()
    }

    override fun setListeners() {
        with(binding) {
            buttonViewContacts.setOnClickListener { toContactList() }
            textViewLogout.setOnClickListener { logoutFromAccount() }
            buttonMessageTop.setOnClickListener { toEditProfileScreen() }
            root.setOnRefreshListener { swipeToRefresh() }
            imageViewSendProfile.setOnClickListener { setBluetooth() }
        }
    }


    private fun swipeToRefresh() {
        with(binding) {
            lifecycleScope.launch {
                imageViewSendProfile.invisible()
                clearData()
                delay(1000L)
                initialUser()
                setUserProfile()
                imageViewSendProfile.visible()
                root.isRefreshing = false
            }
        }
    }

    private fun clearData() {
        with(binding) {
            textViewName.text = ""
            textViewCareer.text = ""
            textViewHomeAddress.text = ""
            imageViewProfileImage.loadImage(R.drawable.ic_user_photo)
        }
    }

    private fun toContactList() {
        (parentFragment as? ViewPagerFragment)?.openFragment(ViewPagerAdapter.Fragments.CONTACTS.ordinal)
    }

    private fun logoutFromAccount() {
        lifecycleScope.launch {
            DataStore.deleteDataFromDataStore(requireContext(), Constants.KEY_EMAIL)
            DataStore.deleteDataFromDataStore(requireContext(), Constants.KEY_PASSWORD)
            DataStore.deleteDataFromDataStore(requireContext(), Constants.KEY_REMEMBER_ME)
        }

        val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToSignInFragment()
        navController.navigate(direction)
    }

    private fun toEditProfileScreen() {
        val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToEditProfile()
        navController.navigate(direction)
    }

    private fun setUserProfile() {
        UserDataHolder.userData = viewModel.getUser()
        with(binding) {
            textViewName.text = userData.user.name
            textViewCareer.text = userData.user.career
            textViewHomeAddress.text = userData.user.address
            imageViewProfileImage.loadImage(userData.user.image)
        }
    }


    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                log("${it.key} = ${it.value}")
            }
        }

    private fun setBluetooth() {

        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        requestBluetoothEnable.launch(enableBtIntent)

    }

    private val requestBluetoothEnable =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val direction = ViewPagerFragmentDirections.actionViewPagerFragmentToBluetoothDevicesFragment()
                navController.navigate(direction)
            }
        }
}