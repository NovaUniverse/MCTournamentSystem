SELECT
	IFNULL(SUM(amount), 0) as total_score
FROM
	player_score
WHERE
	player_id = (
		SELECT
			id
		FROM
			players
		WHERE
			uuid = ?
	)