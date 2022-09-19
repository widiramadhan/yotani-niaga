package com.javaindoku.yotaniniaga.view.login

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.javaindoku.yotaniniaga.R
import com.javaindoku.yotaniniaga.base.BaseActivity
import com.javaindoku.yotaniniaga.databinding.ActivityLoginBinding
import com.javaindoku.yotaniniaga.model.entity.DummyEntity
import com.javaindoku.yotaniniaga.model.profile.editprofile.EditProfile
import com.javaindoku.yotaniniaga.service.database.dao.DummyEntityDao
import com.javaindoku.yotaniniaga.service.network.ApiResult
import com.javaindoku.yotaniniaga.utilities.SharedPrefKeys
import com.javaindoku.yotaniniaga.utilities.dialog.CustomDialog
import com.javaindoku.yotaniniaga.utilities.extension.setOnSafeClickListener
import com.javaindoku.yotaniniaga.view.editprofilepetani.EditProfilePetaniActivity
import com.javaindoku.yotaniniaga.view.forgotpassword.ForgotPasswordActivity
import com.javaindoku.yotaniniaga.view.mainkoperasi.MainKoperasiActivity
import com.javaindoku.yotaniniaga.view.register.RegisterActivity
import com.javaindoku.yotaniniaga.viewmodel.LoginViewModel
import com.kotlinpermissions.KotlinPermissions
import dagger.android.AndroidInjection
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


class LoginActivity : BaseActivity() {
    @Inject
    lateinit var viewModel: LoginViewModel

    @Inject
    lateinit var dummyEntityDao: DummyEntityDao

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var locationProvide: FusedLocationProviderClient

