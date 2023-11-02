package com.techiq.wallpaperwonders.design.register

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
import com.techiq.wallpaperwonders.BR
import com.techiq.wallpaperwonders.R
import com.techiq.wallpaperwonders.service.ApiState
import com.techiq.wallpaperwonders.service.Status
import com.techiq.wallpaperwonders.repository.Register.RegisterRepository
import com.techiq.wallpaperwonders.Model.request.RegisterationRequest
import com.techiq.wallpaperwonders.utils.Constant
import com.techiq.wallpaperwonders.utils.PrefUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class RegisterViewModel @Inject constructor(
    val repository: RegisterRepository,
    private val propertyChangeRegistry: PropertyChangeRegistry,
    @Named(Constant.SHARED_COMMON) private val sharedPreferences: PrefUtils
) : ViewModel(), Observable {
    val parentView: ObservableField<View> = ObservableField()
    private val _registerUserResponse: MutableLiveData<ApiState> = MutableLiveData()
    val registerUserResponse: LiveData<ApiState>
        get() {
            return _registerUserResponse
        }


    private val name: ObservableField<String> = ObservableField("")

    private val email: ObservableField<String> = ObservableField("")
    private val password: ObservableField<String> = ObservableField("")

    @Bindable
    fun getNamelXml(): String {
        return name.get().toString()
    }

    fun setNamelXml(nameuser: String) {
        name.set(nameuser)
        propertyChangeRegistry.notifyChange(this, BR.namelXml)
    }

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

    fun registerUser() {
        if (getEmailXml().isEmpty()) {
            _registerUserResponse.postValue(
                ApiState(
                    Status.ERROR, parentView.get()!!.context.getString(
                        R.string.pleaseEnterUsername
                    )
                )
            )
            return
        }
        if (getPasswordXml().isEmpty()) {
            _registerUserResponse.postValue(
                ApiState(
                    Status.ERROR, parentView.get()!!.context.getString(
                        R.string.pleaseEnterPincode
                    )
                )
            )
            return
        }

        Constant.showProgress(parentView.get()!!.context)
        val registerationRequest: RegisterationRequest = RegisterationRequest(getEmailXml(),getNamelXml(), getPasswordXml())
        viewModelScope.launch {
            val response = repository.userRegister(
                parent = parentView.get(),
                registerationRequest=registerationRequest,
                isSuccessMessageShow = false,
                isFailureMessageShow = true
            )
            launch {
                Log.d("TAG", "getApiStateResponseStatus: "+"inside viemodel"+response.response.toString())
                Constant.dismissProgress(parentView.get()!!.context)
                _registerUserResponse.postValue(response)
            }
        }
    }
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        propertyChangeRegistry.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {
        propertyChangeRegistry.remove(callback)
    }
}