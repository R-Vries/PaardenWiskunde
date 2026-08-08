package data

import domain.material.Material
import kotlinx.serialization.json.Json

object MaterialRepository {
    val materials = loadMaterials()

    private fun loadMaterials(): List<Material> = Json.decodeFromString<List<Material>>(
        object {}.javaClass
            .getResourceAsStream("/materials.json")!!
            .bufferedReader()
            .readText()
    )
}