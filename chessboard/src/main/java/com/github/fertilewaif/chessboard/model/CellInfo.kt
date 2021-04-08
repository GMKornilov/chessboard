package com.github.fertilewaif.chessboard.model

data class CellInfo(var col: Char, var row: Int) {
    fun toIndexes(isWhitePerspective: Boolean): Pair<Int, Int> {
        var res = Pair(col - 'a', row)
        if (!isWhitePerspective) {
            res = Pair(7 - res.first, 7 - res.second)
        }
        return res
    }
}