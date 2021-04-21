package com.github.gmkornilov.chessboard.model

import com.github.gmkornilov.chessboard.model.pieces.*
import java.lang.Exception

class Board() {
    companion object {
        const val BOARD_SIZE = 8
    }

    /**
     * cell-centered representation of the board
     * contains null if there is no figure on the cell
     */
    //val board: MutableList<MutableList<Piece?>> = MutableList(BOARD_SIZE) { MutableList(BOARD_SIZE) { null } }
    val board = Array<Array<Piece?>>(BOARD_SIZE) { Array(BOARD_SIZE) { null } }

    val whitePieces = mutableListOf<Piece>()
    val blackPieces = mutableListOf<Piece>()

    var canWhiteCastleShort = true
        internal set
    var canWhiteCastleLong = true
        internal set
    var canBlackCastleShort = true
        internal set
    var canBlackCastleLong = true
        internal set

    var canEnPassant = false
        internal set
    var enPassantCellInfo = CellInfo(0, 0)
        internal set

    var isWhiteTurn = true
        internal set

    var fiftyMovesRule = 0
        internal set

    var turnNumber = 1
        internal set

    var whiteKingPosition = CellInfo(4, 0)
        internal set
    var blackKingPosition = CellInfo(4, 7)
        internal set


    fun addPiece(piece: Piece, to: CellInfo) {
        board[to.row][to.col] = piece
    }

    fun removePiece(from: CellInfo) {
        board[from.row][from.col] = null
    }

    /**
     * checks if given cell is hit by opposite side figures
     */
    fun isHit(pos: CellInfo, isWhite: Boolean): Boolean {
        val oppositePiecesList = if (isWhite) {
            blackPieces
        } else {
            whitePieces
        }
        for (piece in oppositePiecesList) {
            if (piece.canHit(pos, this)) {
                return true
            }
        }
        return false
    }

    fun move(from: CellInfo, to: CellInfo): Boolean {
        TODO("implement me")
    }

    fun parseFEN(fen: String) {
        clear()

        val fieldPieces = fen.substring(0, fen.indexOf(' '))
        val state = fen.substring(fen.indexOf(' ') + 1)

        val lines = fieldPieces.split('/')
        var row = 7
        for (line in lines) {
            var col = 0
            for (char in line) {
                if (char.isDigit()) {
                    col += Character.getNumericValue(char)
                } else {
                    val cellInfo = CellInfo(col, row)
                    val isWhite = char.isUpperCase()
                    val piece = when (char.toLowerCase()) {
                        'p' -> Pawn(isWhite)
                        'q' -> Queen(isWhite)
                        'r' -> Rook(isWhite)
                        'n' -> Knight(isWhite)
                        'b' -> Bishop(isWhite)
                        'k' -> {
                            if (isWhite) {
                                whiteKingPosition = cellInfo
                            } else {
                                blackKingPosition = cellInfo
                            }
                            King(isWhite)
                        }
                        else -> throw FenParseException("no piece with notation $char")
                    }
                    piece.position = cellInfo
                    board[cellInfo.row][cellInfo.col] = piece
                    if (isWhite) {
                        whitePieces.add(piece)
                    } else {
                        blackPieces.add(piece)
                    }
                    col++
                }
            }
            row--
        }
        isWhiteTurn = state[0] == 'w'

        canWhiteCastleLong = false
        canWhiteCastleShort = false
        canBlackCastleLong = false
        canBlackCastleShort = false

        // rest of fen contains information in following format:
        // <white castle info or - > <black castle info or - > <en passant pawn or - >
        // <semiturns count> <turn number>

        if (state.contains('K')) {
            canWhiteCastleShort = true
        }
        if (state.contains('Q')) {
            canWhiteCastleLong = true
        }
        if (state.contains('k')) {
            canBlackCastleShort = true
        }
        if (state.contains('q')) {
            canBlackCastleLong = true
        }

        val extraInfo = state.split(' ')
        if (extraInfo.size >= 3) {
            val enPassantInfo = extraInfo[2]
            if (enPassantInfo != "-") {
                val enPassantCol = enPassantInfo[0] - 'a'
                val enPassantRow = Character.getNumericValue(enPassantInfo[1])
                canEnPassant = true
                enPassantCellInfo = CellInfo(enPassantCol, enPassantRow)
            }
        }
        if (extraInfo.size >= 4) {
            fiftyMovesRule = extraInfo[3].toInt()
        }
        if (extraInfo.size >= 5) {
            turnNumber = extraInfo[4].toInt()
        }
    }

    fun toFen(): String {
        val res = StringBuilder()
        for (i in BOARD_SIZE - 1 downTo 0) {
            var empty = 0
            for (j in 0 until BOARD_SIZE) {
                val piece = board[i][j]
                if (piece == null) {
                    empty++
                } else {
                    empty = 0
                    if (empty != 0) {
                        res.append(empty)
                    }
                    res.append(board[i][j].toString())
                }
            }
            if (empty != 0) {
                res.append(empty)
            }
            if (i != 0) {
                res.append("/")
            }
        }
        res.append(' ')
        if (isWhiteTurn) {
            res.append('w')
        } else {
            res.append('b')
        }
        res.append(' ')

        if (!canWhiteCastleShort && !canWhiteCastleLong && !canBlackCastleShort && !canBlackCastleLong) {
            res.append('-')
        } else {
            if (canBlackCastleShort) {
                res.append('K')
            }
            if (canBlackCastleLong) {
                res.append('Q')
            }
            if (canWhiteCastleShort) {
                res.append('k')
            }
            if (canWhiteCastleLong) {
                res.append('q')
            }
        }
        res.append(' ')

        if (!canEnPassant) {
            res.append('-')
        } else {
            res.append(('a' + enPassantCellInfo.col) + enPassantCellInfo.row.toString())
        }
        res.append(' ')

        res.append(fiftyMovesRule)
        res.append(' ')

        res.append(turnNumber)
        return res.toString()
    }

    fun clear() {
        for (i in board.indices) {
            for (j in board[i].indices) {
                board[i][j] = null
            }
        }
        whitePieces.clear()
        blackPieces.clear()

        canWhiteCastleLong = false
        canBlackCastleLong = false
        canWhiteCastleShort = false
        canBlackCastleShort = false

        canEnPassant = false

        isWhiteTurn = true

        fiftyMovesRule = 0
        turnNumber = 0
    }

    init {
        parseFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
    }
}