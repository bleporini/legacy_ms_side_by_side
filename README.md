# Setup

Your local Docker registry should count a `ccloud` docker image in which you can run the `ccloud` CLI, otherwise you can build one with :

```dockerfile
FROM ubuntu

RUN apt-get update
RUN apt-get -y install curl
RUN apt-get clean
RUN curl -L https://cnfl.io/ccloud-cli | sh -s -- -b /usr/local/bin
ENTRYPOINT ["ccloud"]
```

and 

```bash
$ docker build -f ccloud.dockerfile -t ccloud:latest .
```

Run the `start.sh` script having defined two environment variables:
- `DBZ_CONFIG_FILE` : the path to the file containing your Confluent Cloud endpoint, the API Key and the API password in the following properties: `cc.username`, `cc.password`, `cc.bootstrap.server`
-  `CCLOUD_CREDENTIALS` : the path of a file containing your Confluent Cloud credentials defined in the following environment variables : `XX_CCLOUD_EMAIL`, `XX_CCLOUD_PASSWORD`. This will prevent you to enter your credentials interactively.
Example:
```bash
$ DBZ_CONFIG_FILE=/conf/cc.properties CCLOUD_CREDENTIALS=/conf/credentials ./start.sh
```


Run `myretailer.Integration` class, passing it `config-file` path as a system property. This file should contain the following properties:

````properties
ssl.endpoint.identification.algorithm=https
sasl.mechanism=PLAIN
request.timeout.ms=20000
bootstrap.servers=<your Confluent Cloud endpoint>
retry.backoff.ms=500
sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username="<the service account API Key>" password="<the service account API password>";
security.protocol=SASL_SSL
````

The application is ready when :
- The connectors are in the `RUNNING` status and you can check it by querying the Connect enpoint: <http://localhost:8083/connectors/inventory-connector/status>  and <http://localhost:8083/connectors/inventory-connector/status>.
- When the streams application has build the product `KTable`, the <http://localhost:8000/products/count> should return 6156.  

To make the demo, you'll have to run SQL statements. All are in the `scenario.sql` file.

First one is a DDL in order to show off how CDC pushes such a modification:

```sql
alter table tbl_customer change column firt_name first_name varchar(255);
```

Then to show off what happens for a new record, run 

```sql
insert into tbl_customer (first_name, last_name) values ('Axel', 'Folley');
```

You can check that the record is in the `KTable` by querying the application <http://localhost:8000/customers/1> that runs an interactive query and you can consume as well the `mysql.legacy.tbl_customer` topic.

Next is to demonstrate how updates are streamed:

```sql
update tbl_customer set first_name = 'Tyler', last_name='Durden';
```

Finally you run the statements that simulates a new order arrival :

```sql
select @customer_id := last_insert_id();

set autocommit = 0;

start transaction;

insert into tbl_order
  (fkid_customer, total)
values (@customer_id, 72);

select @order_id := last_insert_id();

select @item1 := pkid_product from tbl_product limit 1;

insert into tbl_order_item
  (fkid_order, fkid_product)
  values (@order_id, @item1);

commit;
```

Then you should be able to see a new JSON document in the `orders` topic and in the `orders` collection in `myretailer/orders` MongoDB collection.




