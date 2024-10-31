CREATE TABLE consumer (
	id	 BIGSERIAL,
	name	 VARCHAR(512) NOT NULL,
	age	 INTEGER NOT NULL,
	gender VARCHAR(512) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE media (
	id		 BIGSERIAL,
	title		 VARCHAR(512) NOT NULL,
	release_date	 DATE NOT NULL,
	average_rating SMALLINT NOT NULL,
	type		 VARCHAR(512) NOT NULL,
	PRIMARY KEY(id)
);

CREATE TABLE consumer_media (
	consumer_id BIGINT,
	media_id	 BIGINT,
	PRIMARY KEY(consumer_id,media_id)
);

ALTER TABLE consumer_media ADD CONSTRAINT consumer_media_fk1 FOREIGN KEY (consumer_id) REFERENCES consumer(id);
ALTER TABLE consumer_media ADD CONSTRAINT consumer_media_fk2 FOREIGN KEY (media_id) REFERENCES media(id);

