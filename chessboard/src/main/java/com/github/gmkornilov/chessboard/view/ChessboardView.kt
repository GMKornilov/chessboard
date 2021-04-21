package com.github.gmkornilov.chessboard.view

import android.app.AlertDialog
import android.app.Dialog
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
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.gmkornilov.chessboard.R
import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo
import com.github.gmkornilov.chessboard.model.moves.Move
import com.github.gmkornilov.chessboard.model.moves.PromotionMove
import com.github.gmkornilov.chessboard.model.pieces.Piece
import com.github.gmkornilov.chessboard.view.piece_pick.PiecePickAdapter
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

    private var isWhite = false
    private var allowOpponentMoves = false

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

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ChessboardView,
            0, 0
        ).apply {
            try {
                isWhite = getBoolean(R.styleable.ChessboardView_is_white, true)
                allowOpponentMoves = getBoolean(R.styleable.ChessboardView_allow_opponent_moves, true)
            } finally {
                recycle()
            }
        }
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
                onCellCLicked(row, col)
            }
        }
        return true
    }

    private fun onCellCLicked(row: Int, col: Int) {
        if (board.isWhiteTurn != isWhite && !allowOpponentMoves) {
            return
        }
        val moves = availableMoves
        if (moves != null) {
            val cellInfo = CellInfo(col, row)
            val promotionMoves = moves.filter {
                it.getDisplayedCell(isWhite) == cellInfo && it is PromotionMove
            }.map { it as PromotionMove }
            if (promotionMoves.isNotEmpty()) {
                choosePromotionPiece(promotionMoves)
            } else {
                val move = moves.find { it.getDisplayedCell(isWhite) == CellInfo(col, row) }
                if (move != null) {
                    board.move(move, isWhite)
                }
                availableMoves = null
                invalidate()
            }
        } else {
            val (infoCol, infoRow) = CellInfo.fromAnimationIndexes(row, col, isWhite)
            availableMoves = board.getMoves(infoRow, infoCol)
            invalidate()
        }
    }

    private fun choosePromotionPiece(pieces: List<PromotionMove>) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_piece_pick)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.piecesRecyclerView)
        var selectedMove: PromotionMove? = null

        val clickCallback: (PromotionMove) -> Unit = {move ->
            dialog.dismiss()
            selectedMove = move
            board.move(move, isWhite)
            availableMoves = null
            invalidate()
        }

        recyclerView.adapter = PiecePickAdapter(pieces, clickCallback)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        dialog.setOnDismissListener {
            if (selectedMove == null) {
                availableMoves = null
                invalidate()
            }
        }

        dialog.show()
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
        val color = if ((row + col) % 2 == 0) lightColor else darkColor

        val (animCol, animRow) = CellInfo.toAnimationIndexes(CellInfo(col, row), isWhite)

        val paint = Paint()
        paint.color = color
        canvas.drawRect(
            sideX + animCol * cellSize,
            sideY + animRow * cellSize,
            sideX + (animCol + 1) * cellSize,
            sideY + (animRow + 1) * cellSize,
            paint
        )
        if (animCol == 0) {
            drawNumber(canvas, row + 1, animRow, animCol)
        }
        if (animRow == 7) {
            drawChar(canvas, 'a' + col, animRow, animCol)
        }
    }

    private fun drawNumber(canvas: Canvas, num: Int, row: Int, col: Int) {
        val paint = Paint()
        paint.color = if ((col + row) % 2 == 0) {
            lightColor
        } else {
            darkColor
        }
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = cellSize / 5f

        val str = num.toString()

        val x = sideX + cellSize * col
        val y = sideY + cellSize * row + paint.textSize

        canvas.drawText(str, x, y, paint)
    }

    private fun drawChar(canvas: Canvas, char: Char, row: Int, col: Int) {
        val paint = Paint()
        paint.color = if ((col + row) % 2 == 0) {
            lightColor
        } else {
            darkColor
        }
        paint.textAlign = Paint.Align.RIGHT
        paint.textSize = cellSize / 5f

        val str = char.toString()

        val x = sideX + cellSize * (col + 1) - cellSize / 15f
        val y = sideY + cellSize * (row + 1) - cellSize / 15f

        canvas.drawText(str, x, y, paint)
    }
}