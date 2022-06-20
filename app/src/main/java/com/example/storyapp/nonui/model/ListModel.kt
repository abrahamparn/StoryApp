package com.example.storyapp.nonui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class ListModel (
    val name: String,
    val description: String,
    val photoUrl: String
    ):Parcelable
