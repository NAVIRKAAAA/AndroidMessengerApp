package com.rhorbachevskyi.viewpager.presentation.ui.fragments.userprofile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import com.rhorbachevskyi.viewpager.data.model.UserResponse
import com.rhorbachevskyi.viewpager.data.userdataholder.UserDataHolder
import com.rhorbachevskyi.viewpager.databinding.FragmentProfileBinding
import com.rhorbachevskyi.viewpager.presentation.ui.base.BaseFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragment
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.ViewPagerFragmentDirections
import com.rhorbachevskyi.viewpager.presentation.ui.fragments.viewpager.adapter.ViewPagerAdapter
import com.rhorbachevskyi.viewpager.presentation.utils.Constants
import com.rhorbachevskyi.viewpager.presentation.utils.ext.loadImage
import com.rhorbachevskyi.viewpager.presentation.utils.ext.log
import com.rhorbachevskyi.viewpager.presentation.utils.ext.showSnackBar
import com.rhorbachevskyi.viewpager.presentation.utils.saveToPrefs
import dagger.hilt.android.AndroidEntryPoint

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
        requireContext().saveToPrefs(Constants.USER_ID_KEY, userData.user.id)
    }

    override fun setListeners() {
        with(binding) {
            buttonViewContacts.setOnClickListener { toContactList() }
            textViewLogout.setOnClickListener { logoutFromAccount() }
            buttonMessageTop.setOnClickListener { toEditProfileScreen() }
            imageViewSendProfile.setOnClickListener { setBluetooth() }
        }
    }

    private fun toContactList() {
        (parentFragment as? ViewPagerFragment)?.openFragment(ViewPagerAdapter.Fragments.CONTACTS.ordinal)
    }

    private fun logoutFromAccount() {

        requireContext().saveToPrefs(Constants.KEY_EMAIL, "")
        requireContext().saveToPrefs(Constants.KEY_PASSWORD, "")

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
        log("setBluetooth()")
        log("${Build.VERSION.SDK_INT}")
        log("${Build.VERSION_CODES.S}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT
                )
            )
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetoothEnable.launch(enableBtIntent)
        } else {
            requireContext().showSnackBar(binding.root, "Ця функція не доступна на вашому девайсі")
            log("else")
        }



    }

    private val requestBluetoothEnable =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) { // TODO: inactive
//                val direction =
//                    ViewPagerFragmentDirections.actionViewPagerFragmentToBluetoothDevicesFragment()
//                navController.navigate(direction)
            }
        }
}