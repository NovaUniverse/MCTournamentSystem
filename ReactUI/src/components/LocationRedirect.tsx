import React from 'react'
import { redirect, useNavigate } from 'react-router';

interface Props {
	target: string;
}

export default function LocationRedirect({ target }: Props) {
	window.location.href = target;
	return (
		<></>
	)
}
