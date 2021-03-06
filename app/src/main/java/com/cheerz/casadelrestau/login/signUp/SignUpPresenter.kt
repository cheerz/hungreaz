package com.cheerz.casadelrestau.login.signUp

import com.cheerz.casadelrestau.login.LoginDataChecker
import com.cheerz.casadelrestau.network.data.MiamzSignUp
import com.cheerz.casadelrestau.user.User
import com.cheerz.casadelrestau.user.UserStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class SignUpPresenter(private val view: SignUp.View, private val listener: SignUp.Listener) : SignUp.Presenter {

    private val model = SignUpModel()
    private val disposables = CompositeDisposable()


    override fun onSignInClicked() {
        listener.onSignInClicked()
    }

    override fun onSignUpClicked(email: String, password: String) {
        if (LoginDataChecker.checkData(email, password))
            disposables.add(signUp(email, password))
        else
            if (!LoginDataChecker.isPasswordEnough(password))
                view.passwordTooShort()
            view.signUpNotValid()
    }

    private fun signUp(email: String, password: String): Disposable {
        return model.signUp(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    onSignedUp(it)
                }, {
                    view.signUpNotValid()
                })
    }

    private fun onSignedUp(signUp: MiamzSignUp) {
        UserStorage.storeUser(User(signUp.email, signUp.nickname))
        listener.goToMapView()
    }
}
