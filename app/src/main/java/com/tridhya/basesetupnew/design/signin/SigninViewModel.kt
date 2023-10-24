package com.tridhya.basesetupnew.design.signin

import android.util.Log
import android.view.View
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tridhya.basesetupnew.BR
import com.tridhya.basesetupnew.R
import com.tridhya.basesetupnew.Service.ApiState
import com.tridhya.basesetupnew.Service.Status
import com.tridhya.basesetupnew.repository.Signin.SigninRepository
import com.tridhya.basesetupnew.request.LoginRequest
import com.tridhya.basesetupnew.utils.Constant
import com.tridhya.basesetupnew.utils.PrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class SigninViewModel @Inject constructor(
    val repository: SigninRepository,
    private val propertyChangeRegistry: PropertyChangeRegistry,
    @Named(Constant.SHARED_COMMON) private val sharedPreferences: PrefUtils
) :ViewModel(),Observable{
    val parentView: ObservableField<View> = ObservableField()
    private val _signinUserResponse: MutableLiveData<ApiState> = MutableLiveData()
    val signinUserResponse: LiveData<ApiState>
        get() {
            return _signinUserResponse
        }


    private val email: ObservableField<String> = ObservableField("")
    private val password: ObservableField<String> = ObservableField("")

    @Bindable
    fun getEmailXml(): String {
        return email.get().toString()
    }

    fun setEmailXml(mail: String) {
        email.set(mail)
        propertyChangeRegistry.notifyChange(this, BR.emailXml)
    }

    @Bindable
    fun getPasswordXml(): String {
        return password.get().toString()
    }

    fun setPasswordXml(pwd: String) {
        password.set(pwd)
        propertyChangeRegistry.notifyChange(this, BR.passwordXml)
    }
    fun loginUser() {
        if (getEmailXml().isEmpty()) {
            _signinUserResponse.postValue(
                ApiState(
                    Status.ERROR, parentView.get()!!.context.getString(
                        R.string.pleaseEnterUsername
                    )
                )
            )
            return
        }
        if (getPasswordXml().isEmpty()) {
            _signinUserResponse.postValue(
                ApiState(
                    Status.ERROR, parentView.get()!!.context.getString(
                        R.string.pleaseEnterPincode
                    )
                )
            )
            return
        }

        Constant.showProgress(parentView.get()!!.context)
        val loginRequest: LoginRequest = LoginRequest(getEmailXml(), getPasswordXml())

        viewModelScope.launch {
            val response = repository.userSignin(
                parent = parentView.get(),
                loginRequest = loginRequest,
                username = getEmailXml(),
                password = getPasswordXml(),
                isSuccessMessageShow = false,
                isFailureMessageShow = true
            )
            launch {
                Log.d("TAG", "getApiStateResponseStatus: "+"inside viemodel"+response.response.toString())
                Constant.dismissProgress(parentView.get()!!.context)
                _signinUserResponse.postValue(response)
            }
        }
    }
    fun navigateToRegister(){

    }
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        propertyChangeRegistry.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        propertyChangeRegistry.remove(callback)
    }
}