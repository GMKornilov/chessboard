package com.github.fertilewaif.chessboard.model

data class CellInfo(var col: Char, var row: Int) {
    fun toIndexes(isWhitePerspective: Boolean): Pair<Int, Int> {
        return if(isWhitePerspective) {
            Pair(row, col - 'a')
        } else {
            Pair(7 - row, 'h' - col - 1)
        }
    }

    companion object {
        fun fromIndexes(row: Int, col: Int, isWhitePerspective: Boolean): CellInfo {
            return if (isWhitePerspective) {
                CellInfo('a' + col, row)
            } else {
                CellInfo('h' - col, 7 - row)
            }
        }
    }
}