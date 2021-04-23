package com.github.gmkornilov.chessboard.model

data class CellInfo(var col: Int, var row: Int) {

    companion object {
        fun fromAnimationIndexes(row: Int, col: Int, isWhitePerspective: Boolean): CellInfo {
            return if (isWhitePerspective) {
                CellInfo(col, 7 - row)
            } else {
                CellInfo(7 - col, row)
            }
        }

        fun toAnimationIndexes(cellInfo: CellInfo, isWhitePerspective: Boolean): CellInfo {
            return if(isWhitePerspective) {
                CellInfo(cellInfo.col, 7 - cellInfo.row)
            } else {
                CellInfo(7 - cellInfo.col, cellInfo.row)
            }
        }
    }

    val notation: String
        get() = ('a' + col) + (row + 1).toString()
}