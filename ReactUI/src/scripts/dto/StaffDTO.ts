export default interface StaffDTO {
	staff_roles: string[]
	staff: StaffMember[]
}

export interface StaffMember {
	role: string
	offline_mode: boolean
	uuid: string
	username: string
}

export function createEmptyStaffDTO(): StaffDTO {
	return {
		staff: [],
		staff_roles: []
	}
}