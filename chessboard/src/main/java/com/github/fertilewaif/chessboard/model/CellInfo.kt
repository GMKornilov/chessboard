package com.github.fertilewaif.chessboard.model

data class CellInfo(var col: Int, var row: Int) {
    fun toIndexes(isWhitePerspective: Boolean): Pair<Int, Int> {
        return if(isWhitePerspective) {
            Pair(row, col)
        } else {
            Pair(7 - row, 7 - col)
        }
    }

    companion object {
        fun fromIndexes(row: Int, col: Int, isWhitePerspective: Boolean): CellInfo {
            return if (isWhitePerspective) {
                CellInfo(col, row)
            } else {
                CellInfo(7 - col, 7 - row)
            }
        }
    }
}