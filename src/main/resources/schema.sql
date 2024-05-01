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
