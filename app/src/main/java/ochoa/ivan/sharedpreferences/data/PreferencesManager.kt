package ochoa.ivan.sharedpreferences.data

import android.content.Context

class PreferencesManager(context: Context) {
    private val preferences = context.getSharedPreferences("game_vault_preferences", Context.MODE_PRIVATE)

    fun saveLogin() = preferences.edit().putBoolean("is_logged_in", true).apply()
    fun isLoggedIn(): Boolean = preferences.getBoolean("is_logged_in", false)
    fun logout() = preferences.edit().putBoolean("is_logged_in", false).apply()

    fun saveCart(productIds: List<Int>) =
        preferences.edit().putString("cart_ids", productIds.joinToString(",")).apply()

    fun loadCart(): List<Int> = preferences.getString("cart_ids", "")
        .orEmpty()
        .split(",")
        .mapNotNull(String::toIntOrNull)

    fun clearCart() = preferences.edit().remove("cart_ids").apply()
}
