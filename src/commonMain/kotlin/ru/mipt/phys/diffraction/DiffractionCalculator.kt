package ru.mipt.phys.diffraction

import kotlin.math.*


class DiffractionCalculator(
        val pixelSize: Int,
        val intensionMatrixSize: Int,
        private val lambdaWave: Double = 500 * 1e-3, // Микрометры
        private val l0: Double = 5 * 1e5, // Микрометры
        private val pixelLen: Double = 5.0, // Микрометры
        private val intensionMatrixStep: Int = pixelSize / intensionMatrixSize
) {

    private var intensionMatrix: Array<DoubleArray> =
            Array(intensionMatrixSize) { DoubleArray(intensionMatrixSize) }
    //

    fun centerOfMass(holeMatrix: Array<IntArray>, hMsize: Int, cellSize: Int): Pair<Double, Double> {
        var xRelCenter = 0.0
        var yRelCenter = 0.0
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
        return xRelCenter to yRelCenter
    }


    suspend fun calcDiffraction(holeMatrix: Array<IntArray>, hMsize: Int, cellSize: Int): Array<DoubleArray> {

        val (xRelCenter, yRelCenter) = centerOfMass(holeMatrix, hMsize, cellSize)

        for (i in 0 until intensionMatrixSize) {
            for (j in 0 until intensionMatrixSize) {
                var sX = (intensionMatrixStep * j - pixelSize / 2) * pixelLen
                var sY = (intensionMatrixStep * i - pixelSize / 2) * pixelLen
                var sZ = l0
                val R0 = sqrt(sX * sX + sY * sY + sZ * sZ)
                sX /= R0
                sY /= R0
                sZ /= R0
                val alpha = PI * sX / lambdaWave
                val beta = PI * sY / lambdaWave

                var aS = 1.0
                var bS = 1.0
                if (alpha != 0.0)
                    aS = sin(alpha) / alpha
                if (beta != 0.0)
                    bS = sin(beta) / beta

                val directionE = (intensionMatrixStep * pixelLen) * (intensionMatrixStep * pixelLen) * aS * bS

                summingTension(
                        holeMatrix,
                        hMsize,
                        cellSize,
                        sX,
                        sY,
                        directionE,
                        intensionMatrixSize - j - 1,
                        i,
                        xRelCenter,
                        yRelCenter
                )
            }
        }

        return intensionMatrix
    }

    private fun summingTension(
            holeMatrix: Array<IntArray>,
            hMsize: Int,
            cellSize: Int,
            sX: Double,
            sY: Double,
            directionE: Double,
            iPos: Int,
            jPos: Int,
            xRelCenter: Double,
            yRelCenter: Double
    ) {
        var e: Double = 0.0
        for (i in 0 until hMsize) {
            for (j in 0 until hMsize) {
                if (holeMatrix[i][j] != 0) {
                    val xCenter = (i * cellSize - xRelCenter) * pixelLen
                    val yCenter = (j * cellSize - yRelCenter) * pixelLen
                    e += directionE * cos((xCenter * sX + yCenter * sY) * 2 * PI / lambdaWave)
                }
            }
        }
        intensionMatrix[jPos][iPos] = abs(e)
    }
}