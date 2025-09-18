package com.example.playlistmaker.sharing.data.impl

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.interactor.SharingInteractor
import com.example.playlistmaker.sharing.domain.model.EmailData

class SharingInteractorImpl(
    private val context: Context
) : SharingInteractor {

    override fun getShareAppLink(): String =
        context.getString(R.string.link_to_the_course)

    override fun getTermsLink(): String =
        context.getString(R.string.agreememt_yap)

    override fun getSupportEmailData(): EmailData =
        EmailData(
            email = context.getString(R.string.support_email),
            subject = context.getString(R.string.email_subject),
            body = context.getString(R.string.email_body)
        )
}