package ochoa.ivan.sharedpreferences

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ochoa.ivan.sharedpreferences.data.PreferencesManager
import ochoa.ivan.sharedpreferences.data.Producto
import ochoa.ivan.sharedpreferences.data.ProductoRepository
import java.text.NumberFormat

private val Purple = Color(0xFF6C4CE3)
private val Dark = Color(0xFF171429)
private val Background = Color(0xFFF6F4FC)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = PreferencesManager(this)
        setContent {
            MaterialTheme {
                StoreApp(preferences)
            }
        }
    }
}

private enum class Screen { CATALOG, DETAIL, CART }

@Composable
private fun StoreApp(preferences: PreferencesManager) {
    var loggedIn by remember { mutableStateOf(preferences.isLoggedIn()) }

    if (!loggedIn) {
        LoginScreen {
            preferences.saveLogin()
            loggedIn = true
        }
        return
    }

    var screen by rememberSaveable { mutableStateOf(Screen.CATALOG) }
    var selectedId by rememberSaveable { mutableStateOf<Int?>(null) }
    val cartIds = remember { mutableStateListOf<Int>() }

    LaunchedEffect(Unit) {
        cartIds.addAll(preferences.loadCart().filter { ProductoRepository.obtenerPorId(it) != null })
    }

    fun addToCart(product: Producto) {
        cartIds.add(product.id)
        preferences.saveCart(cartIds)
    }

    fun removeOne(productId: Int) {
        cartIds.remove(productId)
        preferences.saveCart(cartIds)
    }

    when (screen) {
        Screen.CATALOG -> CatalogScreen(
            cartCount = cartIds.size,
            onProduct = { selectedId = it.id; screen = Screen.DETAIL },
            onAdd = ::addToCart,
            onCart = { screen = Screen.CART },
            onLogout = { preferences.logout(); loggedIn = false }
        )
        Screen.DETAIL -> ProductoRepository.obtenerPorId(selectedId ?: -1)?.let { product ->
            DetailScreen(product, cartIds.size, ::addToCart) { screen = Screen.CATALOG }
        } ?: run { screen = Screen.CATALOG }
        Screen.CART -> CartScreen(
            cartIds = cartIds,
            onBack = { screen = Screen.CATALOG },
            onRemove = ::removeOne,
            onClear = { cartIds.clear(); preferences.clearCart() }
        )
    }
}

@Composable
private fun LoginScreen(onLogin: () -> Unit) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf("") }

    Surface(color = Background, modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize().padding(28.dp), contentAlignment = Alignment.Center) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("GAMEVAULT", color = Purple, fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Text("Iniciar sesión", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(22.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; error = "" },
                        label = { Text("Correo electrónico") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; error = "" },
                        label = { Text("Contraseña") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (error.isNotEmpty()) {
                        Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 10.dp))
                    }
                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = {
                            if (email == "admin@mail.com" && password == "1234") onLogin()
                            else error = "Credenciales incorrectas. Intenta de nuevo."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Purple),
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Entrar") }
                    Text("Usuario: admin@mail.com  •  Clave: 1234", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CatalogScreen(
    cartCount: Int,
    onProduct: (Producto) -> Unit,
    onAdd: (Producto) -> Unit,
    onCart: () -> Unit,
    onLogout: () -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val products = ProductoRepository.filtrarPorNombre(query)

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("GameVault", fontWeight = FontWeight.Black) },
                actions = {
                    TextButton(onClick = onCart) { Text("Carrito ($cartCount)", color = Color.White) }
                    TextButton(onClick = onLogout) { Text("Salir", color = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Dark, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Buscar videojuegos") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = Purple)) { Text("Buscar") }
            }
            Text("Catálogo", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp))
            if (products.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No se encontraron productos") }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(products, key = { it.id }) { product -> ProductCard(product, onProduct, onAdd) }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(product: Producto, onProduct: (Producto) -> Unit, onAdd: (Producto) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onProduct(product) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(product.imagen),
                contentDescription = product.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(92.dp)
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(product.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(money(product.precio), color = Purple, fontWeight = FontWeight.Black, fontSize = 17.sp)
                Text("Ver detalles", color = Color.Gray, fontSize = 12.sp)
            }
            Button(onClick = { onAdd(product) }, colors = ButtonDefaults.buttonColors(containerColor = Purple)) {
                Text("Agregar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailScreen(product: Producto, cartCount: Int, onAdd: (Producto) -> Unit, onBack: () -> Unit) {
    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Detalle del producto") },
                navigationIcon = { TextButton(onClick = onBack) { Text("← Volver", color = Color.White) } },
                actions = { Text("Carrito: $cartCount", color = Color.White, modifier = Modifier.padding(end = 16.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Dark, titleContentColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(24.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(painterResource(product.imagen), product.nombre, modifier = Modifier.size(260.dp))
            Spacer(Modifier.height(18.dp))
            Text(product.nombre, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text(money(product.precio), color = Purple, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(product.descripcion, fontSize = 17.sp, modifier = Modifier.padding(vertical = 24.dp))
            Button(
                onClick = { onAdd(product) },
                colors = ButtonDefaults.buttonColors(containerColor = Purple),
                modifier = Modifier.fillMaxWidth()
            ) { Text("Agregar al carrito") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartScreen(cartIds: List<Int>, onBack: () -> Unit, onRemove: (Int) -> Unit, onClear: () -> Unit) {
    val grouped = cartIds.groupingBy { it }.eachCount().mapNotNull { (id, quantity) ->
        ProductoRepository.obtenerPorId(id)?.let { it to quantity }
    }
    val total = grouped.sumOf { (product, quantity) -> product.precio * quantity }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Carrito de compras") },
                navigationIcon = { TextButton(onClick = onBack) { Text("← Tienda", color = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Dark, titleContentColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 10.dp, color = Color.White) {
                Column(Modifier.padding(16.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Productos: ${cartIds.size}", fontWeight = FontWeight.Bold)
                        Text("Total: ${money(total)}", color = Purple, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                    if (cartIds.isNotEmpty()) {
                        OutlinedButton(onClick = onClear, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Text("Vaciar carrito")
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (grouped.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Tu carrito está vacío", fontSize = 20.sp, color = Color.Gray)
            }
        } else {
            LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(grouped, key = { it.first.id }) { (product, quantity) ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Image(painterResource(product.imagen), product.nombre, modifier = Modifier.size(72.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(product.nombre, fontWeight = FontWeight.Bold)
                                Text("Cantidad: $quantity")
                                Text(money(product.precio * quantity), color = Purple, fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = { onRemove(product.id) }) { Text("Quitar") }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

private fun money(value: Double): String =
    NumberFormat.getCurrencyInstance(java.util.Locale.forLanguageTag("es-MX")).format(value)
