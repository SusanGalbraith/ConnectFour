package connectfour

fun main() {
    println("Connect Four")
    println("First Player's name:")
    val firstPlayerName = readln()
    println("Second Player's name:")
    val secondPlayerName = readln()

    //parse board dimensions
    var rows = 6
    var columns = 7
    var dimensionsSet = false
    while (!dimensionsSet) {
        println("Set the board dimensions (Rows x Columns)")
        println("Press Enter for default (6 x 7)")
        val dimensions = readln().lowercase().replace(" ", "").replace("\t", "")
        var dimensionList: List<String> = dimensions.split("x")

        dimensionsSet = true
        if (dimensions.isNotEmpty()) {
            var regex = Regex(".?.?x.?.?")
            if (!regex.matches(dimensions)) {
                println("Invalid input")
                dimensionsSet = false
            }
            else {
                if (dimensionList.size < 2
                        || dimensionList[0].isEmpty()
                        || dimensionList[1].isEmpty()
                        || dimensionList[0].toIntOrNull() == null
                        || dimensionList[1].toIntOrNull() == null) {
                    println("Invalid input")
                    dimensionsSet = false
                }
                else {
                    rows = dimensionList[0].trim().toInt()
                    columns = dimensionList[1].trim().toInt()

                    if (rows < 5 || rows > 9) {
                        println("Board rows should be from 5 to 9")
                        dimensionsSet = false
                    }
                    if (columns < 5 || columns > 9) {
                        println("Board columns should be from 5 to 9")
                        dimensionsSet = false
                    }
                }
            }
        }
    }
    var numGames: Int = -1
    while (numGames == -1) {
        println("Do you want to play single or multiple games?")
        println("For a single game, input 1 or press Enter")
        println("Input a number of games:")
        var inputNumGames = readln()
        if (inputNumGames == "1" || inputNumGames.isEmpty()) {
            numGames = 1
        } else {
            if (inputNumGames.toIntOrNull() == null || inputNumGames.toInt() < 1) {
                println("Invalid input")
                continue
            }
            else {
                numGames = inputNumGames.toInt()
            }
        }
    }

    println("$firstPlayerName VS $secondPlayerName")
    println("$rows x $columns board")
    var currGameNum=1
    var firstPlayerScore = 0
    var secondPlayerScore = 0

    while (currGameNum <= numGames) {
        if (numGames == 1) {
            println("Single game")
        }
        else {
            if (currGameNum == 1) {
                println("Total $numGames games")
            }
            println("Game #$currGameNum")
        }

        //start game play
        var isFirstPlayerTurn = when (currGameNum % 2) {
            0 -> false
            1 -> true
            else -> false
        }
        var gameIsOver = false

        var boardRow = mutableListOf<Char>()
        val board = mutableListOf(boardRow)
        for (i in 1..columns) {
            boardRow += ' '
        }
        for (j in 1..rows - 1) {
            board.add(boardRow.toMutableList())
        }

        //print the board
        printBoard(rows, columns, board)

        while (!gameIsOver) {
            if (isFirstPlayerTurn) {
                println("$firstPlayerName\'s turn:")
            } else {
                println("$secondPlayerName\'s turn:")
            }
            val play = readln()
            if (play == "end") {
                gameIsOver = true
            } else {
                if (play.toIntOrNull() == null) {
                    println("Incorrect column number")
                    continue
                } else {
                    val columnPlayed = play.toInt()
                    if (columnPlayed > columns || columnPlayed < 1) {
                        println("The column number is out of range (1 - $columns)")
                        continue
                    }
                    //check if column is full
                    if (!board[0][columnPlayed - 1].isWhitespace()) {
                        println("Column $columnPlayed is full")
                        continue
                    } else {
                        //find the next row open for that column
                        var openRowNum = -1
                        for (i in rows - 1 downTo 0) {
                            if (board[i][columnPlayed - 1].isWhitespace()) {
                                openRowNum = i
                                break
                            }
                        }
                        //mark the play on the board
                        board[openRowNum][columnPlayed - 1] = when(isFirstPlayerTurn) {
                            true -> 'o'
                            false -> '*'
                        }

                        printBoard(rows, columns, board)
                        val gameEval = checkIsGameOver(firstPlayerName, secondPlayerName, board)
                        if (gameEval > 0) {
                            when (gameEval) {
                                1 -> firstPlayerScore += 2
                                2 -> secondPlayerScore += 2
                                3 -> {
                                    firstPlayerScore++
                                    secondPlayerScore++
                                }
                            }
                            gameIsOver = true
                            println("Score")
                            println("$firstPlayerName: $firstPlayerScore $secondPlayerName: $secondPlayerScore")
                        }

                        isFirstPlayerTurn = !isFirstPlayerTurn
                    }
                }
            }
        }
        currGameNum++
    }

    println("Game over!")
}
//Print the board with any moves that have been played
fun printBoard(rows: Int, columns: Int, board: MutableList<MutableList<Char>>) {
    for (i in 1..columns) {
        print(" $i")
    }
    println()
    for (i in 1..rows) {
        for (j in 1..columns) {
            print("║" + board[i-1][j-1])
        }
        println("║")
    }
    print ("╚")
    for (i in 1..columns-1) {
        print("═╩")
    }
    println("═╝")
}

fun checkIsGameOver(firstPlayerName: String, secondPlayerName: String, board: MutableList<MutableList<Char>>): Int {
    //check horizontals for win
    for (i in 0 until board.size) {
        if (board[i].joinToString(separator = "").contains("oooo")) {
            println("Player $firstPlayerName won")
            return 1
        }
        else if (board[i].joinToString(separator = "").contains("****")){
            println("Player $secondPlayerName won")
            return 2
        }
    }

    //check verticals for win
    var columnStr = ""
    for (i in 0 until board[0].size) {
        columnStr = ""
        for (j in 0 until board.size){
            columnStr += board[j][i]
        }
        if (columnStr.contains("oooo")) {
            println("Player $firstPlayerName won")
            return 1
        }
        else if (columnStr.contains("****")){
            println("Player $secondPlayerName won")
            return 2
        }
    }
    //check diagonals for win
    // loop through every square on the board, build a diagonal str forward (row++, column++), and backward
    // (row++, column--) and check if they contain a winning string pattern
    var diagonalFwdStr = ""
    var diagonalBwdStr = ""
    for (i in 0 until board.size) {
        for (j in 0 until board[0].size) {
            diagonalFwdStr = ""
            diagonalBwdStr = ""

            var k=i
            var l=j
            while (k<board.size && l<board[0].size) {
                diagonalFwdStr += board[k][l]
                k++
                l++
            }

            k=i
            l=j
            while (k<board.size && l>=0) {
                diagonalBwdStr += board[k][l]
                k++
                l--
            }

            if (diagonalFwdStr.contains("oooo") || diagonalBwdStr.contains("oooo")) {
                println("Player $firstPlayerName won")
                return 1
            }
            else if (diagonalFwdStr.contains("****") || diagonalBwdStr.contains("****")){
                println("Player $secondPlayerName won")
                return 2
            }
        }
    }

    //if neither has won, check if board is full and there's a tie
    var boardStr = ""
    for (i in 0 until board.size) {
        boardStr += board[i].joinToString(separator = "")
    }
    if (!boardStr.contains(" ")) {
        println("It is a draw")
        return 3
    }

    return 0
}