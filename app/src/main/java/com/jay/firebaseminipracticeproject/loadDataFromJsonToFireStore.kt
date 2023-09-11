package com.jay.firebaseminipracticeproject

import android.content.Context
import java.io.IOException
import java.nio.charset.Charset

fun readJsonFromAssets(context: Context, fileName: String): String? {
    val json: String
    try {
        val inputStream = context.assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        json = String(buffer, Charset.defaultCharset())
        return json
    } catch (e: IOException) {
        e.printStackTrace()
        return null
    }
}