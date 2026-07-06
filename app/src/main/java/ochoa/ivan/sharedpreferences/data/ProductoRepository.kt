package ochoa.ivan.sharedpreferences.data

import ochoa.ivan.sharedpreferences.R

object ProductoRepository {
    val PRODUCTOS: List<Producto> = listOf(
        Producto(1, "Aventura Galáctica", 899.00, R.drawable.game_galaxy, "Explora planetas desconocidos y salva la galaxia en una aventura épica."),
        Producto(2, "Carrera Neón", 649.50, R.drawable.game_racing, "Compite en circuitos futuristas con vehículos y mejoras personalizables."),
        Producto(3, "Reino de Cristal", 799.00, R.drawable.game_fantasy, "Un RPG de fantasía con magia, criaturas legendarias y decisiones importantes."),
        Producto(4, "Zona Táctica", 579.90, R.drawable.game_tactical, "Estrategia por turnos donde cada movimiento puede cambiar el resultado."),
        Producto(5, "Isla Pixel", 399.00, R.drawable.game_pixel, "Construye, cultiva y conoce personajes en una relajante isla pixel art."),
        Producto(6, "Misterio Nocturno", 529.00, R.drawable.game_mystery, "Investiga pistas y resuelve un caso que nadie más se atreve a tocar.")
    )

    fun filtrarPorNombre(texto: String): List<Producto> =
        if (texto.isBlank()) PRODUCTOS
        else PRODUCTOS.filter { it.nombre.contains(texto.trim(), ignoreCase = true) }

    fun obtenerPorId(id: Int): Producto? = PRODUCTOS.find { it.id == id }
}
