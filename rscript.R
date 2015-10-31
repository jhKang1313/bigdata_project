setwd("C:/Users/Jinhyuk/Documents")   #working directory 설정

#-------날씨 데이터 읽기
weather.data <- read.csv("weather_data.csv")  #weather_data.csv 파일 읽기
View(weather.data)
str(weather.data)

#-------매출 데이터 읽기
sales.data <- read.csv("sales_data.csv", na.strings=c("")) #sales_data.csv 파일 읽기, 매출 기록이 없는 것은 NA로 처리
sales.data$매출 = as.character(sales.data$매출) #매출이 factor로 저장되어 있기 때문에 문자열로 변경
sales.data$매출 = as.integer(gsub(",", "", sales.data$매출)) #매출에 ',' 문자를 공백으로 변경한 후, 정수형으로 Type 변경
View(sales.data)        
str(sales.data)

#날짜는 factor로 관리되어야 할까? 문자열로 관리 되어야 할까?
#-------날씨 매출 데이터 병합
weather.sales.data <- weather.data
weather.sales.data$매출 <- as.integer(sales.data$매출) #weather.sales.data 로 날씨, 매출 데이터 병합
str(weather.sales.data)
View(weather.sales.data)


#-------기사 개수 -----------
article.data <- read.csv("article_data.csv") #article_data.csv 파일 읽기  -> 기사 내용으로 인해 파일 크기가 커서 파일을 제대로 못 읽어옴 -> 내용은 중요하지 않기 때문에 날ㄹ
article.count <- table(article.data$날짜, article.data$분류)   #날짜에 따른 경제면, 사회면 기사 개수
article.count <- as.data.frame.matrix(article.count)  #table -> dataframe 변환
article.count <- data.frame(경제 = c(NA, article.count[,1]),사회 = c(NA, article.count[,2])) #2013년 1월 1일 신문 기사 수집이 안되기 때문에 그 날 기사 수를 NA로 설정

weather.sales.article.data <- weather.sales.data    #기사 수 추가된 dataframe 생성
weather.sales.article.data$경제기사수 <- article.count$경제 #경제 기사 수 추가
weather.sales.article.data$사회기사수 <- article.count$사회 #사회 기사 수 추가
weather.sales.article.data$총기사수 <- rowSums(article.count[,1:2], na.rm = TRUE) #총 기사 수 추가
View(weather.sales.article.data)
str(weather.sales.article.data)

#-------분 석-----------
weather.sales.article.data
cor(weather.sales.article.data[,3:13], , use = "pairwise.complete.obs")
cor(weather.sales.article.data$사회기사수, weather.sales.article.data$매출, use = "pairwise.complete.obs")

m <- lm(매출 ~ 총기사수, weather.sales.article.data)
summary(m)





