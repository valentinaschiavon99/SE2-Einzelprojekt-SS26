package at.aau.serg.services

import at.aau.serg.models.GameResult
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.*

class GameResultServiceTests {

    private lateinit var service: GameResultService

    @BeforeEach
    fun setup() {
        service = GameResultService()
    }

    @Test
    fun test_getGameResults_emptyList() {
        val result = service.getGameResults()

        assertEquals(emptyList<GameResult>(), result)
    }

    @Test
    fun test_addGameResult_getGameResults_containsSingleElement() {
        val gameResult = GameResult(1, "player1", 17, 15.3)

        service.addGameResult(gameResult)
        val res = service.getGameResults()

        assertEquals(1, res.size)
        assertEquals(gameResult, res[0])
    }

    @Test
    fun test_getGameResultById_existingId_returnsObject() {
        val gameResult = GameResult(1, "player1", 17, 15.3)
        service.addGameResult(gameResult)

        val res = service.getGameResult(1)

        assertEquals(gameResult, res)
    }

    @Test
    fun test_getGameResultById_nonexistentId_returnsNull() {
        val gameResult = GameResult(1, "player1", 17, 15.3)
        service.addGameResult(gameResult)

        val res = service.getGameResult(22)

        assertNull(res)
    }

    @Test
    fun test_addGameResult_multipleEntries_correctId() {
        val gameResult1 = GameResult(0, "player1", 17, 15.3)
        val gameResult2 = GameResult(0, "player2", 25, 16.0)

        service.addGameResult(gameResult1)
        service.addGameResult(gameResult2)

        val res = service.getGameResults()

        assertEquals(2, res.size)

        assertEquals(gameResult1, res[0])
        assertEquals(1, res[0].id)

        assertEquals(gameResult2, res[1])
        assertEquals(2, res[1].id)
    }
    @Test
    fun `test getSortedLeaderboard sorting criteria`() {
        // Task 2.2.1: Higher score first, then lower time [cite: 40]
        val p1 = GameResult(0, "A", 100, 50.0)
        val p2 = GameResult(0, "B", 100, 30.0) // Same score, faster
        val p3 = GameResult(0, "C", 50, 10.0)  // Lower score

        service.addGameResult(p1)
        service.addGameResult(p2)
        service.addGameResult(p3)

        val sorted = service.getSortedLeaderboard()

        assertEquals("B", sorted[0].playerName) // Best: Rank 1
        assertEquals("A", sorted[1].playerName) // Second: Rank 2
        assertEquals("C", sorted[2].playerName) // Third: Rank 3
    }
    @Test
    fun `test deleteGameResult`() {
        service.addGameResult(GameResult(0, "DeleteMe", 10, 10.0))
        val deleted = service.deleteGameResult(1)
        assertTrue(deleted)
        assertEquals(0, service.getGameResults().size)
    }
    @Test
    fun `test getLeaderboardWithRank valid rank and neighbors`() {
        // Task 2.2.2: Rank + 3 above + 3 below [cite: 44, 46]
        for (i in 1..10) {
            service.addGameResult(GameResult(0, "P$i", i * 10, i.toDouble()))
        }

        // Total 10 players. Best is P10 (Score 100), Rank 5 is P6 (Score 60)
        val slice = service.getLeaderboardWithRank(5)

        // Neighbors for Rank 5: Ranks 2, 3, 4, [5], 6, 7, 8 (Total 7)
        assertEquals(7, slice.size)
    }
    @Test
    fun `test getLeaderboardWithRank boundaries`() {
        for (i in 1..5) {
            service.addGameResult(GameResult(0, "P$i", i * 10, 10.0))
        }
        // Top boundary: Rank 1 (no players above)
        val topSlice = service.getLeaderboardWithRank(1)
        assertEquals(4, topSlice.size) // P5, P4, P3, P2

        // Bottom boundary: Rank 5 (no players below)
        val bottomSlice = service.getLeaderboardWithRank(5)
        assertEquals(4, bottomSlice.size) // P4, P3, P2, P1
    }
    @Test
    fun `test getLeaderboardWithRank invalid rank throws exception`() {
        service.addGameResult(GameResult(0, "Top", 100, 10.0))

        // Task 2.2.2: Invalid rank results in error [cite: 47]
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            service.getLeaderboardWithRank(0)
        }
        org.junit.jupiter.api.assertThrows<IllegalArgumentException> {
            service.getLeaderboardWithRank(5)
        }
    }
}

