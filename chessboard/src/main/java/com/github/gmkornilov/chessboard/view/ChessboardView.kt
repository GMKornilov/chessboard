package com.github.gmkornilov.chessboard.view

import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.gmkornilov.chessboard.R
import com.github.gmkornilov.chessboard.model.Board
import com.github.gmkornilov.chessboard.model.CellInfo
import com.github.gmkornilov.chessboard.model.MoveNotFoundException
import com.github.gmkornilov.chessboard.model.moves.Move
import com.github.gmkornilov.chessboard.model.moves.PromotionMove
import com.github.gmkornilov.chessboard.model.pieces.Piece
import com.github.gmkornilov.chessboard.view.piece_pick.PiecePickAdapter
import kotlin.math.min

class ChessboardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    interface OnMoveListener {
        fun onMove(move: String)
    }

    interface OnFenChangedListener {
        fun onFenChanged(newFen: String)
    }

    private val board: Board by lazy {
        Board(allowOpponentMoves)
    }

    private var onMoveListener: OnMoveListener? = null
    private var onFenChangedListener: OnFenChangedListener? = null

    private var availableMoves: List<Move>? = null

    private var draggedPiece: Piece? = null
    private var draggedPieceMoved = false
    private var dragX = -1f
    private var dragY = -1f

    private var clickedOnce = false

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

    private val captureRadius: Float
        get() = cellSize / 2.15f
    private val captureStrokeWidth: Float
        get() = cellSize / 15

    private val moveRadius: Float
        get() = cellSize / 6

    private val normalPieceSize: Float
        get() = 6 * cellSize / 7
    private val bigPieceSize: Float
        get() = 1.5f * cellSize

    private val textSize: Float
        get() = cellSize / 5


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.ChessboardView,
            0, 0
        ).apply {
            try {
                isWhite = getBoolean(R.styleable.ChessboardView_is_white, true)
                allowOpponentMoves =
                    getBoolean(R.styleable.ChessboardView_allow_opponent_moves, true)
            } finally {
                recycle()
            }
        }
    }

    var fen: String?
        get() = getFEN()
        set(value) {
            value ?: return
            setFEN(value)
            onFenChangedListener?.onFenChanged(value)
        }

    var lastMove: String?
        get() {
            val notation = board.lastMoveNotation
            if (notation.isEmpty()) {
                return null
            }
            return notation
        }
        set(value) {
            value ?: return
            val move = board.getMoveByNotation(value)
                ?: throw MoveNotFoundException("can't find legal move with following notation: $value")
            doMove(move)
        }

    fun setOnMoveListener(listener: OnMoveListener) {
        onMoveListener = listener
    }

    fun setOnFenChangedListener(listener: OnFenChangedListener) {
        onFenChangedListener = listener
        listener.onFenChanged(getFEN())
    }

    fun undo() {
        board.undo(isWhite)
        onFenChangedListener?.onFenChanged(getFEN())
        invalidate()
    }

    private fun getFEN(): String {
        return board.toFen()
    }

    private fun setFEN(fen: String) {
        availableMoves = null
        board.parseFEN(fen)
        invalidate()
    }

    private fun doMove(move: Move) {
        val notation = move.getMoveNotation(board)
        board.move(move, isWhite)
        onMoveListener?.onMove(notation)
        val fen = board.toFen()
        onFenChangedListener?.onFenChanged(fen)
        availableMoves = null
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

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false

        val col = ((event.x - sideX) / cellSize).toInt()
        val row = ((event.y - sideY) / cellSize).toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val (infoCol, infoRow) = CellInfo.fromAnimationIndexes(row, col, isWhite)
                draggedPiece = board.board[infoRow][infoCol]
                if (!clickedOnce) {
                    availableMoves = board.getMoves(infoRow, infoCol, isWhite)
                }
                dragX = event.x
                dragY = event.y
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                draggedPieceMoved = true
                dragX = event.x
                dragY = event.y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                Log.println(Log.WARN, "chessboard", "HEY")
                if (draggedPiece != null && draggedPieceMoved) {
                    onPieceMoved(row, col)
                    clickedOnce = false
                } else {
                    val moves = availableMoves
                    if (clickedOnce) {
                        clickedOnce = false
                        onCellCLicked(row, col)
                    } else if (moves != null && moves.isNotEmpty()) {
                        clickedOnce = true
                    }
                }
                draggedPiece = null
                draggedPieceMoved = false
                invalidate()
            }
        }
        return true
    }

    private fun onCellCLicked(row: Int, col: Int) {
        val moves = availableMoves
        if (moves != null) {
            onPieceMoved(row, col)
        } else {
            val (infoCol, infoRow) = CellInfo.fromAnimationIndexes(row, col, isWhite)
            availableMoves = board.getMoves(infoRow, infoCol, isWhite)
            invalidate()
        }
    }

    private fun onPieceMoved(toRow: Int, toCol: Int) {
        val moves = availableMoves ?: return
        val cellInfo = CellInfo(toCol, toRow)
        val promotionMoves = moves.filter {
            it.getDisplayedCell(isWhite) == cellInfo && it is PromotionMove
        }.map { it as PromotionMove }
        if (promotionMoves.isNotEmpty()) {
            choosePromotionPiece(promotionMoves)
        } else {
            val move = moves.find { it.getDisplayedCell(isWhite) == CellInfo(toCol, toRow) }
            if (move != null) {
                doMove(move)
            }
            availableMoves = null
            invalidate()
        }
    }

    private fun choosePromotionPiece(pieces: List<PromotionMove>) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_piece_pick)

        val recyclerView = dialog.findViewById<RecyclerView>(R.id.piecesRecyclerView)
        var selectedMove: PromotionMove? = null

        val clickCallback: (PromotionMove) -> Unit = { move ->
            dialog.dismiss()
            selectedMove = move
            doMove(move)
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
        var radius = moveRadius
        if (board.board[boardCellInfo.row][boardCellInfo.col] != null) {
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = captureStrokeWidth
            radius = captureRadius
        }
        val xCenter = sideX + cellInfo.col * cellSize + cellSize / 2
        val yCenter = sideY + cellInfo.row * cellSize + cellSize / 2
        canvas.drawCircle(xCenter, yCenter, radius, paint)
    }

    private fun drawPieces(canvas: Canvas) {
        for (boardRow in board.board) {
            for (piece in boardRow) {
                piece ?: continue
                if (piece != draggedPiece) {
                    drawPiece(canvas, piece)
                }
            }
        }
        if (draggedPiece != null) {
            drawDraggedPiece(canvas)
        }
    }

    private fun drawPiece(canvas: Canvas, piece: Piece) {
        val drawPosition = CellInfo.toAnimationIndexes(piece.position, isWhite)
        val left = sideX + drawPosition.col * cellSize + (cellSize - normalPieceSize) / 2
        val top = sideY + drawPosition.row * cellSize + (cellSize - normalPieceSize) / 2
        drawPieceAt(canvas, piece, left, top, normalPieceSize)
    }

    private fun drawDraggedPiece(canvas: Canvas) {
        val piece = draggedPiece
        piece ?: return
        drawPieceAt(canvas, piece, dragX - bigPieceSize / 2, dragY - bigPieceSize / 2, bigPieceSize)
    }

    private fun drawPieceAt(canvas: Canvas, piece: Piece, x: Float, y: Float, sz: Float) {
        val drawable = piecesBitmaps[piece.drawableRes]
            ?: throw Exception("${piece.drawableRes} ${resources.getResourceEntryName(piece.drawableRes)}\n ${piecesBitmaps}")

        drawable.setBounds(0, 0, sz.toInt(), sz.toInt())

        canvas.translate(x, y)
        drawable.draw(canvas)
        canvas.translate(-x, -y)
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
        paint.textSize = textSize

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
        paint.textSize = textSize

        val str = char.toString()

        val x = sideX + cellSize * (col + 1) - cellSize / 15f
        val y = sideY + cellSize * (row + 1) - cellSize / 15f

        canvas.drawText(str, x, y, paint)
    }
}