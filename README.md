[![](https://jitpack.io/v/GMKornilov/chessboard.svg)](https://jitpack.io/#GMKornilov/chessboard)

# Android ChessBoard
Small chessboard widget for Android, which gives you callbacks on game turns,checks, checkmates and stalemates. Also allows to set board positions and undo game moves.

(My attempts to recreate this package for Flutter: <https://pub.dev/packages/flutter_chess_board>).

## Installation

1. Add the JitPack repository to your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2. Add the dependency to your module builg.gradle:
```
dependencies {
    implementation 'com.github.GMKornilov:chessboard:v1.0.1'
}
```

## Usage

```
<com.github.gmkornilov.chessboard.view.ChessboardView
        android:id="@+id/chessboardView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:is_white="true"
        app:allow_opponent_moves="true" />
```

## XML parameters
1. ```is_white``` - is user playing as white or black.
2. ```allow_opponent_moves``` - defines if user can play with himself.

## Provided types
1. ```ChessboardView``` - view itself.
2. ```ChessboardView.BoardListener``` - listener for main board events. Contains following methods:
    - ```onMove(move: String): Unit```(```move``` is provided as Short Algebraic Notation(SAN))
    - ```onFenChanged(newFen: String): Unit```
    - ```onUndo(): Unit```
    - ```onCheck(isWhiteChecked: Boolean): Unit```
    - ```onCheckmate(whiteLost: Boolean): Unit```
    - ```onStalemate(): Unit```


## Public properties

1. ```fen: String?``` - current board state in FEN. Getter returns current state of board. Setter sets board state and calls ```onFenChanged``` method of current listener.
2. ```lastMove: String?``` - last move played in current position. Getter returns SAN of the last move or null, if no move was played.
Setter take move in SAN as argument and tries to evaluate given move on board.
After evaluation ```onFenChanged``` and ```onMove``` of corresponding listeners are called.

## Public methods

1. ```addBoardListener(listener: ChessboardView.BoardListener)``` - adds listener of board events.
2. ```removeBoardListener(listener: ChessboardView.BoardListener)``` - removes listener of board events.
3. ```undo()``` - reverts last played move, if there is any.

## Some screenshots

![white najdorf screenshot](https://i.imgur.com/NbUWrpq.jpeg)

![black caro kann advance variation screenshot](https://i.imgur.com/259G3hu.jpeg)

![white scholar's mate screenshot](https://i.imgur.com/od8g5z0.jpeg)
