package com.example.playlistmaker.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private val gson = Gson()
private val typeToken = object : TypeToken<MutableList<String>>() {}.type

fun String.toTrackIdList(): MutableList<String> =
    if (this.isEmpty()) mutableListOf()
    else gson.fromJson(this, typeToken)

fun List<String>.toJsonString(): String =
    gson.toJson(this)