package xyz.gnarbot.gnar

import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import okhttp3.Request
import org.json.JSONObject
import org.json.JSONTokener
import xyz.gnarbot.gnar.utils.HttpUtils
import xyz.gnarbot.gnar.utils.get
import java.io.File
import java.io.IOException

class Credentials(file: File) {
    val loader: ConfigurationLoader<CommentedConfigurationNode> = HoconConfigurationLoader.builder()
            .setPath(file.toPath()).build()

    val config: CommentedConfigurationNode = loader.load()

    val token: String = config["token"].string.takeIf { !it.isNullOrBlank() } ?: error("Bot token can't be null or blank.")

    val shards = kotlin.run {
        val request = Request.Builder().url("https://discordapp.com/api/gateway/bot")
                .header("Authorization", "Bot " + token)
                .header("Content-Type", "application/json")
                .build()

        try {
            HttpUtils.CLIENT.newCall(request).execute().use { response ->
                val jso = JSONObject(JSONTokener(response.body()!!.byteStream()))
                response.close()
                jso.getInt("shards")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            1
        }
    }

    val dbAddr: String = config["database", "addr"].getString("localhost")
    val dbPort: Int = config["database", "port"].getInt(28015)
    val dbName: String = config["database", "name"].getString("bot")
    val dbUser: String = config["database", "user"].getString("admin")
    val dbPass: String = config["database", "pass"].getString("")

    val abal: String? = config["server counts", "abal"].string
    val carbonitex: String? = config["server counts", "carbonitex"].string
    val discordBots: String? = config["server counts", "discordbots"].string

    val cat: String? = config["cat"].string
    val imgFlip: String? = config["imgflip"].string
    val mashape: String? = config["mashape"].string
}
