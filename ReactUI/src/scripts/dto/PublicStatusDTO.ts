export interface PublicStatusDTO {
	tournament_name: string;
	active_server: string
	servers: Server[]
	teams: Team[]
	player_server_data: PlayerServerData[]
	players: Player[]
	locked_winner: number
	offline_mode: boolean
	dynamic_config_url: string
	online_players: OnlinePlayer[]
}

export interface Server {
	player_count: number
	name: string
}

export interface Team {
	kills: number
	score: number
	color: Color
	team_number: number
	id: number
	display_name: string
}

export interface Color {
	r: number
	b: number
	g: number
}

export interface PlayerServerData {
	game_enabled: boolean
	server: string
	max_health: number
	in_game: boolean
	metadata: any
	closest_enemy_distance: number
	health: number
	uuid: string
	gamemode: string
	food: number
	username: string
}

export interface Trigger {
	running: boolean
	server: string
	ticks_left: number
	trigger_count: number
	name: string
	flags: string[]
	session_id: string
	description: string
	type: string
}

export interface Player {
	kills: number
	server?: string
	score: number
	ping: number
	online: boolean
	team_number: number
	id: number
	uuid: string
	username: string
	team_score: number
}

export interface OnlinePlayer {
	server: string
	ping: number
	name: string
	uuid: string
}
