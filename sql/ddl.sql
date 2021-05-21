create user app with password 'sv100500';

create schema game;


create table game.game_properties(
	id int primary key,
	game_start_ts bigint,
	game_end_ts bigint,
	penalty_duration bigint);


insert into game.game_properties values
	(1,
	cast (EXTRACT(EPOCH FROM TIMESTAMP '2021-04-25') * 1000 as BIGINT),
	cast (EXTRACT(EPOCH FROM TIMESTAMP '2022-04-25') * 1000 as BIGINT),
	30 * 1000);

create table game.spot(
	id serial primary key,
	lat double precision,
	lon double precision,
	hint varchar(6000),
	render_id int
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


create table game.answer_attempts(
	id serial primary key,
	play_point_ptr int,
	answer char(1),
	answer_ts bigint
);

create table game.play(
	id serial primary key,
	team_name varchar(6000),
	device_id varchar(6000),
	registration_ts bigint,
	points_planned int
);

create table game.play_point(
	id serial primary key,
	play_ptr int,
	point_ptr int,
	question_ptr int,
	num int
);

create view game.v_play_point_with_status as
select
	pp.*,
	case when q.correct_answer = a.answer then 1 else 0 end as status,
	a.answer_ts,
	coalesce(a.attempt, 0) as attempt
from 
	game.play_point pp 
	join game.question q on pp.question_ptr = q.id 
	left join 
		(select
			*,
			case
				when a.id is null then 0
				else rank() over (partition by a.play_point_ptr order by a.answer_ts) 
			end as attempt,
			case
				when a.id is null then 0
				else rank() over (partition by a.play_point_ptr order by a.answer_ts desc) 
			end as anti_attempt
		from
			game.answer_attempts a
		) a on a.play_point_ptr = pp.id
where
	coalesce(a.anti_attempt, 1) = 1;

create view game.v_leader as
select
	pp.play_ptr, 
	count(*) as points_total, 
	sum(pp.status) as points_passed, 
	count(*) - sum(pp.status) as distance_to_finish, 
	max(pp.answer_ts) as last_answer_ts 
from 
	game.v_play_point_with_status pp 
group by 
	pp.play_ptr
order by distance_to_finish, last_answer_ts
limit 1;


GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA game TO app;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA game TO app;
