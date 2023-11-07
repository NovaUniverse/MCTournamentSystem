export enum Events {
	STATE_UPDATE = "sys:state_update",
	SERVER_UPDATE = "sys:server_update",
	STAFF_CHANGED = "sys:staff_changed",
	CRASH = "sys:crash",
	DISCONNECTED = "sys:disconnected",
	THEME_CHANGED = "ui:theme_change",
	LOGIN_STATE_CHANGED = "auth:login_state_changed",
	FORCE_STATE_UPDATE = "state:force_update",
	FORCE_SCORE_UPDATE = "score:force_update",
}