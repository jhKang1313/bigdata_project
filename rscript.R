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


#-------기사-----------
article.data <- read.csv("article_data.csv") #article_data.csv 파일 읽기  -> 기사 내용으로 인해 파일 크기가 커서 파일을 제대로 못 읽어옴 -> 내용은 중요하지 않기 때문에 날ㄹ
article.count <- table(article.data$날짜, article.data$분류)   #날짜에 따른 경제면, 사회면 기사 개수
article.count <- as.data.frame.matrix(article.count)  #table -> dataframe 변환
article.count$날짜 <- levels(article.data$날짜) #날짜 column 추가
weather.sales.article.data <- weather.sales.data


article.count.eco <- c()
article.count.sco
for(i in 1:nrow(weather.sales.data)){
  for(j in 1:nrow(article.count)){
    if(weather.sales.data$날짜[i] == article.count$날짜[j]){
      article.count.eco <- c(article.count.eco, article.count$경제[j])
      print(i)
      break
    }
  }
  if(j == nrow(article.count)){
    article.count.eco <- c(article.count.eco, 0)
  }
}
article.count.eco
weather.sales.article.data$경제 <- article.count.eco
View(weather.sales.article.data)

article.count$날짜 == article.data$날짜
str(article.count)
data1 <- data.frame(article.data$날짜, 경제 = as.vector(article.count[,1]) , 사회 = as.vector(article.count[,2]))

name
article.count[weather.sales.article.data$날짜 == article.count[,0],1]
str(article.count)
levels(weather.sales.data$날짜)
str(article.count)
data <- data.frame(weather.sales.data$날짜)#, article.count)
tapply(article.data$날짜, weather.sales.data$날짜, equal)   
View(article.data)
str(article.data)












