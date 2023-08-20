INSERT INTO
	player_score (player_id, server, reason, amount)
SELECT
	id,
	?,
	?,
	?
FROM
	players
WHERE
	uuid = ?