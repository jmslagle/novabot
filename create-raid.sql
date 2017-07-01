create table raid
(
  user_id varchar(50) not null,
  boss_id int not null,
  location varchar(30) not null,
  primary key (user_id, boss_id, location),
  constraint raid_users_id_fk
  foreign key (user_id) references pokealerts.users (id)
)
;
