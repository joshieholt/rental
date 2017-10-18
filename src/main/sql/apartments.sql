DROP TABLE IF EXISTS apartments;

CREATE TABLE apartments (
	id BIGSERIAL PRIMARY KEY NOT NULL,
	rent INT NOT NULL,
	number_of_bedrooms INT NOT NULL,
	number_of_bathrooms NUMERIC(4,2) NOT NULL,
	square_footage INT NOT NULL,
	address VARCHAR(255) NOT NULL,
	city VARCHAR(255) NOT NULL,
	state VARCHAR(50) NOT NULL,
	zip_code VARCHAR(25) NOT NULL,
	user_id BIGINT NOT NULL,
	is_active boolean not null default false
);
