CREATE TABLE raid
(
  user_id VARCHAR(50) NOT NULL,
  raid_level INT(11) NOT NULL,
  boss_id INT(11) NOT NULL,
  location VARCHAR(30) NOT NULL,
  CONSTRAINT `PRIMARY` PRIMARY KEY (user_id, raid_level, boss_id, location),
  CONSTRAINT raid_users_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
);