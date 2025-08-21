package com.example.composeapp.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Button(onClick = {
            scope.launch {
                try {
                    val data = fetchBybitData()
                    copyToClipboard(context, data)
                    Toast.makeText(context, "Список скопирован", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Ошибка получения данных", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Получить данные")
        }
    }
}

private suspend fun fetchBybitData(): String = withContext(Dispatchers.IO) {
    val url = URL("https://api.bybit.com/v5/market/tickers?category=spot")
    val connection = url.openConnection() as HttpURLConnection
    try {
        connection.requestMethod = "GET"
        connection.connect()
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        val json = JSONObject(response)
        val list = json.getJSONObject("result").getJSONArray("list")
        val builder = StringBuilder()
        for (i in 0 until list.length()) {
            val item = list.getJSONObject(i)
            val name = item.getString("symbol")
            val max24h = item.getString("highPrice24h")
            val current = item.getString("lastPrice")
            builder.append("name: ").append(name)
                .append(", max24h: ").append(max24h)
                .append(", curent: ").append(current)
                .append('\n')
        }
        builder.toString()
    } finally {
        connection.disconnect()
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("bybit", text)
    clipboard.setPrimaryClip(clip)
}

