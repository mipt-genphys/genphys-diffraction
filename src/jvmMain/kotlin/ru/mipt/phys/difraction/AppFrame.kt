package ru.mipt.phys.difraction

import java.awt.*
import javax.swing.JFrame


class AppFrame(val pixelSize: Int) {
    private val canvas = Canvas()
    private val frame = JFrame()
    private val graphics: Graphics
    private val mListener: AppMouseListener
    //private val interpolator = BlockInterpolator()

    private val gridStep = 5 //changeable
    private val intensionGridStep = 5

    private val intensionMatrixSize: Int = pixelSize / intensionGridStep
    private val gridSize: Int = (pixelSize / gridStep)
    private var gridMatrix: Array<Array<Int>> = Array(gridSize) { Array(gridSize) { 0 } }
    //buttons
    private lateinit var buttonCalc: Button
    private lateinit var buttonClear: Button
    //
    private val diffCalc: DiffractionCalculator = DiffractionCalculator(pixelSize, intensionMatrixSize)

    init {
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(pixelSize, pixelSize)

        canvas.setSize(pixelSize, pixelSize)

        configureButtons()

        frame.add(canvas)
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
        graphics = canvas.graphics

        //add rendering Antialiasing
        val g2 = graphics as Graphics2D
        val rh = RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.setRenderingHints(rh)
        g2.stroke = BasicStroke(3.toFloat())
        //
        mListener = AppMouseListener(graphics, gridMatrix, gridStep)
        canvas.addMouseMotionListener(mListener)
        canvas.addMouseListener(mListener)

    }

    fun startDraw() {
        mListener.activeDrawFlag = true
    }

    private fun configureButtons() {
        buttonCalc = Button("Calc!")
        buttonCalc.size = Dimension(150, 40)
        buttonCalc.setLocation(225, 10)
        buttonCalc.addActionListener {
            mListener.activeDrawFlag = false

            gridMatrix.fillcontour()
            //printGridMatrix()
            drawDiffractionPic(diffCalc.calcDiffraction(gridMatrix, gridSize, gridStep))
            startDraw()
        }
        frame.add(buttonCalc)

        buttonClear = Button("Clean")
        buttonClear.size = Dimension(150, 40)
        buttonClear.setLocation(450, 10)
        buttonClear.addActionListener {
            graphics.clearRect(0, 0, pixelSize, pixelSize)
            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    gridMatrix[r][c] = 0
                }
            }
        }
        frame.add(buttonClear)
    }


    private fun drawDiffractionPic(intensionMatrix: Array<Array<Double>>) {
        var maxValue: Double = Double.MIN_VALUE
        var minValue: Double = Double.MAX_VALUE
        for (i in 0 until intensionMatrixSize) {
            for (j in 0 until intensionMatrixSize) {
                if (maxValue < intensionMatrix[i][j])
                    maxValue = intensionMatrix[i][j]
                if (minValue > intensionMatrix[i][j])
                    minValue = intensionMatrix[i][j]
            }
        }

        for (i in 0 until intensionMatrixSize)
            for (j in 0 until intensionMatrixSize)
                intensionMatrix[i][j] = (intensionMatrix[i][j] / (maxValue - minValue)) * 255

        for (i in 0 until intensionMatrixSize)
            for (j in 0 until intensionMatrixSize) {
                graphics.color = Color(
                    intensionMatrix[i][j].toInt(),
                    intensionMatrix[i][j].toInt(),
                    intensionMatrix[i][j].toInt()
                );
                graphics.fillRect(j * intensionGridStep, i * intensionGridStep, intensionGridStep, intensionGridStep)
            }
    }

}

fun main() {
    val app = AppFrame(pixelSize = 600)
    app.startDraw()
}