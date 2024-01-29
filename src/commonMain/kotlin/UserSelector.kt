// import korlibs.korge.input.*
// import korlibs.korge.scene.*
// import korlibs.korge.view.*
// import korlibs.korge.view.align.*
// import korlibs.korge.view.onClick

// class UserSelector : Scene() {
//     override suspend fun SContainer.sceneMain() {
//         val listaUsuarios = mvc.Controller().leerUsuarios()
// //        val usuario = listaUsuarios[0]
//         container {
//             listaUsuarios.forEachIndexed { it, user ->
//                 text(user.nombreUsuario, 33).position(0,50 * it).onClick {
//                     sceneContainer.changeTo { GameView(GameModel(user.dineroActual,user.nombreUsuario)) }
//                 }
//             }
//         }.centerOnStage()

//     }
// }
