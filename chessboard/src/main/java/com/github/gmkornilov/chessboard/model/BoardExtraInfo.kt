package com.github.gmkornilov.chessboard.model

internal data class BoardExtraInfo(
    val canWhiteCastle: Pair<Boolean, Boolean>,
    val canBlackCastle: Pair<Boolean, Boolean>,
    val canEnPassant: Boolean,
    val enPassantCellInfo: CellInfo,
    val isWhiteTurn: Boolean,
    val fiftyMovesRule: Int,
    val turnNumber: Int) {
}