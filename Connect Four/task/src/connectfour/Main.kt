package connectfour

class BoardDimensions {
    var rows: Int = 6
    var columns: Int = 7
}

class Player {
    var name: String = ""
    var score: Int = 0
}

fun main() {
    println("Connect Four")
    playConnectFour()
    println("Game over!")
}

//get board dimensions from user
fun getBoardDimensions(): BoardDimensions {
    var boardDimensions: BoardDimensions = BoardDimensions()
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
                    boardDimensions.rows = dimensionList[0].trim().toInt()
                    boardDimensions.columns = dimensionList[1].trim().toInt()

                    if (boardDimensions.rows < 5 || boardDimensions.rows > 9) {
                        println("Board rows should be from 5 to 9")
                        dimensionsSet = false
                    }
                    if (boardDimensions.columns < 5 || boardDimensions.columns > 9) {
                        println("Board columns should be from 5 to 9")
                        dimensionsSet = false
                    }
                }
            }
        }
    }
    return boardDimensions
}

//Get number of games from user
fun getNumberOfGames(): Int {
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
    return numGames
}

//Create the board given the dimensions
fun creatBoard(boardDimensions: BoardDimensions): MutableList<MutableList<Char>> {
    var boardRow = mutableListOf<Char>()
    val board = mutableListOf(boardRow)
    for (i in 1..boardDimensions.columns) {
        boardRow += ' '
    }
    for (j in 1 until boardDimensions.rows) {
        board.add(boardRow.toMutableList())
    }
    return board
}

//Print the board with any moves that have been played
fun printBoard(boardDimensions: BoardDimensions, board: MutableList<MutableList<Char>>) {
    for (i in 1..boardDimensions.columns) {
        print(" $i")
    }
    println()
    for (i in 1..boardDimensions.rows) {
        for (j in 1..boardDimensions.columns) {
            print("║" + board[i-1][j-1])
        }
        println("║")
    }
    print ("╚")
    for (i in 1..boardDimensions.columns-1) {
        print("═╩")
    }
    println("═╝")
}

//Gather user input and play the game
fun playConnectFour() {
    var currGameNum=1
    var firstPlayer: Player = Player()
    var secondPlayer: Player = Player()
    println("First Player's name:")
    firstPlayer.name = readln()
    println("Second Player's name:")
    secondPlayer.name = readln()

    var boardDimensions: BoardDimensions = getBoardDimensions()
    var numGames: Int = getNumberOfGames()

    println("${firstPlayer.name} VS ${secondPlayer.name}")
    println("${boardDimensions.rows} x ${boardDimensions.columns} board")

    while (currGameNum <= numGames) {
        when (numGames) {
            1 -> println("Single game")
            else -> {
                if (currGameNum == 1) println("Total $numGames games")
                println("Game #$currGameNum")
            }
        }

        //start game play
        var isFirstPlayerTurn = when (currGameNum % 2) {
            0 -> false
            1 -> true
            else -> false
        }

        playGame(boardDimensions, isFirstPlayerTurn, firstPlayer, secondPlayer)

        currGameNum++
    }
}

//Play a single game
fun playGame(boardDimensions: BoardDimensions, isFirstPlayerTurn: Boolean, firstPlayer: Player, secondPlayer: Player) {
    var gameIsOver = false
    var isFirstPlayerTurn = isFirstPlayerTurn
    val board = creatBoard(boardDimensions)
    printBoard(boardDimensions, board)

    while (!gameIsOver) {
        when (isFirstPlayerTurn) {
            true -> println("${firstPlayer.name}\'s turn:")
            false -> println("${secondPlayer.name}\'s turn:")
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
                if (columnPlayed > boardDimensions.columns || columnPlayed < 1) {
                    println("The column number is out of range (1 - ${boardDimensions.columns})")
                    continue
                }
                //check if column is full
                if (!board[0][columnPlayed - 1].isWhitespace()) {
                    println("Column $columnPlayed is full")
                    continue
                } else {
                    //find the next row open for that column
                    var openRowNum = -1
                    for (i in boardDimensions.rows - 1 downTo 0) {
                        if (board[i][columnPlayed - 1].isWhitespace()) {
                            openRowNum = i
                            break
                        }
                    }
                    //mark the play on the board
                    board[openRowNum][columnPlayed - 1] = when (isFirstPlayerTurn) {
                        true -> 'o'
                        false -> '*'
                    }

                    printBoard(boardDimensions, board)
                    val gameEval = checkIsGameOver(firstPlayer.name, secondPlayer.name, board)
                    if (gameEval > 0) {
                        when (gameEval) {
                            1 -> firstPlayer.score += 2
                            2 -> secondPlayer.score += 2
                            3 -> {
                                firstPlayer.score++
                                secondPlayer.score++
                            }
                        }
                        gameIsOver = true
                        println("Score")
                        println("${firstPlayer.name}: ${firstPlayer.score} ${secondPlayer.name}: ${secondPlayer.score}")
                    }

                    isFirstPlayerTurn = !isFirstPlayerTurn
                }
            }
        }
    }
}

//Check if the game is won, or a tie
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