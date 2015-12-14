#테이블 생성 
create table article_table(					#신문 기사 테이블				
article_date varchar(20) character set 'utf8' not null,
article_type varchar(20) character set 'utf8' not null,
article_content varchar(4096) character set 'utf8' not null,
posi_word_count int not null,
nega_word_count int not null,
non_word_count int not null
)
DEFAULT CHARACTER SET = utf8;
load data local infile 'C:/Users/Jinhyuk/Desktop/article.csv' into table article_table fields terminated by ',';	#수집한 신문 기사 읽기

create table sentimentword_table(					#감성 사전 테이블
origin_word varchar(128) character set 'utf8' not null,
word_type varchar(20) character set 'utf8' not null,
sentiment_type int not null,
sentiment_score int not null,
primary key(origin_word)
)
default character set = utf8;


create table originword_table(						#원형 어휘 테이블
`text` varchar(128) character set 'utf8' not null,
origin_word varchar(128) character set 'utf8' not null,
primary key(`text`),
constraint origin_word foreign key(origin_word) references sentimentword_table(origin_word)
)
DEFAULT CHARACTER SET = utf8;

create table na_table(						#감성 판단 불가 어휘 테이블		
origin_word varchar(100) character set 'utf8' not null
)
DEFAULT CHARACTER SET = utf8;