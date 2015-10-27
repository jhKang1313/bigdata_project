setwd("C:/Users/Jinhyuk/Documents")   #working directory 설정

weather.data <- read.csv("weather_data.csv")  #weather_data.csv 파일 읽기
View(weather.data)
str(weather.data)

sales.data <- read.csv("sales_data.csv", na.strings=c("")) #sales_data.csv 파일 읽기, 매출 기록이 없는 것은 NA로 처리
sales.data$매출 = as.character(sales.data$매출) #매출이 factor로 저장되어 있기 때문에 문자열로 변경
sales.data$매출 = as.integer(gsub(",", "", sales.data$매출)) #매출에 ',' 문자를 공백으로 변경한 후, 정수형으로 Type 변경
View(sales.data)        
str(sales.data)

#날짜는 factor로 관리되어야 할까? 문자열로 관리 되어야 할까?
weather.sales.data <- weather.data
weather.sales.data$매출 <- as.integer(sales.data$매출) #weather.sales.data 로 날씨, 매출 데이터 병합
str(weather.sales.data)
View(weather.sales.data)


#-------기사-----------
article_data <- read.csv("article_data.csv")
View(article_data)












