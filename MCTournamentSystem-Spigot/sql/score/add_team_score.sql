INSERT INTO
	team_score (team_id, server, reason, amount)
SELECT
	id,
	?,
	?,
	?
FROM
	teams
WHERE
	team_number = ?