import korlibs.image.bitmap.*
import korlibs.image.format.*
import korlibs.io.file.std.*
import korlibs.korge.input.*
import korlibs.korge.view.*
import korlibs.math.geom.*

class Ficha(var num: Int):Container(){
    private lateinit var image:Image
    var numTablero:Int = 0
    var posFichaX:Double = 0.0
    var posFichaY:Double = 0.0

    suspend fun setImage(nameImage: String, virtualDouble: Double, posFichaY: Double) {
        image = Image(resourcesVfs[nameImage].readBitmap()).position(virtualDouble, posFichaY)
        image.size = Size(100, 100)
    }
    fun getImage():Image{
        return image
    }
    fun setPosition(posFichaX: Double, posFichaY: Double){
        image.position(posFichaX, posFichaY)
    }
    fun setDraggable(fichasContainer:Container){
        image.draggableCloseable(selector = image, autoMove = true) { info: DraggableInfo ->
            info.view.pos = info.viewNextXY
            if (info.end){
                val newFicha = image.clone()
                fichasContainer.addChild(newFicha)
//              setPosition(imageX, imageY)
                info.view.pos = info.viewNextXY
            }
        }
    }
}
