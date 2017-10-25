create table geocoding
(
  lat double not null,
  lon double not null,
  suburb varchar(30) default 'NULL' null,
  street_num varchar(15) default 'NULL' null,
  street varchar(100) default 'NULL' null,
  state varchar(30) default 'NULL' null,
  postal varchar(15) default 'NULL' null,
  neighbourhood varchar(50) default 'NULL' null,
  sublocality varchar(50) default 'NULL' null,
  country varchar(50) default 'NULL' null,
  primary key (lat, lon)
)
;

create table pokemon
(
  user_id varchar(50) default 'NULL' null,
  id varchar(30) default 'NULL' null,
  max_iv float default '100' null,
  min_iv float default '0' null,
  location varchar(30) default 'NULL' null,
  constraint pokemon_user_id_id_channel_max_iv_min_iv_pk
  unique (user_id, id, location, max_iv, min_iv)
)
;

create table raid
(
  user_id varchar(50) not null,
  boss_id int not null,
  location varchar(30) not null,
  primary key (user_id, boss_id, location)
)
;

create table raidlobby
(
  lobby_id int not null
    primary key,
  gym_id varchar(50) default 'NULL' null,
  members int default '0' null,
  role_id varchar(50) default 'NULL' null,
  channel_id varchar(50) default 'NULL' null,
  next_timeleft_update int default 15 null,
  invite_code varchar(10) default 'NULL' null
)
;

create table users
(
  id varchar(50) not null
    primary key,
  joindate timestamp default current_timestamp() not null,
  paused tinyint(1) default '0' null,
  constraint users_id_uindex
  unique (id)
)
;

alter table pokemon
  add constraint pokemon_users_id_fk
foreign key (user_id) references users (id)
;

alter table raid
  add constraint raid_users_id_fk
foreign key (user_id) references users (id)
;

