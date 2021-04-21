create user app with password 'sv100500';

create schema game;


create table game.spot(
	id serial primary key,
	lat double precision,
	lon double precision
);

create table game.question(
	id serial primary key,
	txt varchar(6000),
	option_a varchar(6000),
	option_b varchar(6000),
	option_c varchar(6000),
	option_d varchar(6000),
	correct_answer char(1)
);


create table game.play(
	id serial primary key,
	team_name varchar(6000),
	device_id varchar(6000),
	registration_ts bigint
);

create table game.play_point(
	id serial primary key,
	play_ptr int,
	point_ptr int,
	question_ptr int,
	num int,
	status int,
	answer_ts bigint
);

create view game.v_leader as
select
	pp.play_ptr, 
	count(*) as points_total, 
	sum(pp.status) as points_passed, 
	count(*) - sum(pp.status) as distance_to_finish, 
	max(pp.answer_ts) as last_answer_ts 
from 
	game.play_point pp 
group by 
	pp.play_ptr
order by distance_to_finish, last_answer_ts
limit 1;

grant all on game.play to app;
grant all on game.play_point to app;
grant all on game.question to app;
grant all on game.spot to app;

grant all on game.v_leader to app;

grant all on game.play_id_seq to app;
grant all on game.play_point_id_seq to app;
grant all on game.question_id_seq to app;
grant all on game.spot_id_seq to app;

