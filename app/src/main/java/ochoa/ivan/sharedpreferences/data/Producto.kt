package ochoa.ivan.sharedpreferences.data

import androidx.annotation.DrawableRes

data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double,
    @param:DrawableRes val imagen: Int,
    val descripcion: String
)
