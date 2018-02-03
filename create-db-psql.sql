CREATE TABLE pokemon (
  user_id character varying(50) NOT NULL,
  max_iv real DEFAULT 100.0000 NOT NULL,
  min_iv real DEFAULT 0.000000 NOT NULL,
  max_lvl smallint DEFAULT 40 NOT NULL,
  min_lvl smallint DEFAULT '0'::smallint NOT NULL,
  max_cp integer DEFAULT 2147483647 NOT NULL,
  min_cp integer DEFAULT 0 NOT NULL,
  location character varying(30) NOT NULL,
  id smallint
);

CREATE TABLE preset (
  user_id character varying(50) NOT NULL,
  preset_name character varying(50) NOT NULL,
  location character varying(50) NOT NULL
);

CREATE TABLE raid (
  user_id character varying(50) NOT NULL,
  boss_id smallint NOT NULL,
  location character varying(30) NOT NULL,
  egg_level smallint NOT NUll,
  gym_name character varying(100) NOT NULL
);

CREATE TABLE raidlobby (
  lobby_id smallint NOT NULL,
  gym_id character varying(50),
  members smallint DEFAULT 0,
  role_id character varying(50),
  channel_id character varying(50),
  next_timeleft_update smallint,
  invite_code character varying(10)
);

CREATE TABLE spawninfo (
  lat double precision NOT NULL,
  lon double precision NOT NULL,
  timezone character varying(30),
  suburb character varying(30),
  street_num character varying(15),
  street character varying(100),
  state character varying(30),
  postal character varying(15),
  neighbourhood character varying(50),
  sublocality character varying(50),
  country character varying(50)
);

CREATE TABLE users (
  id character varying(50) NOT NULL,
  joindate timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
  bot_token character varying (70) DEFAULT '',
  paused boolean
);

ALTER TABLE ONLY preset
ADD CONSTRAINT preset_pkey PRIMARY KEY (user_id, preset_name, location);

ALTER TABLE ONLY raid
ADD CONSTRAINT raid_pkey PRIMARY KEY (user_id, boss_id, egg_level,location, gym_name);

ALTER TABLE ONLY raidlobby
ADD CONSTRAINT raidlobby_pkey PRIMARY KEY (lobby_id);

ALTER TABLE ONLY spawninfo
ADD CONSTRAINT spawninfo_pkey PRIMARY KEY (lat, lon);

ALTER TABLE ONLY users
ADD CONSTRAINT users_pkey PRIMARY KEY (id);