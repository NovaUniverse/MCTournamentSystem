SELECT
	p.id AS id,
	p.uuid AS uuid,
	p.username AS username,
	p.kills AS kills,
	p.metadata AS metadata,
	t.team_number AS team_number,
	COALESCE(ps.total_score, 0) AS total_score,
	COALESCE(ts.team_score, 0) AS team_score
FROM
	players AS p
	LEFT JOIN (
		SELECT
			player_id,
			SUM(amount) AS total_score
		FROM
			player_score
		GROUP BY
			player_id
	) AS ps ON ps.player_id = p.id
	LEFT JOIN teams AS t ON t.team_number = p.team_number
	LEFT JOIN (
		SELECT
			team_id,
			SUM(amount) AS team_score
		FROM
			team_score
		GROUP BY
			team_id
	) AS ts ON ts.team_id = t.id
GROUP BY
	p.id