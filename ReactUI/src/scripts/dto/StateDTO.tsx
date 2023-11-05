export interface StateDTO {
    internet_cafe_settings: InternetCafeSettings
    servers: Server[]
    system: System
    teams: Team[]
    player_server_data: PlayerServerData[]
    players: Player[]
    whitelist: WhitelistEntry[]
    user: User
    online_players: OnlinePlayer[]
}

export interface WhitelistEntry {
    uuid: string
    username: string
    offline_mode: boolean
}

export interface InternetCafeSettings {
    ggrock: Ggrock
}

export interface Ggrock {
    enabled: boolean
    url?: string
}

export interface Server {
    player_count: number
    name: string
}

export interface System {
    proxy_software_version: string
    motd: string
    proxy_software: string
    total_memory: number
    cores: number
    public_ip: string
    tournament_name: string
    team_size: number
    offline_mode: boolean
    free_memory: number
    scoreboard_url: string
    os_name: string;
    linux_distro?: string;
    dynamic_config_url?: string;
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

export interface Player {
    kills: number
    server?: string
    score: number
    metadata: any
    ping: number
    online: boolean
    team_number: number
    id: number
    uuid: string
    username: string
    team_score: number
}

export interface User {
    permissions: string[]
    username: string
}

export interface OnlinePlayer {
    server: string
    ping: number
    name: string
    uuid: string
}

export function createEmptyState(): StateDTO {
    return {
        internet_cafe_settings: {
            ggrock: {
                enabled: false
            }
        },
        online_players: [],
        player_server_data: [],
        players: [],
        servers: [],
        teams: [],
        system: {
            cores: 0,
            free_memory: 0,
            motd: "",
            offline_mode: false,
            os_name: "",
            proxy_software: "",
            proxy_software_version: "",
            public_ip: "",
            scoreboard_url: "",
            team_size: 0,
            total_memory: 0,
            tournament_name: ""
        },
        user: {
            permissions: [],
            username: ""
        },
        whitelist: []
    }
}