import arcs.sdk.Particle
import arcs.sdk.Singleton
import kotlinx.coroutines.flow.combine

// import ...


// The story for properly namespacing generated schemas should exist in this world.
import StreamingParticle.GameState
import StreamingParticle.Move
import StreamingParticle.Player
import StreamingParticle.Events


class StreamingParticle : Particle() {

    private val gameState = Singleton { GameState() }
    private val move = Singleton { Move() }
    private val playerOne = Singleton { Player() }
    private val playerTwo = Singleton { Player() }
    private val events = Collection { Events() }


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

    private val defaultGame = GameState(
            board = ",,,,,,,,",
            currentPlayer = (0..1).random().toDouble(),
            gameOver = false,
            winnerAvatar = ""
    )

    // Could be inside a main() function, etc.
    init {

        // We can subscribe to single handles for initialization purposes.
        // In this implementation, asFlow() is considered "hot"
        playerOne.asFlow()
                .filter { p -> p != null && p.id != 0.0 }
                .collect { _ ->
                   val newPlayer = Player(id=0.0)
                   playerOne.set(newPlayer)
                }
        playerTwo.asFlow()
                .filter { p -> p != null && p.id != 1.0 }
                .collect { _ ->
                    val newPlayer = Player(id=1.0)
                    playerTwo.set(newPlayer)
                }

        // Flows can be saved as variables and combined / subscribed on later
        val board = gameState.asFlow().map { gs -> gs.board.split(",").toMutableList() }


        // This is a bit iffy, but there might be a way to add multiple subscribers to a flow to
        // address existence vs nullability. I'm happy to yield to the latest thinking on how to
        // handle this.
        gameState.asFlow().combineLatest(events.asFlow())
                .filter { gs, es -> gs == null || es.any { it.type == "reset" } }
                .collect { gameState.set(defaultGame)}
        gameState.asFlow()
                .filter { gs -> !gs.gameOver }
                .collect { gs ->
                   // ...
                }


        combine(gameState.asFlow(), playerOne.asFlow(), playerTwo.asFlow()).collect { gs, p1, p2 ->
            // Internal logic is the same. Here is some refactoring to use pattern matching

            val currentPlayer = when (gs.currentPlayer) {
                p1.name -> Pair(p1.name, p1.avatar)
                p2.name -> Pair(p2.name, p2.avatar)
                else -> Pair("unknown", ":/")
            }

            val congratsMessage = gs.winnerAvatar?.let {
                when (it) {
                    p1.avatar -> "Congratulations ${p1.name}, you won!"
                    p2.avatar -> "Congratulations ${p2.name}, you won!"
                    else -> "It's a tie!"
                }
            }

            render(mapOf(
                    "message" to congratsMessage,
                    "hideCongrats" to !gs.gameOver,
                    "playerDetails" to "${currentPlayer.first} playing as ${currentPlayer.second}"
            ))


        }


        // We can preprocess the stream so that arbitrary conditions are met before collection.
        //
        // We might encourage, via documentation / education, that the infinite-loop prevention
        // logic should always exist as an independent filter function before collection.
        combine(gameState.asFlow(), move.asFlow())
                .filter { gs, mv -> !gs.gameOver && mv.playerId == gs.currentPlayer }
                .collect { gs, mv ->
                    val boardList = gs.board.split(",").toMutableList()
                    if (!move.isValidMove(boardList)) return

                    val newGs = gs.copy()

                    boardList[mv] = avatar // Not sure where avatar should come from

                    newGs.board = boardList.joinToString(",")

                    newGs.currentPlayer = (gs.currentPlayer + 1) % 2

                    newGs.gameOver = !boardList.contains("")

                    // Check if the game is over
                    winningSequences.forEach { sequence ->
                        if (boardList[sequence[0]] != "" &&
                                boardList[sequence[0]] == boardList[sequence[1]] &&
                                boardList[sequence[0]] == boardList[sequence[2]]) {
                            newGs.gameOver = true
                            newGs.winnerAvatar = avatar
                        }
                    }
                    gameState.set(newGs)
                }

    }
}
