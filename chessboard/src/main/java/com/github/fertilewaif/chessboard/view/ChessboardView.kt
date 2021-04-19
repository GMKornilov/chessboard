package com.github.fertilewaif.chessboard.view

import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.github.fertilewaif.chessboard.R
import com.github.fertilewaif.chessboard.model.Board
import com.github.fertilewaif.chessboard.model.CellInfo
import com.github.fertilewaif.chessboard.model.pieces.Piece
import java.lang.Exception
import kotlin.math.min

class ChessboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val board = Board()

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

    }

    private val isWhite = false

    private val darkColor = Color.parseColor("#769656")
    private val lightColor = Color.parseColor("#eeeed2")

    private var sideX = 10f
    private var sideY = 10f
    private var cellSize = 100f

    var fen
        get() = board.toFen()
        set(value) {
            board.parseFEN(value)
            invalidate()
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val minSpec = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(minSpec, minSpec)
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

        val sz = 2 * cellSize / 3
        drawable.setBounds(0, 0, sz.toInt(), sz.toInt())

        val left = sideX + drawPosition.row * cellSize + (cellSize - sz) / 2
        val top = sideY + drawPosition.col * cellSize + (cellSize - sz) / 2

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