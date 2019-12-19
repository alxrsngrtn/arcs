/*
 * Copyright 2019 Google LLC.
 *
 * This code may only be used under the BSD style license found at
 * http://polymer.github.io/LICENSE.txt
 *
 * Code distributed by Google as part of this project is also subject to an additional IP rights
 * grant found at
 * http://polymer.github.io/PATENTS.txt
 */

package arcs.tutorials.tictactoe

import arcs.sdk.kotlin.Collection
import arcs.sdk.kotlin.Handle
import arcs.sdk.kotlin.Particle
import arcs.sdk.kotlin.Singleton
import arcs.sdk.kotlin.TTTGame_Events
import arcs.sdk.kotlin.TTTGame_GameState
import arcs.sdk.kotlin.TTTGame_PlayerOne
import arcs.sdk.kotlin.TTTGame_PlayerOneMove
import arcs.sdk.kotlin.TTTGame_PlayerTwo
import arcs.sdk.kotlin.TTTGame_PlayerTwoMove
import arcs.sdk.kotlin.log

class TTTGame : Particle() {
    private val defaultGame = TTTGame_GameState(
        board = ",,,,,,,,",
        currentPlayer = (0..1).random().toDouble(),
        gameOver = false,
        winnerAvatar = ""
    )
    private val defaultPlayerOne = TTTGame_PlayerOne(
        name = "PlayerOne",
        avatar = "",
        id = -1.0
    )
    private val defaultPlayerOneMove = TTTGame_PlayerOneMove(-1.0)
    private val defaultPlayerTwo = TTTGame_PlayerTwo(
        name = "PlayerTwo",
        avatar = "",
        id = -1.0
    )
    private val defaultPlayerTwoMove = TTTGame_PlayerTwoMove(-1.0)

    private val gameState = Singleton(this, "gameState") { defaultGame }
    private val playerOne = Singleton(this, "playerOne") { defaultPlayerOne }
    private val playerOneMove = Singleton(this, "playerOneMove") { defaultPlayerOneMove }
    private val playerTwo = Singleton(this, "playerTwo") { defaultPlayerTwo }
    private val playerTwoMove = Singleton(this, "playerTwoMove") { defaultPlayerOneMove }
    private val events = Collection(this, "events") { TTTGame_Events(
        type = "",
        move = -1.0,
        time = -1.0
    ) }

    private val winningSequences = arrayOf(
        arrayOf(0, 1, 2),
        arrayOf(3, 4, 5),
        arrayOf(6, 7, 8),
        arrayOf(0, 3, 6),
        arrayOf(1, 4, 7),
        arrayOf(2, 5, 8),
        arrayOf(0, 4, 8),
        arrayOf(2, 4, 6)
    )

    override fun onHandleSync(handle: Handle, allSynced: Boolean) {
        if (gameState.get() == null) {
            gameState.set(defaultGame)
        }
        if (handle.name == "playerOne" && playerOne.get()?.id != 0.0) {
            val p1 = playerOne.get() ?: defaultPlayerOne
            p1.id = 0.0
            playerOne.set(p1)
        }
        if (handle.name == "playerTwo" && playerTwo.get()?.id != 1.0) {
            val p2 = playerTwo.get() ?: defaultPlayerTwo
            p2.id = 1.0
            playerTwo.set(p2)
        }
    }

    override fun populateModel(slotName: String, model: Map<String, Any?>): Map<String, Any?> {
        val gs = gameState.get() ?: defaultGame
        val p1 = playerOne.get() ?: defaultPlayerOne
        val p2 = playerTwo.get() ?: defaultPlayerTwo

        val cpName = if (gs.currentPlayer == p1.id) p1.name else p2.name
        val cpAvatar = if (gs.currentPlayer == p1.id) p1.avatar else p2.avatar

        val congratsMessage = gs.winnerAvatar.let {
            if (it == p1.avatar) "Congratulations ${p1.name}, you won!"
            else if (it == p2.avatar) "Congratulations ${p2.name}, you won!"
            else "It's a tie!"
        }

        return mapOf(
            "message" to congratsMessage,
            "hideCongrats" to !gs.gameOver,
            "playerDetails" to "$cpName playing as $cpAvatar"
        )
    }

    override fun onHandleUpdate(handle: Handle) {
        val gs = gameState.get() ?: defaultGame
        val p1 = playerOne.get() ?: defaultPlayerOne
        val p2 = playerTwo.get() ?: defaultPlayerTwo
        val mv1 = playerOneMove.get() ?: defaultPlayerOneMove
        val mv2 = playerTwoMove.get() ?: defaultPlayerTwoMove
        // Apply the moves
        if (gs.gameOver != true) {
            val boardList = gs.board.split(",").toMutableList()
            // Check the handle updated matches the current player
            if (handle.name == "playerOneMove" && gs.currentPlayer == 0.0) {
                applyMove(
                    mv = mv1.move.toInt(),
                    avatar = p1.avatar,
                    boardList = boardList,
                    gs = gs
                )
            } else if (handle.name == "playerTwoMove" && gs.currentPlayer == 1.0) {
                applyMove(
                    mv = mv2.move.toInt(),
                    avatar = p2.avatar,
                    boardList = boardList,
                    gs = gs
                )
            }
        }
        if (hasReset()) {
            log("default game board: ${defaultGame.board}")
            gameState.set(TTTGame_GameState(
                board = ",,,,,,,,",
                currentPlayer = (0..1).random().toDouble(),
                gameOver = false,
                winnerAvatar = ""
            ))
            mv1.move = -1.0
            mv2.move = -1.0
            playerOneMove.set(mv1)
            playerTwoMove.set(mv2)
            events.clear()
        }
        renderOutput()
    }

    override fun getTemplate(slotName: String): String = """
        It is your turn <span>{{playerDetails}}</span>.
        <div slotid="boardSlot"></div>
        <div hidden="{{hideCongrats}}"><span>{{message}}</span></div>
        """.trimIndent()

    private fun applyMove(
        mv: Int,
        avatar: String,
        boardList: MutableList<String>,
        gs: TTTGame_GameState
    ) {
        if (!mv.isValidMove(boardList)) return
        boardList[mv] = avatar
        gs.board = boardList.joinToString(",")

        gs.currentPlayer = (gs.currentPlayer + 1) % 2
        gameState.set(gs)

        gameState.set(checkGameOver(boardList, gs, avatar))
    }

    private fun checkGameOver(
        boardList: List<String>,
        gs: TTTGame_GameState,
        avatar: String
    ): TTTGame_GameState {
        // Check if the board is full, meaning the game is tied
        gs.gameOver = !boardList.contains("")

        // Check if the game is over
        winningSequences.forEach { sequence ->
            if (boardList[sequence[0]] != "" &&
                boardList[sequence[0]] == boardList[sequence[1]] &&
                boardList[sequence[0]] == boardList[sequence[2]]) {
                gs.gameOver = true
                gs.winnerAvatar = avatar
            }
        }
        return gs
    }

    private fun hasReset() = events.any { it.type == "reset" }

    private fun Int.isValidMove(boardList: List<String>) = this in 0..9 && boardList[this] == ""
}
