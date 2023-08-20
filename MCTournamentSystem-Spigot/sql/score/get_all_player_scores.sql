SELECT
	p.uuid AS uuid,
	IFNULL(SUM(s.amount), 0) AS score
FROM
	players AS p
	LEFT JOIN player_score AS s ON s.player_id = p.id
GROUP BY
	p.id