import React, { ChangeEvent, useState } from 'react'
import { Button, Col, Container, FormControl, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';
import toast from 'react-hot-toast';

interface Props {
	visible: boolean;
	showCloseButtons?: boolean;
	onClose?: () => void;
	onSubmit: (username: string, password: string) => void;
}

export default function LoginModal({ visible, showCloseButtons = true, onClose, onSubmit }: Props) {
	const [username, setUsername] = useState<string>("");
	const [password, setPassword] = useState<string>("");

	function handleUsernameChange(e: ChangeEvent<any>) {
		setUsername(e.target.value);
	}

	function handlePasswordChange(e: ChangeEvent<any>) {
		setPassword(e.target.value);
	}

	function handleLogin() {
		if (username.length == 0) {
			toast.error("Please enter a username");
			return;
		}

		if (username.length == 0) {
			toast.error("Please enter a password");
			return;
		}

		onSubmit(username, password);
	}

	function handleKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
		if (e.key === 'Enter') {
			handleLogin();
		}
	}

	return (
		<>
			{visible &&
				<Container className='my-4'>
					<Row>
						<Col>
							<div
								className="modal show"
								style={{ display: 'block', position: 'initial' }}
							>
								<div className='modal-dialog modal-lg'>
									<div className='modal-content'>
										<ModalHeader closeButton={showCloseButtons}>
											<ModalTitle>Login</ModalTitle>
										</ModalHeader>

										<ModalBody>
											<Container fluid>
												<Row>
													<Col>
														<label>Username</label>
														<FormControl type='text' placeholder='Username' value={username} onKeyDown={handleKeyDown} onChange={handleUsernameChange} />
													</Col>
												</Row>

												<Row>
													<Col>
														<label>Password</label>
														<FormControl type="password" placeholder='Password' value={password} onKeyDown={handleKeyDown} onChange={handlePasswordChange} />
													</Col>
												</Row>
											</Container>
										</ModalBody>
										<ModalFooter>
											{showCloseButtons &&
												<Button variant="secondary" onClick={onClose}>
													Cancel
												</Button>
											}
											<Button variant="success" onClick={handleLogin}>
												Login
											</Button>
										</ModalFooter>
									</div>
								</div>
							</div>
						</Col>
					</Row>
				</Container>
			}
		</>
	)
}