    companion object {
        const val REQUEST_CODE_SIGN_IN = 209
        val TAG = LoginActivity::class.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        prefHelper.setStringToShared(SharedPrefKeys.TES_SHARED, "tes keluar")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = viewModel
        checkPermission()
        setupOnClick()
        observeViewModel()
        testDb()
        binding.lblForgotPassword.setOnSafeClickListener {
            startActivity(Intent(applicationContext, ForgotPasswordActivity::class.java))
        }

        binding.lblRegister.setOnSafeClickListener {
            startActivity(Intent(applicationContext, RegisterActivity::class.java))
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        binding.lytGoogle.setOnSafeClickListener {
            signOutThenSignInGoogle()
        }

    }

    private fun getLastLocation() {

        // Todo Location Already on  ... start
        val manager = this@LoginActivity.getSystemService(LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this@LoginActivity)) {
//            Toast.makeText(LoginActivity.this, "Gps already enabled", Toast.LENGTH_SHORT).show();
        }
        if (!hasGPSDevice(this@LoginActivity)) {
            Toast.makeText(this@LoginActivity, "Gps not Supported", Toast.LENGTH_SHORT).show()
        }

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this@LoginActivity)) {
            Log.e("Starbridges", "Gps already enabled")
            Toast.makeText(this@LoginActivity, "Gps not enabled", Toast.LENGTH_SHORT).show()
//            enableLoc()
        } else {
            Log.e("Starbridges", "Gps already enabled")
            //Toast.makeText(LoginActivity.this, "Gps already enabled", Toast.LENGTH_SHORT).show();
        }
    }

    private fun hasGPSDevice(context: Context): Boolean {
        val mgr = context
            .getSystemService(LOCATION_SERVICE) as LocationManager
            ?: return false
        val providers = mgr.allProviders ?: return false
        return providers.contains(LocationManager.GPS_PROVIDER)
    }

    private fun signOutThenSignInGoogle() {
        mGoogleSignInClient.signOut().addOnCompleteListener {
            val intent = mGoogleSignInClient.signInIntent
            startActivityForResult(intent, REQUEST_CODE_SIGN_IN)
        }
    }

    private fun testDb()
    {
        GlobalScope.launch {
            dummyEntityDao.insert(DummyEntity(1, "test"))
        }
    }

    private fun observeViewModel() {
        val loadingDialog = loadingDialog(this)
        viewModel.loginResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                if (it.messageInt == R.string.sorry_invalid_password) {
                    CustomDialog.dialogOneButton(this@LoginActivity,
                        true,
                        getString(R.string.check_again),
                        getString(
                            R.string.sorry_seems_like_your_password_incorrect
                        ),
                        R.drawable.ic_dialog_failed,
                        false,
                        getString(R.string.srOk),
                        {}).show()
                } else if (it.messageInt == R.string.srUserNotFound) {
                    CustomDialog.customDialogTwoButton(
                        this@LoginActivity,
                        true,
                        getString(R.string.phone_number_not_registered),
                        getString(R.string.continue_register_with_this_phone_number),
                        getString(R.string.srYesContinue),
                        getString(R.string.srTxtBtnNegative),
                        {
                            val intent = Intent(applicationContext, RegisterActivity::class.java)
                            intent.putExtra(
                                RegisterActivity.PHONE_NUMBER,
                                binding.txtRegisteredPhoneNo.text.toString()
                            )
                            startActivity(intent)
                        },
                        {},
                        binding.txtRegisteredPhoneNo.text.toString(),
                        true
                    ).show()
                } else {
                    if (!it.message.isNullOrBlank()) {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_SHORT).show()
                    } else if (it.messageInt != null) {
                        Toast.makeText(
                            applicationContext,
                            getString(it.messageInt!!),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                loadingDialog.dismiss()
                prefHelper.setStringToShared(SharedPrefKeys.USER_ID, it.data?.data?.userId.toString())
                prefHelper.setStringToShared(SharedPrefKeys.USER_NAME, it.data?.data?.username!!)
                prefHelper.setStringToShared(SharedPrefKeys.NAME, it.data?.data?.name.toString())
                prefHelper.setStringToShared(SharedPrefKeys.IMAGE_PROFILE, it.data?.data?.imageProfile.toString())
                prefHelper.setStringToShared(SharedPrefKeys.TOKEN, it.data?.data?.token)
                prefHelper.setBooleanToShared(SharedPrefKeys.IS_LOGIN, true)
                prefHelper.setStringToShared(SharedPrefKeys.PETANI_ID, it.data?.data?.detailLogin.petaniId.toString())
                prefHelper.setStringToShared(SharedPrefKeys.KOPERASI_ID, it.data?.data?.detailLogin.koperasiId.toString())
                prefHelper.setStringToShared(SharedPrefKeys.LATITUDE, it.data?.data?.detailLogin.latitude.toString())
                prefHelper.setStringToShared(SharedPrefKeys.LONGITUDE, it.data?.data?.detailLogin.longitude.toString())
                prefHelper.setStringToShared(SharedPrefKeys.TYPE_USER, it.data?.data?.typeUser.toString())

                //  CHECK BORROWER
                viewModel.checkBorrower(binding.txtRegisteredPhoneNo.text.toString())

                /*startActivity(Intent(applicationContext, MainKoperasiActivity::class.java))
                finish()*/
            }
        })

        viewModel.checkBorrowerResult.observe(this, Observer {
            if (it.status == ApiResult.Status.LOADING) {
                loadingDialog.show()
            } else if (it.status == ApiResult.Status.ERROR) {
                loadingDialog.dismiss()
                Toast.makeText(this, getString(it.messageInt!!), Toast.LENGTH_SHORT).show()
            } else {
                loadingDialog.dismiss()
                if (it.data?.isSuccess == true){
                    prefHelper.setStringToShared(SharedPrefKeys.BORROWER_ID, it.data?.data?.borrowerId.toString())
                    startActivity(Intent(applicationContext, MainKoperasiActivity::class.java))
                    finish()
                } else {
                    val typeUser = prefHelper.getStringFromShared(SharedPrefKeys.TYPE_USER).toString()
                    if (typeUser == "PTN"){
                        startActivity(Intent(applicationContext, EditProfilePetaniActivity::class.java))
                        finish()
                    } else {
                        startActivity(Intent(applicationContext, MainKoperasiActivity::class.java))
                        finish()
                    }
                }
            }
        })

    }

    private fun setupOnClick() {
        binding.btnLogin.setOnSafeClickListener {
            if(binding.txtRegisteredPhoneNo.text.toString().isBlank())
            {
                binding.txtRegisteredPhoneNo.background = resources.getDrawable(
                    R.drawable.bg_shape_edittext_error,
                    null
                )
                binding.lblErrorRegisteredPhoneNoLogin.visibility = View.VISIBLE
            }
            else
            {
                binding.txtRegisteredPhoneNo.background = resources.getDrawable(
                    R.drawable.bg_shape_edittext,
                    null
                )
                binding.lblErrorRegisteredPhoneNoLogin.visibility = View.GONE
            }
            if(binding.txtPassword.text.toString().isBlank())
            {
                binding.txtPassword.background = resources.getDrawable(
                    R.drawable.bg_shape_edittext_error,
                    null
                )
                binding.lblErrorPasswordLogin.visibility = View.VISIBLE
            }
            else
            {
                binding.txtPassword.background = resources.getDrawable(
                    R.drawable.bg_shape_edittext,
                    null
                )
                binding.lblErrorPasswordLogin.visibility = View.GONE
            }

            if(binding.txtRegisteredPhoneNo.toString().isNotBlank() && binding.txtPassword.text.toString().isNotBlank())
            {

                actionLogin()
            }

//            startActivity(Intent(applicationContext, MainKoperasiActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CODE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            Log.d(TAG, account.toString())
//            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "signInResult:failed code=" + e.statusCode)
//            updateUI(null)

        }
    }

    private fun actionLogin() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val fcmToken = task.result
            viewModel.login(
                binding.txtRegisteredPhoneNo.text.toString(),
                binding.txtPassword.text.toString(),
                fcmToken.toString()
            )
        })
    }

    private fun checkPermission(){
        KotlinPermissions.with(this)
            .permissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
            )
            .onAccepted { permissions ->
                if (permissions.size == 1) {

                }
            }.onDenied { permissions ->
            }.onForeverDenied { permissions ->
            }.ask()
    }
}