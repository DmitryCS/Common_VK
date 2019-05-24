package com.example.vk_sdk_v3


import android.content.res.Resources
import android.net.Uri
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException
import java.util.*

public class NetworkUtils{
    companion object {
        private val VK_API_BASE_URL: String = "https://api.vk.com"
        private val VK_USERS_GET: String = "/method/users.get"
        private val PARAM_USER_ID: String = "user_ids"
        private val PARAM_VERSION: String = "v"
        private var ACCESS_TOKEN: String? = "access_token"

        public fun generateURL(userIds: String): URL {
            var builtUri: Uri = Uri.parse(VK_API_BASE_URL + VK_USERS_GET)
                .buildUpon()
                .appendQueryParameter(PARAM_USER_ID, userIds)
                .appendQueryParameter(PARAM_VERSION, "5.8")
                .appendQueryParameter(ACCESS_TOKEN, "fd0a7792fd0a7792fd0a779278fd607b06ffd0afd0a7792a1bdfcd4e63cebb7063c23fc")//Resources.getSystem().getString(R.string.service_token))//getString(R.string.service_token))
                .appendQueryParameter("fields","photo_100")
                .build()
            var url: URL? = null
            url = URL(builtUri.toString())

            return url
        }

        public fun generateURL2(userId: String): URL {
            var builtUri: Uri = Uri.parse("https://api.vk.com/method/friends.get")
                .buildUpon()
                .appendQueryParameter("user_id", userId)
                .appendQueryParameter("v", "5.95")
                .appendQueryParameter("access_token", "fd0a7792fd0a7792fd0a779278fd607b06ffd0afd0a7792a1bdfcd4e63cebb7063c23fc")
                .appendQueryParameter("fields", "photo_100")
                .build()
            var url: URL? = null
            url = URL(builtUri.toString())

            return url
        }
/*       public fun generateURL3(userId: String, userId2: String): URL {
            var builtUri: Uri = Uri.parse("https://oauth.vk.com/authorize")
                .buildUpon()
                .appendQueryParameter("client_id", "6950036")
                .appendQueryParameter("v", "5.95")
                .appendQueryParameter("access_token", "fd0a7792fd0a7792fd0a779278fd607b06ffd0afd0a7792a1bdfcd4e63cebb7063c23fc")
                .appendQueryParameter("fields", "photo_100")
                .build()
            var url: URL? = null
            url = URL(builtUri.toString())

            return url
        }
*/
        public fun generateURL4(userId: String, token: String?): URL {
            var builtUri: Uri = Uri.parse("https://api.vk.com/method/groups.get")
                .buildUpon()
                .appendQueryParameter("user_id", userId)
                .appendQueryParameter("v", "5.95")
                .appendQueryParameter("access_token", token)
                .appendQueryParameter("extended", "1")
                .appendQueryParameter("fields", "photo_100")
                .build()
            var url: URL? = null
            url = URL(builtUri.toString())

            return url
        }

        public fun getResponseFromURL(url: URL): String? {
            var urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            try {
                var inn: InputStream = urlConnection.getInputStream()
                var scanner: Scanner = Scanner(inn)
                scanner.useDelimiter("\\A")
                var hasInput: Boolean = scanner.hasNext()
                if (hasInput) {
                    return scanner.next()
                } else {
                    return null
                }
            } catch (e: UnknownHostException) {
                return null
            }finally {
                urlConnection.disconnect()
            }
        }
        public fun getResponseFromURL2(url: URL): String? {
            var urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            try {
                var inn: InputStream = urlConnection.getInputStream()
                var scanner: Scanner = Scanner(inn)
                scanner.useDelimiter("\\A")
                var hasInput: Boolean = scanner.hasNext()
                if (hasInput) {
                    return scanner.next()
                } else {
                    return null
                }
            } catch (e: UnknownHostException) {
                return null
            }finally {
                urlConnection.disconnect()
            }
        }
    }
}
