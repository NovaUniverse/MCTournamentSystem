import React, { useEffect, useState } from 'react'
import PageSelection from '../components/nav/PageSelection'
import { Col, Container, Row } from 'react-bootstrap'
import StaffTable from '../components/tables/staff/StaffTable'
import StaffDTO, { createEmptyStaffDTO } from '../scripts/dto/StaffDTO';
import { useTournamentSystemContext } from '../context/TournamentSystemContext';
import toast from 'react-hot-toast';
import { Events } from '../scripts/enum/Events';

export default function Staff() {
	const tournamentSystem = useTournamentSystemContext();

	const [staff, setStaff] = useState<StaffDTO>(createEmptyStaffDTO());

	useEffect(() => {
		const interval = setInterval(() => {
			update();
		}, 3000);

		const handleStaffUpdate = () => {
			update();
		}

		update();

		tournamentSystem.events.on(Events.STATE_UPDATE, handleStaffUpdate);

		return () => {
			tournamentSystem.events.off(Events.STATE_UPDATE, handleStaffUpdate);
			clearInterval(interval);
		}
	}, []);

	async function update() {
		try {
			const data = await tournamentSystem.api.getStaffList();

			data.staff.sort((a, b) => {
				return a.username.localeCompare(b.username);
			});

			data.staff_roles.sort((a, b) => {
				return a.localeCompare(b);
			});

			setStaff(data);
		} catch (err) {
			console.error("Failed to update staff list");
			console.error(err);
			toast.error("Failed to update staff list");
		}
	}

	return (
		<>
			<PageSelection />

			<Container fluid>
				<Row>
					<Col>
						<StaffTable staff={staff} />
					</Col>
				</Row>
			</Container>
		</>
	)
}
