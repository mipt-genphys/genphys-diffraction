package ru.mipt.phys.difraction

import kotlin.math.*

class DiffractionCalculator(val pixelSize: Int, val intensionMatrixSize: Int) {
    private val lambdaWave: Double = 500 * 1e-3 // Микрометры
    private val L0: Double = 5 * 1e5 // Микрометры
    private val pixelLen: Double = 5.0 // Микрометры
    private val intensionMatrixStep: Int = pixelSize/intensionMatrixSize

    private var intensionMatrix: Array<Array<Double>>  = Array(intensionMatrixSize) { Array(intensionMatrixSize) {0.toDouble()} }
    //
    private lateinit var holeMatrix: Array<Array<Int>>
    private var hMsize: Int = -1 // to be changed
    private var cellSize: Int = -1 // to be changed
    //
    private var xRelCenter = 0.0
    private var yRelCenter = 0.0

    fun centerOfMass(holeMatrix: Array<Array<Int>>, hMsize: Int, cellSize: Int) {
        var numOfCells = 0
        for (i in 0 until hMsize) {
            for (j in 0 until hMsize) {
                if (holeMatrix[i][j] > 0) {
                    xRelCenter += i * cellSize
                    yRelCenter += j * cellSize
                    numOfCells++
                }
            }
        }
        xRelCenter /= numOfCells
        yRelCenter /= numOfCells
    }


    fun calcDiffraction(holeMatrix: Array<Array<Int>>, hMsize: Int, cellSize: Int) : Array<Array<Double>>{
        this.holeMatrix = holeMatrix
        this.hMsize = hMsize
        this.cellSize = cellSize

        centerOfMass(holeMatrix, hMsize, cellSize)

        for (i in 0 until intensionMatrixSize) {
            for (j in 0 until intensionMatrixSize) {
                var sX = (intensionMatrixStep * j - pixelSize/2) * pixelLen
                var sY = (intensionMatrixStep * i - pixelSize/2) * pixelLen
                var sZ = L0
                val R0 = sqrt(sX * sX + sY * sY + sZ * sZ)
                sX /= R0
                sY /= R0
                sZ /= R0
                val alpha = PI * sX / lambdaWave
                val beta = PI * sY / lambdaWave

                var aS = 1.0
                var bS = 1.0
                if(alpha != 0.0)
                    aS = sin(alpha) / alpha
                if(beta != 0.0)
                    bS = sin(beta) / beta

                val directionE = (intensionMatrixStep * pixelLen) * (intensionMatrixStep * pixelLen) * aS * bS

                summingTension(sX, sY, directionE, intensionMatrixSize - j - 1, i, xRelCenter, yRelCenter)
            }
        }

        return intensionMatrix
    }

    private fun summingTension(sX: Double, sY: Double, directionE: Double, iPos: Int, jPos: Int, xRelCenter: Double, yRelCenter: Double) {
        var xCenter: Double
        var yCenter: Double
        var E: Double = 0.toDouble()
        for (i in 0 until hMsize) {
            for (j in 0 until hMsize) {
                if (holeMatrix[i][j] != 0) {
                    xCenter = (i * cellSize - xRelCenter) * pixelLen
                    yCenter = (j * cellSize - yRelCenter) * pixelLen
                    E += directionE * cos((xCenter * sX + yCenter * sY) * 2 * PI / lambdaWave)
                }
            }
        }
        intensionMatrix[jPos][iPos] = abs(E)
    }
}