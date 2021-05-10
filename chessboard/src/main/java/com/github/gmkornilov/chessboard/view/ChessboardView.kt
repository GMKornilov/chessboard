package com.github.gmkornilov.chessboard.view

import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.gmkornilov.chessboard.R
import com.github.gmkornilov.chessboard.model.AnimationInfo
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
    interface BoardListener {
        fun onMove(move: String)

        fun onFenChanged(newFen: String)

        fun onUndo()

        fun onCheck(isWhiteChecked: Boolean)

        fun onCheckmate(whiteLost: Boolean)

        fun onStalemate()

        fun onIsWhiteChanged(isWhite: Boolean)

        fun onAllowOpponentMovesChanged(allowOpponentMovesChanged: Boolean)
    }

    private val board: Board

    private var boardListeners = mutableListOf<BoardListener>()

    private var availableMoves: List<Move>? = null

    private var draggedPiece: Piece? = null
    private var draggedPieceMoved = false
    private var dragX = -1f
    private var dragY = -1f

    private var currentAnimations = mutableListOf<AnimationInfo>()

    private var animatedPiece: Piece? = null
    private var animX = -1f
    private var animY = -1f
    private var animDeltaX = -1f
    private var animDeltaY = -1f
    private var animToX = -1f
    private var animToY = -1f
    private var animationsLeft = -1
    private val animationDrawAmounts = 15

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
                _isWhite = getBoolean(R.styleable.ChessboardView_is_white, true)
                _allowOpponentMoves =
                    getBoolean(R.styleable.ChessboardView_allow_opponent_moves, true)
            } finally {
                recycle()
            }
        }
        board = Board(_allowOpponentMoves)
    }

    var fen: String?
        get() = getFEN()
        set(value) {
            value ?: return
            setFEN(value)
            notifyFenChanged(value)
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
            doMove(move, false)
        }

    private var _isWhite = false
    var isWhite: Boolean
        get() = _isWhite
        set(value) {
            _isWhite = value
            notifyIsWhiteChanged()
            invalidate()
        }

    private var _allowOpponentMoves = false
    var allowOpponentMoves: Boolean
        get() = _allowOpponentMoves
        set(value) {
            board.allowOpponentMoves = value
            _allowOpponentMoves = value
            notifyAllowOpponentMovesChanged()
            invalidate()
        }

    fun addBoardListener(listener: BoardListener) {
        boardListeners.add(listener)
    }

    fun removeBoardListener(listener: BoardListener) {
        boardListeners.remove(listener)
    }

    // region notify listeners

    private fun notifyMove(move: String) {
        boardListeners.forEach { it.onMove(move) }
    }

    private fun notifyFenChanged(newFen: String) {
        boardListeners.forEach { it.onFenChanged(newFen) }
    }

    private fun notifyUndo() {
        boardListeners.forEach { it.onUndo() }
    }

    private fun notifyCheck(isWhiteChecked: Boolean) {
        boardListeners.forEach { it.onCheck(isWhiteChecked) }
    }

    private fun notifyCheckmate(whiteLost: Boolean) {
        boardListeners.forEach { it.onCheckmate(whiteLost) }
    }

    private fun notifyStalemate() {
        boardListeners.forEach { it.onStalemate() }
    }

    private fun notifyIsWhiteChanged() {
        boardListeners.forEach { it.onIsWhiteChanged(isWhite) }
    }

    private fun notifyAllowOpponentMovesChanged() {
        boardListeners.forEach { it.onAllowOpponentMovesChanged(allowOpponentMoves) }
    }
    // endregion

    private fun redraw() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            invalidate()
        } else {
            postInvalidate()
        }
    }

    fun undo() {
        if (lastMove == null) {
            return
        }
        currentAnimations.addAll(board.undo(isWhite))
        notifyFenChanged(getFEN())
        notifyUndo()
        startAnimation()
        invalidate()
    }

    private fun getFEN(): String {
        return board.toFen()
    }

    private fun setFEN(fen: String) {
        availableMoves = null
        board.parseFEN(fen)
        redraw()
    }

    private fun doMove(move: Move, isFromDrag: Boolean) {
        var notation = move.getMoveNotation(board)
        currentAnimations.addAll(board.move(move, isWhite))
        if (isFromDrag) {
            currentAnimations.removeFirst()
        }

        val fen = board.toFen()
        notifyFenChanged(fen)
        availableMoves = null
        startAnimation()
        invalidate()

        val isCheck = board.isCheck
        if (board.isCheck) {
            notation += "+"
        }
        val isCheckmate = board.isCheckmate
        if (board.isCheckmate) {
            notation += "#"
        }
        val isStalemate = board.isStalemate
        notifyMove(notation)
        if (isCheck) {
            notifyCheck(board.isWhiteTurn)
        }
        if (isCheckmate) {
            notifyCheckmate(board.isWhiteTurn)
        }
        if (isStalemate) {
            notifyStalemate()
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

        val piece = animatedPiece
        if (piece != null) {
            drawAnimatedPiece(canvas, piece)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        if (animatedPiece != null) {
            return true
        }

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
                    onPieceMoved(row, col, true)
                    clickedOnce = false
                } else {
                    val moves = availableMoves
                    if (clickedOnce) {
                        clickedOnce = false
                        onCellClicked(row, col)
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

    private fun startAnimation() {
        if (currentAnimations.isEmpty()) {
            return
        }
        val (piece, from, to) = currentAnimations[0]
        if (from == null || to == null) {
            currentAnimations.removeFirst()
            startAnimation()
            return
        }
        animationsLeft = animationDrawAmounts
        animatedPiece = piece
        animX = sideX + cellSize * from.col + (cellSize - normalPieceSize) / 2
        animY = sideY + cellSize * from.row + (cellSize - normalPieceSize) / 2

        animToX = sideX + cellSize * to.col + (cellSize - normalPieceSize) / 2
        animToY = sideY + cellSize * to.row + (cellSize - normalPieceSize) / 2

        animDeltaX = (animToX - animX) / animationDrawAmounts
        animDeltaY = (animToY - animY) / animationDrawAmounts

        invalidate()
    }

    private fun onCellClicked(row: Int, col: Int) {
        val moves = availableMoves
        if (moves != null) {
            onPieceMoved(row, col, false)
        } else {
            val (infoCol, infoRow) = CellInfo.fromAnimationIndexes(row, col, isWhite)
            availableMoves = board.getMoves(infoRow, infoCol, isWhite)
            invalidate()
        }
    }

    private fun onPieceMoved(toRow: Int, toCol: Int, isFromDrag: Boolean) {
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
                doMove(move, isFromDrag)
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
            doMove(move, false)
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

    // region draw logic

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
                val animInfo = currentAnimations.find { it.piece == piece }
                if (animInfo != null && piece != animatedPiece) {
                    if (animInfo.from != null) {
                        val x =
                            sideX + cellSize * animInfo.from.col + (cellSize - normalPieceSize) / 2
                        val y =
                            sideY + cellSize * animInfo.from.row + (cellSize - normalPieceSize) / 2
                        drawPieceAt(canvas, piece, x, y, normalPieceSize)
                    }
                } else if (piece != draggedPiece && piece != animatedPiece) {
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

    private fun drawAnimatedPiece(canvas: Canvas, piece: Piece) {
        drawPieceAt(canvas, piece, animX, animY, normalPieceSize)
        animX += animDeltaX
        animY += animDeltaY
        animationsLeft--
        if (animationsLeft == 0) {
            currentAnimations.removeFirst()
            animatedPiece = null
            startAnimation()
        }
        invalidate()
    }

    private fun drawDraggedPiece(canvas: Canvas) {
        val piece = draggedPiece
        piece ?: return
        drawPieceAt(canvas, piece, dragX - bigPieceSize / 2, dragY - bigPieceSize / 2, bigPieceSize)
    }

    private fun drawPieceAt(canvas: Canvas, piece: Piece, x: Float, y: Float, sz: Float) {
        val drawable = piecesBitmaps[piece.drawableRes]
            ?: throw Exception("Piece drawable resource not found")

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

    // endregion
}