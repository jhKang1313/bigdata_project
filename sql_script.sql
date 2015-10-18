load data local infile 'C:/article.csv' into table article fields terminated by ',';
set sql_safe_updates = 0;
delete from article;
select * from article;

show variables;
set character_set_client = euckr;
set character_set_connection = euckr;
set character_set_database = euckr;
set character_set_results= euckr;	
set character_set_server = euckr;

select count(*) from article;