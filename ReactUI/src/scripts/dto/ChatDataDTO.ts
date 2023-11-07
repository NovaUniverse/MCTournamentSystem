export default interface ChatDataDTO {
	success: boolean
	messages: ChatMessage[]
}

export interface ChatMessage {
	sent_at: string
	message_id: number
	uuid: string
	content: string
	username: string
}
