package ru.mipt.phys.difraction

import java.awt.Graphics
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class AppMouseListener(var g:Graphics, var table: Array<Array<Int>>, var gridStep: Int) : MouseMotionListener, MouseListener {

    var prevX:Int = -1
    var prevY:Int = -1
    var firstPressedFlag:Boolean = false
    var activeDrawFlag:Boolean = false

    //создаем контур
    private fun makingCircuit(xp:Int, yp:Int, xn:Int, yn:Int) {
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
                    yStart += itY;
                }
            }
        }
    }

    override fun mouseMoved(e: MouseEvent) {
    }

    override fun mouseDragged(e: MouseEvent) {
        if (activeDrawFlag) {
            if (firstPressedFlag) {
                g.drawLine(prevX, prevY, e.x, e.y)
                makingCircuit(prevX, prevY, e.x, e.y)
            }
            prevX = e.x
            prevY = e.y
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