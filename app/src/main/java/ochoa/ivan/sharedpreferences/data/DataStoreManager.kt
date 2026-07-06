package ochoa.ivan.sharedpreferences.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "game_vault_datastore")

class DataStoreManager(private val context: Context) {
    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val CART_IDS = stringPreferencesKey("cart_ids")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[Keys.IS_LOGGED_IN] ?: false
    }

    val cartIds: Flow<List<Int>> = context.dataStore.data.map { preferences ->
        decodeIds(preferences[Keys.CART_IDS].orEmpty())
    }

    suspend fun saveLogin() {
        context.dataStore.edit { it[Keys.IS_LOGGED_IN] = true }
    }

    suspend fun logout() {
        context.dataStore.edit { it[Keys.IS_LOGGED_IN] = false }
    }

    suspend fun addToCart(productId: Int) {
        context.dataStore.edit { preferences ->
            val current = decodeIds(preferences[Keys.CART_IDS].orEmpty())
            preferences[Keys.CART_IDS] = (current + productId).joinToString(",")
        }
    }

    suspend fun removeOneFromCart(productId: Int) {
        context.dataStore.edit { preferences ->
            val current = decodeIds(preferences[Keys.CART_IDS].orEmpty()).toMutableList()
            current.remove(productId)
            preferences[Keys.CART_IDS] = current.joinToString(",")
        }
    }

    suspend fun clearCart() {
        context.dataStore.edit { it.remove(Keys.CART_IDS) }
    }

    private fun decodeIds(value: String): List<Int> =
        value.split(",").mapNotNull(String::toIntOrNull)
}
