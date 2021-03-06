package com.cheerz.casadelrestau

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import com.cheerz.casadelrestau.network.HttpClient
import com.cheerz.casadelrestau.network.data.MiamzEvent
import com.cheerz.casadelrestau.network.data.MiamzReqEvent
import com.cheerz.casadelrestau.network.data.MiamzReqEventWrapper
import com.cheerz.casadelrestau.places.PlacesRepository
import com.cheerz.casadelrestau.user.UserStorage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.book_place.view.*

class CreateEventView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var placeId: Int = -1

    init {
        inflate(context, R.layout.book_place, this)
        bookButton.setOnClickListener {
            bookButtonClicked()
        }

        closeButton.setOnClickListener { closeView() }
    }

    fun closeView() {
        this.hide()
    }

    override fun onVisibilityChanged(changedView: View?, visibility: Int) {
        if (visibility == View.VISIBLE) {
            showTitle(context)
        }
    }

    private fun showTitle(context: Context) {
        val username = UserStorage.retrieveUser()!!.username
        title.text = context.getString(R.string.hey_nickname, username)
    }

    private fun postNewEvent(context: Context, startAt: String, stopAt: String) {
        val event = MiamzReqEventWrapper(MiamzReqEvent(placeId, startAt, stopAt))
        HttpClient.service.postNewEvent(event)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                onSuccessCreateEvent(it)
            }, {
                toast(context, "Error")
            })
    }

    private fun onSuccessCreateEvent(event: MiamzEvent) {
        PlacesRepository.updatePlace(placeId, event)
        hide()
    }

    fun setPlace(id: Int, placeName: String) {
        placeId = id
        name.text = placeName
    }

    private fun bookButtonClicked() {
        val hour = hour.text.toString()
        val minutes = minutes.text.toString()
        val hour2 = hour2.text.toString()
        val minutes2 = minutes2.text.toString()

        if (hour.toInt() in 24 downTo -1 && minutes.toInt() in 60 downTo -1 && hour2.toInt() in 24 downTo -1 && minutes2.toInt() in 60 downTo -1)
            postNewEvent(context, "$hour:$minutes", "$hour2:$minutes2")
        else
            toast(this.context, "Use proper value for your reservation")
    }
}
