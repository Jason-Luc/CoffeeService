drop table if exists t_coffee;
drop table if exists t_order;
drop table if exists t_order_coffee;

create table t_coffee
(
    id          bigint auto_increment,
    create_time timestamp,
    update_time timestamp,
    name        varchar(255),
    price       double,
    primary key (id)
);

create table t_order
(
    id          bigint,
    create_time timestamp,
    update_time timestamp,
    customer    varchar(255),
    state       integer not null,
    discount    integer,
    total       double,
    waiter      varchar(255),
    barista     varchar(255),
    primary key (id)
);



create table t_order_coffee
(
    coffee_order_id bigint not null,
    items_id        bigint not null
);

