[![](https://jitpack.io/v/GMKornilov/chessboard.svg)](https://jitpack.io/#GMKornilov/chessboard)

# Android ChessBoard
Small chessboard widget for Android, which gives you callbacks on game turns.

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

2. Add the dependency:
```
dependencies {
    implementation 'com.github.GMKornilov:chessboard:-SNAPSHOT'
}
```

## Usage:

```
<com.github.gmkornilov.chessboard.view.ChessboardView
        android:id="@+id/chessboardView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:is_white="true"
        app:allow_opponent_moves="true" />
```

## XML parameters
1. is_white - is user playing as white or black
2. allow_opponnt_moves - defines if user can play with himself 

## Provided types
1. ```ChessboardView``` - view itself
2. ```ChessboardView.OnMoveListener``` - interface for listening to game moves. Has only one method: ```onMove(move: String): Unit```(```move``` is provided as Short Algebraic Notation(SAN)).

## Public methods

1. ```getFEN()``` - get current board state in Forsyth–Edwards Notation(FEN)
2. ```setFEN(fen: String)``` - sets current board state in Forsyth–Edwards Notation(FEN).
3. ```setOnMoveListener(listener: ChessboardView.OnMoveListener)``` - sets listener for board moves.

## Some screenshots:

![white najdorf screenshot](https://i.imgur.com/NbUWrpq.jpeg)

![black caro kann advance variation screenshot](https://i.imgur.com/259G3hu.jpeg)

![white scholar's mate screenshot](https://i.imgur.com/od8g5z0.jpeg)