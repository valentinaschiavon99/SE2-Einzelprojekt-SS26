package at.aau.serg.services

import at.aau.serg.models.GameResult
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicLong

@Service
class GameResultService {

    private val gameResults = mutableListOf<GameResult>()
    private var nextId = AtomicLong(1)

    fun addGameResult(gameResult: GameResult) {
        gameResult.id = nextId.getAndIncrement()
        gameResults.add(gameResult)
    }

    fun getGameResult(id: Long): GameResult? = gameResults.find { it.id == id }

    fun getGameResults(): List<GameResult> = gameResults.toList()

    fun deleteGameResult(id: Long) = gameResults.removeIf { it.id == id }

    /**
     * Task 2.2.1: Sorting logic
     * Returns the leaderboard sorted by:
     * 1. Score descending (higher is better) [cite: 40]
     * 2. Time ascending (faster is better) as a tiebreaker [cite: 40]
     */
    fun getSortedLeaderboard(): List<GameResult> {
        return gameResults.sortedWith(
            compareByDescending<GameResult> { it.score }
                .thenBy { it.timeInSeconds }
        )
    }

    /**
     * Task 2.2.2: Rank-based filtering
     * Returns a slice of the leaderboard: the player at the given rank 
     * plus 3 neighbors above and 3 neighbors below[cite: 46].
     */
    fun getLeaderboardWithRank(rank: Int): List<GameResult> {
        val sortedList = getSortedLeaderboard()

        // Validate if the rank is within the valid range of the list [cite: 47]
        if (rank < 1 || rank > sortedList.size) {
            throw IllegalArgumentException("Rank out of bounds")
        }

        val index = rank - 1 // Convert 1-based rank to 0-based index
        val start = (index - 3).coerceAtLeast(0) // Get up to 3 above [cite: 46]
        val end = (index + 3).coerceAtMost(sortedList.size - 1) // Get up to 3 below [cite: 46]

        // subList 'toIndex' is exclusive, so we add 1
        return sortedList.subList(start, end + 1)
    }
}