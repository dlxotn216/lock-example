drop table if exists app_coupon;
create table app_coupon
(
  coupon_key     bigint auto_increment not null,
  name           varchar(255) not null,
  remained_stock bigint       not null,
  
  created_at     datetime(6) not null,
  created_by     bigint       not null,
  modified_at    datetime(6) not null,
  modified_by    bigint       not null,
  primary key (coupon_key)
);

drop table if exists app_coupon_user;
create table app_coupon_user
(
  coupon_user_key bigint auto_increment not null,
  coupon_key      bigint not null,
  user_key        bigint not null,
  
  created_at      datetime(6) not null,
  created_by      bigint not null,
  modified_at     datetime(6) not null,
  modified_by     bigint not null,
  primary key (coupon_user_key)
);
create unique index app_coupon_user_ux_01 on app_coupon_user (coupon_key, user_key);

drop table if exists usr_user;
create table usr_user
(
  user_key  bigint auto_increment not null,
  email     varchar(255) not null,
  name      varchar(255) not null,
  introduce varchar(4000) null,
  
  primary key (user_key)
);
create unique index usr_user_ux_01 on usr_user (email);

drop table if exists usr_user_ver;
create table usr_user_ver
(
  user_key  bigint auto_increment not null,
  email     varchar(255) not null,
  name      varchar(255) not null,
  introduce varchar(4000) null,
  version   bigint not null,

  primary key (user_key)
);
create unique index usr_user_ver_ux_01 on usr_user_ver (email);
