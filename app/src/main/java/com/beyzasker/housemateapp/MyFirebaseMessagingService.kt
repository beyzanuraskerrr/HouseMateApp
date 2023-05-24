package com.beyzasker.housemateapp

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Gelen bildirimleri işleyin
        if (remoteMessage.notification != null) {
            val title = remoteMessage.notification?.title
            val body = remoteMessage.notification?.body

            // FCM isteğini gönderin
            sendFCMRequest(title, body)
        }
    }

    private fun sendFCMRequest(title: String?, body: String?) {
        // FCM API URL'sini oluşturun
        val url = "https://fcm.googleapis.com/v1/projects/beyzasker-housemateapp/messages:send"

        // HTTP bağlantısı oluşturun
        val connection = URL(url).openConnection() as HttpURLConnection

        // İstek yöntemini POST olarak ayarlayın
        connection.requestMethod = "POST"

        // İstek başlıklarını ayarlayın
        connection.setRequestProperty("Content-Type", "application/json")
        connection.setRequestProperty("Authorization", "Bearer AIzaSyB2fVsbYupnHXPUZYByGPqTMhU-I-_pA6k")

        // İstek gövdesini oluşturun
        val requestBody = """
    {
        "message": {
            "notification": {
                "title": "$title",
                "body": "$body"
            },
            "topic": "genel_bildirim"
        }
    }
""".trimIndent()


        // İstek gövdesini isteğe ekle
        connection.doOutput = true
        val outputStream = OutputStreamWriter(connection.outputStream)
        outputStream.write(requestBody)
        outputStream.flush()
        outputStream.close()

        // İstek yanıtını alın
        val responseCode = connection.responseCode

        // İstek yanıtını okuyun
        val inputStream = connection.inputStream.bufferedReader()
        val response = StringBuilder()
        var line: String?
        while (inputStream.readLine().also { line = it } != null) {
            response.append(line)
        }
        inputStream.close()

    } }