CREATE TABLE geocoding
(
  lat DOUBLE NOT NULL,
  lon DOUBLE NOT NULL,
  suburb VARCHAR(30),
  street_num VARCHAR(15),
  street VARCHAR(100),
  state VARCHAR(30),
  postal VARCHAR(15),
  neighbourhood VARCHAR(50),
  sublocality VARCHAR(50),
  country VARCHAR(50),
  CONSTRAINT `PRIMARY` PRIMARY KEY (lat, lon)
);