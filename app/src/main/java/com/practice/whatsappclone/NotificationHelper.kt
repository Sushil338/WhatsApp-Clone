package com.practice.whatsappclone

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

object NotificationHelper {

    private const val VERSEL_URL = "https://vercelnotifications-ph8hp5jrg-sushils-projects-d3bdb4d0.vercel.app/send-notification"

    fun sendPushNotification(context: Context, receiverToken: String, title: String, body: String) {
        val json = JSONObject().apply {
            put("token", receiverToken)
            put("title", title)
            put("body", body)
        }

        val request = JsonObjectRequest(
            Request.Method.POST, VERSEL_URL, json,
            { response ->
                println("Notification sent: $response")
            },
            { error ->
                error.printStackTrace()
            }
        )

        Volley.newRequestQueue(context).add(request)
    }
}

