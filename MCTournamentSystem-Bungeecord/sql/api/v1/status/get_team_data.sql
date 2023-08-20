SELECT
	t.id AS id,
	t.team_number AS team_number,
	t.kills AS kills,
	IFNULL(SUM(s.amount), 0) AS total_score
FROM
	teams AS t
	LEFT JOIN team_score AS s ON s.team_id = t.id
GROUP BY
	t.id