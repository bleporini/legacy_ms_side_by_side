
alter table tbl_customer change column firt_name first_name varchar(255);

insert into tbl_customer (first_name, last_name) values ('Axel', 'Folley');
update tbl_customer set first_name = 'Tyler', last_name='Durden';

select @customer_id := last_insert_id();

set autocommit = 0;

start transaction;

insert into tbl_order
  (fkid_customer, total)
values (@customer_id, 72);

select @order_id := last_insert_id();

select @item1 := pkid_product from tbl_product limit 1;

insert into tbl_order_item
  (fkid_order, fkid_product, quantity, price)
  values (@order_id, @item1, 1, 50);

commit;


select @item2 := pkid_product from tbl_product limit 1 ;
