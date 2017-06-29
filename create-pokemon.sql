CREATE TABLE pokemon
(
  user_id VARCHAR(50),
  id VARCHAR(30),
  max_iv FLOAT DEFAULT '100',
  min_iv FLOAT DEFAULT '0',
  location VARCHAR(30),
  CONSTRAINT pokemon_users_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
);
CREATE UNIQUE INDEX pokemon_user_id_id_channel_max_iv_min_iv_pk ON pokemon (user_id, id, location, max_iv, min_iv);