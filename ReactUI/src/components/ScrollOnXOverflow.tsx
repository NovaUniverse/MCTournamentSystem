import React from 'react'

import "./ScrollOnXOverflow.scss";

interface Props {
	children: any;
}

export default function ScrollOnXOverflow({ children }: Props) {
	return (
		<div className="scroll_on_x_overflow">
			{children}
		</div>
	)
}
