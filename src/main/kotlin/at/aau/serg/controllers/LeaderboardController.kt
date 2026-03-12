package at.aau.serg.controllers

import at.aau.serg.models.GameResult
import at.aau.serg.services.GameResultService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/leaderboard")
class LeaderboardController(
    private val gameResultService: GameResultService
) {

    @GetMapping
    fun getLeaderboard(@RequestParam(required = false) rank: Int?): List<GameResult> {
        return try {
            if (rank == null) {
                // Task 2.2.1: Return the full sorted leaderboard [cite: 40, 45]
                gameResultService.getSortedLeaderboard()
            } else {
                // Task 2.2.2: Return the player at rank plus 3 neighbors above and below [cite: 44, 46]
                gameResultService.getLeaderboardWithRank(rank)
            }
        } catch (e: IllegalArgumentException) {
            // Task 2.2.2: Respond with HTTP 400 if rank is invalid
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, e.message)
        }
    }
}