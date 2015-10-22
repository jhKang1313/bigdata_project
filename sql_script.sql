load data local infile 'C:/article.csv' into table article fields terminated by ',';

set sql_safe_updates = 0;
delete from article;
select * from article;
drop table article;
show variables;

select count(*) from originword_table;
select count(*) from sentimentword_table;
