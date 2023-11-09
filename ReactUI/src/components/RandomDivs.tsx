import React from 'react'
import { useLocation } from 'react-router-dom';

export default function RandomDivs() {
	const randomDivList: number[] = [];
	for (let i = 0; i < 20; i++) {
		randomDivList.push(i + 1);
	}

	const location = useLocation();

	return (
		<>
			{/* These are just for fun to allow css mods to add random stuff */}
			{randomDivList.map(i => <div key={i} className={"random_div_" + i} data-page={location.pathname} />)}
		</>
	)
}
