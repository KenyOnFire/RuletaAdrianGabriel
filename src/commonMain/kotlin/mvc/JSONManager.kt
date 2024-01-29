// package mvc

// import korlibs.io.file.std.*
// import kotlinx.serialization.json.*


// class JSONManager {
//     suspend fun readUsers(): JsonElement {
//         // Leer el contenido del archivo JSON utilizando la librería KorIO
//         val jsonContent = resourcesVfs["usuarios.json"].readString()

//         // Utilizar la librería de serialización para parsear el JSON
//         return Json.parseToJsonElement(jsonContent)
//     }

// }
