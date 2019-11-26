use legacy;

create table tbl_customer
(
  pkid_customer int auto_increment primary key ,
  firt_name varchar(255),
  last_name varchar (255)
);


create table tbl_order
(
  pkid_order int  auto_increment primary key,
  fkid_customer int,
  total DECIMAL(13, 2),
  foreign key fkc_order__customer(fkid_customer) references tbl_customer(pkid_customer)
);

create table tbl_product
(
  pkid_product int auto_increment primary key,
  ean13 varchar(13),
  brand varchar(255),
  name varchar(255)
);

load data infile '/var/lib/mysql-files/products.csv'
into table tbl_product
columns terminated by ';'
ignore 1 rows
(ean13, brand, name)
;


create table tbl_order_item
(
  pkid_order_item int auto_increment primary key,
  fkid_order int ,
  fkid_product int,
  quantity DECIMAL(13,4),
  price DECIMAL(13, 4),
  foreign key fkc_order_item__order(fkid_order) references tbl_order(pkid_order),
  foreign key fkc_order_item__product(fkid_product) references tbl_product(pkid_product)
);


GRANT SELECT, RELOAD, SHOW DATABASES, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'debezium' IDENTIFIED BY 'dbz';
