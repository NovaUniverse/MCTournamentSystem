/**
 * Data transfer object for score data
 */
export default interface ScoreDTO {
	teams: TeamScoreEntry[]
	players: PlayerScoreEntry[]
}

export interface TeamScoreEntry {
	server: string
	reason: string
	amount: number
	gained_at: string
	id: number
	team: TeamData
}

export interface TeamData {
	team_number: number
	id: number
}

export interface PlayerScoreEntry {
	server: string
	reason: string
	amount: number
	gained_at: string
	id: number
	player: PlayerData
}

export interface PlayerData {
	id: number
	uuid: string
	username: string
}

export function createEmptyScoreDTO() : ScoreDTO {
	return {
		players: [],
		teams: []
	}
}