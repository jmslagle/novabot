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

create table preset
(
  user_id varchar(50) not null,
  preset_name varchar(50) not null,
  location varchar(50) not null,
  primary key (user_id, preset_name, location),
  constraint preset_users_id_fk
  foreign key (user_id) references users (id)
);

