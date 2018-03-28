CREATE TABLE stations (
	id INT AUTO_INCREMENT NOT NULL,
	name VARCHAR(64) NOT NULL,
	latitude DECIMAL(10, 8) NOT NULL,
	longitude DECIMAL(11, 8) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE readings (
	stationID INT NOT NULL,
	time DATETIME NOT NULL,
	humidity FLOAT,
	temperature FLOAT,
	apm FLOAT,
	airPressure FLOAT,
	PRIMARY KEY(stationID, time),
	FOREIGN KEY(stationID) references stations(id)
);

CREATE TABLE readingsArchive (
	stationID INT NOT NULL,
	time DATETIME NOT NULL,
	humidity FLOAT,
	temperature FLOAT,
	apm FLOAT,
	airPressure FLOAT,
	PRIMARY KEY(stationID, time),
	FOREIGN KEY(stationID) references stations(id)
);

delimiter #
CREATE PROCEDURE archive()
BEGIN
DECLARE n INT DEFAULT 0;
DECLARE i INT DEFAULT 0;

CREATE TEMPORARY TABLE tmpreadings ENGINE=memory AS SELECT stationID, time, humidity, temperature, apm, airPressure FROM readings r WHERE time = (SELECT MAX(time) FROM readings WHERE stationID = r.stationID);


SELECT COUNT(*) FROM tmpreadings INTO n;

SET i=0;
WHILE i<n DO 
	INSERT INTO readingsArchive(
		stationID, 
		time, 
		humidity, 
		temperature, 
		apm,
		airPressure
	) 
	SELECT
		stationID, 
		NOW(), 
		humidity,
		temperature,
		apm,
		airPressure
	FROM tmpreadings LIMIT i,1;

	SET i = i + 1;

END WHILE;

SELECT * FROM readingsArchive;

DROP TEMPORARY TABLE IF EXISTS tmpreadings;

END#

CREATE PROCEDURE cleanup()
BEGIN

DELETE r1 
FROM readings AS r1 
INNER JOIN (
SELECT stationID, MAX(time) as time FROM readings GROUP BY(stationID)
) AS r2 ON r1.time != r2.time AND r1.stationID = r2.stationID;

DELETE FROM readingsArchive WHERE TIMEDIFF(NOW(), time) > '12:00:00';
END#

CREATE PROCEDURE insertReading(IN id INT, IN humidity FLOAT, IN temperature FLOAT, IN apm FLOAT, IN airPressure FLOAT)
BEGIN
	DELETE FROM readings r WHERE stationID = id;
	INSERT INTO readings(stationID, time, humidity, temperature, apm, airPressure)
	VALUES(
		id,
		NOW(),
		humidity,
		temperature,
		apm,
		airPressure
	);
END#

delimiter ;

CREATE EVENT archive
ON SCHEDULE EVERY 1 HOUR
DO
	call archive();

CREATE EVENT cleanup
ON SCHEDULE EVERY 10 MINUTE
DO
	call cleanup();
