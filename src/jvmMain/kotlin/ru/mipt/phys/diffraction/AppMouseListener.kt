package ru.mipt.phys.diffraction

import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class AppMouseListener(var g:Graphics, var table: Array<IntArray>, var gridStep: Int) : MouseMotionListener, MouseListener {

    var xp:Int = -1
    var yp:Int = -1
    var firstPressedFlag:Boolean = false
    var activeDrawFlag:Boolean = false

    //создаем контур
    private fun makingCircuit(xn:Int, yn:Int) {
        if(xn - xp != 0) {
            val k:Double = (yn - yp).toDouble() / (xn - xp)
            if(abs(k) <= 1) {
                var xStart = min(xp, xn)
                val xEnd = max(xp, xn)
                val itX = (sqrt(1/sqrt(1 + k*k)) * gridStep).toInt()
                while (xStart < xEnd) {
                    table[(((xStart - xn)*k + yn)/gridStep).toInt()][(xStart/gridStep)] = 1
                    xStart += itX
                }
            }
        }
        if(yn - yp != 0) {
            val k:Double = (xn - xp).toDouble() / (yn - yp)
            if(abs(k) <= 1) {
                var yStart = min(yp, yn)
                val yEnd = max(yp, yn)
                val itY = (sqrt(1/sqrt(1 + k*k)) * gridStep).toInt()
                while (yStart < yEnd) {
                    table[(yStart / gridStep)][(((yStart - yn) * k + xn) / gridStep).toInt()] = 1
                    yStart += itY
                }
            }
        }
    }

    override fun mouseMoved(e: MouseEvent) {
    }

    override fun mouseDragged(e: MouseEvent) {
        if(e.x > 600 || e.y > 600 || e.x < 0 || e.y < 0){
            firstPressedFlag = false
            return
        }
        if (activeDrawFlag) {
            if (firstPressedFlag) {
                g.drawLine(xp, yp, e.x, e.y)
                makingCircuit(e.x, e.y)
            }
            xp = e.x
            yp = e.y
            firstPressedFlag = true
        }
    }

    override fun mouseReleased(p0: MouseEvent?) {
        if(activeDrawFlag)
            firstPressedFlag = false
    }

    override fun mouseEntered(p0: MouseEvent?) {
    }

    override fun mouseClicked(p0: MouseEvent?) {
    }

    override fun mouseExited(p0: MouseEvent?) {
    }

    override fun mousePressed(p0: MouseEvent?) {
    }
}