import React, { ChangeEvent, useEffect, useState } from 'react'
import { Button, Col, Container, FormControl, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';

interface Props {
	children?: any;
	visible: boolean;
	title: string;
	cancelType?: string;
	cancelText?: string;
	confirmType?: string;
	confirmText?: string;
	maxLength?: number;
	placeholder?: string;
	extraButtonVisible?: boolean;
	extraButtonText?: string;
	extraButtonType?: string;
	allowEnterToSubmit?: boolean;
	initialValue?: string;
	onExtraButtonClick?: () => void;
	onClose: () => void;
	onSubmit: (text: string) => void;
}

export default function TextPromptModal({ initialValue = "", maxLength, placeholder, children, visible, title, onClose, onSubmit, allowEnterToSubmit = true, cancelText = "Cancel", confirmText = "Confirm", extraButtonVisible = false, extraButtonText = "Extra button", extraButtonType = "secondary", onExtraButtonClick = () => { }, cancelType = "secondary", confirmType = "primary" }: Props) {
	const [text, setText] = useState<string>("");

	useEffect(() => {
		//console.debug("Resetting text prompt modal");
		setText(initialValue);
	}, [visible]);


	function handleTextChange(e: ChangeEvent<any>) {
		setText(e.target.value);
	}

	function handleSubmit() {
		onSubmit(text);
	}

	function handleExtraButton() {
		if (onExtraButtonClick != null) {
			onExtraButtonClick();
		}
	}

	function handleKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
		if (e.key === 'Enter') {
			if (allowEnterToSubmit) {
				handleSubmit();
			}
		}
	}

	return (
		<Modal show={visible} onHide={onClose}>
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
					<Row>
						<Col>
							<FormControl type="text" onChange={handleTextChange} value={text} maxLength={maxLength} placeholder={placeholder} onKeyDown={handleKeyDown} />
						</Col>
					</Row>
				</Container>
			</ModalBody>
			<ModalFooter>
				<Button className='mx-1' variant={cancelType} onClick={onClose}>
					{cancelText}
				</Button>

				{extraButtonVisible &&
					<Button className='mx-1' variant={extraButtonType} onClick={handleExtraButton}>
						{extraButtonText}
					</Button>
				}

				<Button className='mx-1' variant={confirmType} onClick={handleSubmit}>
					{confirmText}
				</Button>
			</ModalFooter>
		</Modal>
	)
}
