package com.javaindoku.yotaniniaga.view.mainkoperasi

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityMainBinding
import com.javaindoku.yotaniniaga.utilities.ConstValue
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.view.delivery.DeliveryFragment
import com.javaindoku.yotaniniaga.view.mainkoperasi.fragment.HomeKoperasiFragment
import com.javaindoku.yotaniniaga.view.profile.ProfileFragment
import com.javaindoku.yotaniniaga.view.profilePetani.ProfilePetaniFragment
import com.javaindoku.yotaniniaga.view.transaction.fragment.TransactionFragment
import com.javaindoku.yotaniniaga.view.transactionfarmer.fragment.TransactionFarmerFragment
import com.javaindoku.yotaniniaga.viewmodel.MainViewModel
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainKoperasiActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var viewModel: MainViewModel

    private lateinit var navControllerHome: NavController
    private lateinit var navControllerProfile: NavController
    private lateinit var navControllerNotification: NavController
    private lateinit var navControllerDelivery: NavController
    private lateinit var navControllerTransaction: NavController
    private var typeUser: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.viewModel = viewModel
        var notifPosition = 2

        typeUser = prefHelper.getStringFromShared(SharedPrefKeys.TYPE_USER)
        if (typeUser == ConstValue.KOPERASI) {
            binding.navView.inflateMenu(R.menu.bottom_nav_menu_koperasi)
            notifPosition = ConstValue.notificationKoperasi
            findNavController(R.id.navHostHome).setGraph(R.navigation.mobile_navigation_home)
            findNavController(R.id.navHostTransaction).setGraph(R.navigation.mobile_navigation_transaction_petani)
            findNavController(R.id.navHostProfile).setGraph(R.navigation.mobile_navigation_profile)
        } else {
            binding.navView.inflateMenu(R.menu.bottom_nav_menu_petani)
            notifPosition = ConstValue.notificationPetani
            findNavController(R.id.navHostHome).setGraph(R.navigation.mobile_navigation_home_petani)
            findNavController(R.id.navHostTransaction).setGraph(R.navigation.mobile_navigation_transaction_petani)
            findNavController(R.id.navHostProfile).setGraph(R.navigation.mobile_navigation_profile_petani)
        }
        val hostHome: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostHome) as NavHostFragment? ?: return
        val hostProfile: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostProfile) as NavHostFragment?
                ?: return
        val hostNotification: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostNotification) as NavHostFragment?
                ?: return
        val hostDelivery: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostDelivery) as NavHostFragment?
                ?: return
        val hostTransaction: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostTransaction) as NavHostFragment?
                ?: return
//        val hostDelivery
        navControllerHome = hostHome.navController
        navControllerProfile = hostProfile.navController
        navControllerNotification = hostNotification.navController
        navControllerDelivery = hostDelivery.navController
        navControllerTransaction = hostTransaction.navController


        val mbottomNavigationMenuView = binding.navView.getChildAt(0) as BottomNavigationMenuView
        val notificationBadge = LayoutInflater.from(this).inflate(
            R.layout.layout_unread_message,
            mbottomNavigationMenuView, false
        )
        val itemView =
            mbottomNavigationMenuView.getChildAt(notifPosition) as BottomNavigationItemView

        val tvUnreadChats = notificationBadge.findViewById(R.id.lblUnreadNotif) as TextView
        tvUnreadChats.text = "1"//String that you want to show in badge
        itemView.addView(notificationBadge)
        itemView.removeView(notificationBadge)


        binding.navView.setOnNavigationItemSelectedListener { item ->
            val id = item.itemId

            when (id) {
                R.id.navigation_home -> {
                    setAllFragmentGoneWithExceptFragment(binding.fragmentContainer0)
                    (getForegroundFragment(R.id.navHostHome) as HomeKoperasiFragment).onResume()
                }
                R.id.navigation_notifications -> {
                    setAllFragmentGoneWithExceptFragment(binding.fragmentContainer1)
                    getForegroundFragment(R.id.navHostNotification)?.onResume()
                }
                R.id.navigation_profile -> {
                    if (typeUser == ConstValue.KOPERASI) {
                        setAllFragmentGoneWithExceptFragment(binding.fragmentContainer2)
                        (getForegroundFragment(R.id.navHostProfile) as ProfileFragment).onResume()
                    } else {
                        setAllFragmentGoneWithExceptFragment(binding.fragmentContainer2)
                        (getForegroundFragment(R.id.navHostProfile) as ProfilePetaniFragment).onResume()
                    }
                }
                R.id.navigation_delivery -> {
                    setAllFragmentGoneWithExceptFragment(binding.fragmentContainer3)
                    (getForegroundFragment(R.id.navHostDelivery) as DeliveryFragment).onResume()
                }
                R.id.navigation_transaction -> {
                    try {
                        setAllFragmentGoneWithExceptFragment(binding.fragmentContainer4)
                        (getForegroundFragment(R.id.navHostTransaction) as TransactionFarmerFragment).onResume()
                    } catch (e: Exception) {
                        Log.e("Transaction onResume: ", "Fragment inactive")
                    }
                }
            }

            true
        }
    }

    private fun setAllFragmentGoneWithExceptFragment(fragment: View) {
        binding.fragmentContainer0.visibility = View.GONE
        binding.fragmentContainer1.visibility = View.GONE
        binding.fragmentContainer2.visibility = View.GONE
        binding.fragmentContainer3.visibility = View.GONE
        binding.fragmentContainer4.visibility = View.GONE

        fragment.visibility = View.VISIBLE
    }

    override fun onBackPressed() {
        if (binding.navView.selectedItemId == R.id.navigation_home) {
            val isPopBackStack = navControllerHome.popBackStack()
            if (!isPopBackStack)
                super.onBackPressed()
        } else if (binding.navView.selectedItemId == R.id.navigation_profile) {
            val isPopBackStack = navControllerProfile.popBackStack()
            if (!isPopBackStack)
                binding.navView.selectedItemId = R.id.navigation_home
        } else if (binding.navView.selectedItemId == R.id.navigation_delivery) {
            val isPopBackStack = navControllerDelivery.popBackStack()
            if (!isPopBackStack)
                binding.navView.selectedItemId = R.id.navigation_home
        } else if (binding.navView.selectedItemId == R.id.navigation_transaction) {
            val isPopBackStack = navControllerTransaction.popBackStack()
            if (!isPopBackStack)
                binding.navView.selectedItemId = R.id.navigation_home
        } else
            binding.navView.selectedItemId = R.id.navigation_home
    }

    fun getForegroundFragment(fragmentId: Int): Fragment? {
        val navHostFragment = supportFragmentManager.findFragmentById(fragmentId);
        return if (navHostFragment == null) {
            null
        } else {
            navHostFragment.childFragmentManager.fragments[0]
        }
    }
}