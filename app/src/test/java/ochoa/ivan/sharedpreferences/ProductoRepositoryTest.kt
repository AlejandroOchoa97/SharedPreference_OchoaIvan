package ochoa.ivan.sharedpreferences

import ochoa.ivan.sharedpreferences.data.ProductoRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductoRepositoryTest {
    @Test
    fun emptySearchReturnsAllProducts() {
        assertEquals(ProductoRepository.PRODUCTOS, ProductoRepository.filtrarPorNombre(""))
    }

    @Test
    fun searchIgnoresCaseAndMatchesPartOfName() {
        val result = ProductoRepository.filtrarPorNombre("neÓn")
        assertEquals(1, result.size)
        assertEquals("Carrera Neón", result.first().nombre)
    }

    @Test
    fun productCanBeFoundById() {
        assertEquals("Isla Pixel", ProductoRepository.obtenerPorId(5)?.nombre)
        assertNull(ProductoRepository.obtenerPorId(999))
        assertTrue(ProductoRepository.PRODUCTOS.isNotEmpty())
    }
}
