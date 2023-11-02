import React from 'react'
import { Button, Col, Container, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';

interface Props {
	visible: boolean;
	children?: any;
	cancelText?: string;
	confirmText?: string;
	cancelButtonVariant?: string;
	confirmButtonVariant?: string;
	title: string
	onCancel: () => void;
	onConfirm: () => void;
}

export default function ConfirmModal({ visible, children, cancelText = "Cancel", confirmText = "Confirm", title, onCancel, onConfirm, cancelButtonVariant = "secondary", confirmButtonVariant = "primary" }: Props) {
	return (
		<Modal show={visible} onHide={onCancel}>
			<ModalHeader closeButton>
				<ModalTitle>{title}</ModalTitle>
			</ModalHeader>

			<ModalBody>
				<Container fluid>
					{children != null &&
						<Row>
							<Col>
								{children}
							</Col>
						</Row>
					}
				</Container>
			</ModalBody>
			<ModalFooter>
				<Button variant={cancelButtonVariant} onClick={onCancel}>
					{cancelText}
				</Button>
				<Button variant={confirmButtonVariant} onClick={onConfirm}>
					{confirmText}
				</Button>
			</ModalFooter>
		</Modal>
	)
}
