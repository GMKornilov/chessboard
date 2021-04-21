package com.github.gmkornilov.chessboard.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.util.Log.DEBUG
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import com.github.gmkornilov.chessboard.R
import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo
import com.github.gmkornilov.chessboard.model.moves.Move
import com.github.gmkornilov.chessboard.model.pieces.Piece
import kotlin.math.min

class ChessboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val board = Board()

    private var availableMoves: List<Move>? = null

    private val piecesIds = listOf(
        R.drawable.ic_bb,
        R.drawable.ic_bk,
        R.drawable.ic_bn,
        R.drawable.ic_bp,
        R.drawable.ic_bq,
        R.drawable.ic_br,

        R.drawable.ic_wb,
        R.drawable.ic_wk,
        R.drawable.ic_wn,
        R.drawable.ic_wp,
        R.drawable.ic_wq,
        R.drawable.ic_wr
    )
    private val piecesBitmaps = piecesIds.map {
        it to ResourcesCompat.getDrawable(resources, it, null)
    }.toMap()

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ChessboardView,
            0, 0
        ).apply {
            try {
                isWhite = getBoolean(R.styleable.ChessboardView_is_white, true)
            } finally {
                recycle()
            }
        }
    }

    private var isWhite = false

    private val darkColor = Color.parseColor("#fcaf68")
    private val lightColor = Color.parseColor("#914f11")
    private val moveColor = Color.parseColor("#91240A")

    private var sideX = 10f
    private var sideY = 10f
    private var cellSize = 100f

    var fen
        get() = board.toFen()
        set(value) {
            availableMoves = null
            board.parseFEN(value)
            invalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minSpec = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(minSpec, minSpec)
        super.onMeasure(minSpec, minSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val boardSize = min(width, height)
        cellSize = boardSize / 8f
        sideX = (width - boardSize) / 2f
        sideY = (width - boardSize) / 2f

        drawCells(canvas)
        drawPieces(canvas)
        drawMoves(canvas)
    }

    override fun onDragEvent(event: DragEvent?): Boolean {
        if (event == null) {
            return false
        }
        when (event.action) {

        }
        return super.onDragEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }
        //Log.println(Log.DEBUG, "chessboard", "$event.x $event.y ${event.action}")
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val col = ((event.x - sideX) / cellSize).toInt()
                val row = ((event.y - sideY) / cellSize).toInt()
                val cellInfo = CellInfo.fromAnimationIndexes(row, col, isWhite)
                Log.println(DEBUG, "chessboard", "${cellInfo.col} ${cellInfo.row}")
                val piece = board.board[cellInfo.row][cellInfo.col]
                if (piece != null) {
                    availableMoves = piece.getLegalMoves(board)
                    invalidate()
                }
            }
        }
        return true
    }

    private fun drawMoves(canvas: Canvas) {
        availableMoves?.let {
            for (move in it) {
                drawMove(move, canvas)
            }
        }
    }

    private fun drawMove(move: Move, canvas: Canvas) {
        val paint = Paint()
        paint.color = moveColor

        val cellInfo = move.getDisplayedCell(isWhite)
        val boardCellInfo = CellInfo.fromAnimationIndexes(cellInfo.row, cellInfo.col, isWhite)
        var radius = cellSize / 6
        if (board.board[boardCellInfo.row][boardCellInfo.col] != null) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = cellSize / 15
            radius = cellSize / 2.15f
            //paint.alpha = 200
        }
        val xCenter = sideX + cellInfo.col * cellSize + cellSize / 2
        val yCenter = sideY + cellInfo.row * cellSize + cellSize / 2
        canvas.drawCircle(xCenter, yCenter, radius, paint)
    }

    private fun drawPieces(canvas: Canvas) {
        for (boardRow in board.board) {
            for (piece in boardRow) {
                if (piece != null) {
                    drawPiece(canvas, piece)
                }
            }
        }
    }

    private fun drawPiece(canvas: Canvas, piece: Piece) {
        val drawable = piecesBitmaps[piece.drawableRes]
            ?: throw Exception("${piece.drawableRes} ${resources.getResourceEntryName(piece.drawableRes)}\n ${piecesBitmaps}")

        val drawPosition = CellInfo.toAnimationIndexes(piece.position, isWhite)

        val sz = 6 * cellSize / 7
        drawable.setBounds(0, 0, sz.toInt(), sz.toInt())

        val left = sideX + drawPosition.col * cellSize + (cellSize - sz) / 2
        val top = sideY + drawPosition.row * cellSize + (cellSize - sz) / 2

        canvas.translate(left, top)
        drawable.draw(canvas)
        canvas.translate(-left, -top)
    }

    private fun drawCells(canvas: Canvas) {
        for (i in 0 until Board.BOARD_SIZE) {
            for (j in 0 until Board.BOARD_SIZE) {
                drawCell(canvas, i, j)
            }
        }
    }

    private fun drawCell(canvas: Canvas, row: Int, col: Int) {
        val color = if ((row + col) % 2 == 0) darkColor else lightColor
        val paint = Paint()
        paint.color = color
        canvas.drawRect(
            sideX + col * cellSize,
            sideY + row * cellSize,
            sideX + (col + 1) * cellSize,
            sideY + (row + 1) * cellSize,
            paint
        )
    }
}