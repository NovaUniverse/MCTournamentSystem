import React, { ChangeEvent, useEffect, useState } from 'react'
import { useTeamEditorContext } from '../context/TeamEditorContext';
import { Alert, Button, Col, Container, FormCheck, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';
import DiscardChangesButton from '../components/buttons/editor/DiscardChangesButton';
import AddPlayerButton from '../components/buttons/editor/AddPlayerButton';
import TeamEditorTable from '../components/tables/editor/TeamEditorTable';
import { LocalStorageKeys } from '../scripts/enum/LocalStorageKeys';
import { Oval } from 'react-loading-icons';
import { Prism as SyntaxHighlighter } from 'react-syntax-highlighter';
import { dracula } from 'react-syntax-highlighter/dist/esm/styles/prism';
import toast from 'react-hot-toast';
import { Permission } from '../scripts/enum/Permission';
import ImportTeamsButton from '../components/buttons/editor/ImportTeamsButton';

import "./Editor.scss";

export default function Editor() {
	const teamEditor = useTeamEditorContext();

	const [showMetadata, setShowMetadata] = useState<boolean>(String(localStorage.getItem(LocalStorageKeys.EDITOR_SHOW_METADATA)) == "true");
	const [namesUpdating, setNamesUpdating] = useState<boolean>(false);
	const [exportModalVisible, setExportModalVisible] = useState<boolean>(false);
	const [jsonData, setJsonData] = useState<string>("");

	useEffect(() => {
		teamEditor.tournamentSystem.pauseBackgroundTasks();
		return () => {
			teamEditor.tournamentSystem.rerumeBackgroundTasks();
		}
	}, []);

	function handleShowMetadataChange(e: ChangeEvent<any>) {
		const checked = e.target.checked;
		setShowMetadata(checked);
		localStorage.setItem(LocalStorageKeys.EDITOR_SHOW_METADATA, checked);
	}

	function updateUsernames() {
		if (!namesUpdating) {
			setNamesUpdating(true);

			teamEditor.updateUsernames().then(() => {
				setNamesUpdating(false);
			}).catch((_) => {
				setNamesUpdating(false);
			});
		}
	}

	function exportData() {
		setJsonData(JSON.stringify(teamEditor.players, null, 4));
		setExportModalVisible(true);
	}

	async function apply() {
		const req = await teamEditor.tournamentSystem.api.upploadTeam(teamEditor.players);
		if (req.success) {
			toast.success("Team uploaded");
			setExportModalVisible(false);
		} else {
			toast.error("Failed to upload team. " + req.message);
		}
	}

	function download() {
		var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(jsonData);
		var downloadAnchorNode = document.createElement('a');
		downloadAnchorNode.setAttribute("href", dataStr);
		downloadAnchorNode.setAttribute("download", "TeamData.json");
		document.body.appendChild(downloadAnchorNode); // required for firefox
		downloadAnchorNode.click();
		downloadAnchorNode.remove();
	}

	return (
		<>
			<Container fluid>
				{teamEditor.offlineMode &&
					<Row className='mb-2'>
						<Col>
							<Alert variant='warning'>Running in offline mode!. Offline mode teams are not compatible with online mode</Alert>
						</Col>
					</Row>
				}

				{namesUpdating &&
					<Row className='mb-2'>
						<Col>
							<Alert variant='info'>
								Updating usernames&nbsp;
								<span className='float-end'>
									<Oval />
								</span>
							</Alert>
						</Col>
					</Row>
				}

				<Row className='mt-1'>
					<Col>
						<div>
							<span className='float-end'>
								<Button onClick={updateUsernames} value="info" className='mx-1 my-1' disabled={namesUpdating} variant='info'>Update usernames</Button>
								<Button onClick={exportData} value="primary" className='mx-1 my-1' disabled={namesUpdating} variant='success'>Save</Button>
								<ImportTeamsButton className='mx-1 my-1' disabled={namesUpdating} />
								<DiscardChangesButton className='mx-1 my-1' disabled={namesUpdating} />
								<AddPlayerButton className="mx-1 my-1" disabled={namesUpdating} />
							</span>
						</div>
					</Col>
				</Row>

				<Row className='mt-2 mx-2'>
					<TeamEditorTable showMetadata={showMetadata} disableInputs={namesUpdating} />
				</Row>

				<Row className='mt-2 mx-2'>
					<FormCheck type="switch" label="Show metadata field (Advanced users only)" checked={showMetadata} onChange={handleShowMetadataChange} />
				</Row>
			</Container>

			<Modal show={exportModalVisible} dialogClassName='modal-xl' onHide={() => setExportModalVisible(false)}>
				<ModalHeader closeButton>
					<ModalTitle>Team data</ModalTitle>
				</ModalHeader>

				<ModalBody>
					<Container fluid>
						<Row>
							<Col>
								<div className='editor-export-content-container'>
									<SyntaxHighlighter language="json" style={dracula}>
										{jsonData}
									</SyntaxHighlighter>
								</div>
							</Col>
						</Row>
					</Container>
				</ModalBody>
				<ModalFooter>
					<Button variant="secondary" onClick={() => { setExportModalVisible(false) }}>Close</Button>
					<Button variant="success" onClick={apply} disabled={!teamEditor.tournamentSystem.authManager.hasPermission(Permission.EDIT_TEAMS)}>Apply</Button>
					<Button variant="primary" onClick={download}>Download</Button>
				</ModalFooter>
			</Modal>
		</>
	)
}