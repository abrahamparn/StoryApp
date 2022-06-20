package com.example.storyapp.nonui.response

import com.google.gson.annotations.SerializedName

data class OverallResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
