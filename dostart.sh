#!/usr/bin/env bash


ccloud login || exit -1

#bash --version
ccloud kafka topic list| tail -n +3
ccloud kafka topic list| tail -n +3| sed -e 's/^/ccloud -vvv kafka topic delete & /'|sh &
jobs
wait 

topics=( \
"mysql.legacy.tbl_product" \
"mysql.legacy.tbl_customer" \
"mysql.legacy.tbl_order" \
"mysql.legacy.tbl_order_item" \
"productsById" \
"customersById" \
"orders" \
"dbhistory.inventory" \
"mysql" \
)

for topic in ${topics[*]}
do
    ccloud kafka topic create $topic &
done

wait

ccloud kafka topic list
