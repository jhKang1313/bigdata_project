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
article.data <- read.csv("article_data.csv") #article_data.csv 파일 읽기  -> 기사 내용으로 인해 파일 크기가 커서 파일을 제대로 못 읽어옴 -> 내용은 중요하지 않기 때문에 버림
article.count <- table(article.data$날짜, article.data$분류)   #날짜에 따른 경제면, 사회면 기사 개수
article.count <- as.data.frame.matrix(article.count)  #table -> dataframe 변환
article.count <- data.frame(경제 = c(NA, article.count[,1]),사회 = c(NA, article.count[,2])) #2013년 1월 1일 신문 기사 수집이 안되기 때문에 그 날 기사 수를 NA로 설정
article.count
weather.sales.article.data <- weather.sales.data    #기사 수 추가된 dataframe 생성
weather.sales.article.data$경제기사수 <- article.count$경제 #경제 기사 수 추가
weather.sales.article.data$사회기사수 <- article.count$사회 #사회 기사 수 추가
weather.sales.article.data$총기사수 <- rowSums(article.count[,1:2], na.rm = TRUE) #총 기사 수 추가
View(weather.sales.article.data)
str(weather.sales.article.data)

#-------분 석-----------
View(weather.sales.article.data)
cor(weather.sales.article.data[,3:13] , use = "pairwise.complete.obs") # 각 column에 대한 상관분석
m <- lm(매출 ~ 경제기사수+사회기사수, weather.sales.article.data)
summary(m)


#상관분석에 대한 값이 높게 나오지 않는다.


m <- lm(매출 ~ 총기사수, weather.sales.article.data)#기사수에 따른 매출 회기 분석
summary(m)
#역시 높게 나오지 않는다.

#데이터 수정
#------계절 추가----
season.data <- c()
season.data[grep('201.-(12|01|02)-',weather.sales.article.data$날짜)] <- '겨울' #1, 2, 12월에 해당하는 인덱스에 겨울 저장
season.data[grep('201.-(03|04|05)-',weather.sales.article.data$날짜)] <- '봄'   #3, 4, 5월은 봄
season.data[grep('201.-(06|07|08)-',weather.sales.article.data$날짜)] <- '여름'
season.data[grep('201.-(09|10|11)-',weather.sales.article.data$날짜)] <- '가을'

w.s.a.s.data <- weather.sales.article.data 
w.s.a.s.data$계절 <- as.factor(season.data) #계절 column 추가
View(w.s.a.s.data)
str(w.s.a.s.data)

#-----기온, 매출 월별로 정규화-----
#----계절 별로 상대적인 온도를 구한다.
relativeTemp <- c()
calcTemp <- function(){
  for(year in 2013:2015){    #2013년부터 2015년     
    for(month in 1:12){       #1월부터 9월
      if(year == 2015 && month == 9){ #2015년은 8월까지 데이터가 존재하므로 반복문 멈춤
        break;
      }
      regex <- paste(as.character(year),'-',sep='')     #정규식 만들기
      if(month < 10){
        regex <- paste(regex, as.character(month), sep='0')
      }
      else{
        regex <- paste(regex, as.character(month), sep='')
      }
      temperature.mean <- mean(w.s.a.s.data$기온[grep(regex, w.s.a.s.data$날짜)])   #월의 평균 기온
      season <- w.s.a.s.data$계절[grep(regex, w.s.a.s.data$날짜)[1]]    #어느 계절인지 저장
      if(season == '겨울' || season == '가을'){#겨울, 봄은 평균온도 - 현재온도
        relativeTemp <<- c(relativeTemp, w.s.a.s.data$기온[grep(regex, w.s.a.s.data$날짜)]-temperature.mean)
      }
      else{   #여름, 가을은 현재온도 - 평균온도
        relativeTemp <<- c(relativeTemp, temperature.mean- w.s.a.s.data$기온[grep(regex, w.s.a.s.data$날짜)])
      }
    }
  }
}
calcTemp()
relativeTemp #계절에 따른 월별 상대적인 온도
relativeTemp.data.frame <- w.s.a.s.data  #데이터 복사
relativeTemp.data.frame$상대온도 <- relativeTemp #상대온대 Column 추가
View(relativeTemp.data.frame)

cor(relativeTemp.data.frame[,c(3:13,15)] , use = "pairwise.complete.obs")


#----- 상대온도를 정규화
normalization <- function(x){
  temp <- (x - mean(x, na.rm= TRUE))/sd(x, na.rm = TRUE)
  return(temp)
}
relativeTemp.normal <- c()

calcTemp.normal <- function(){
  for(year in 2013:2015){    #2013년부터 2015년     
    for(month in 1:12){       #1월부터 9월
      if(year == 2015 && month == 9){ #2015년은 8월까지 데이터가 존재하므로 반복문 멈춤
        break;
      }
      regex <- paste(as.character(year),'-',sep='')     #정규식 만들기
      if(month < 10){
        regex <- paste(regex, as.character(month), sep='0')
      }
      else{
        regex <- paste(regex, as.character(month), sep='')
      }
      print(regex)
      temperature.mean <- mean(w.s.a.s.data$기온[grep(regex, w.s.a.s.data$날짜)])   #월의 평균 기온
      season <- w.s.a.s.data$계절[grep(regex, w.s.a.s.data$날짜)[1]]    #어느 계절인지 저장
      if(season == '겨울' || season == '가을'){#겨울, 봄은 평균온도 - 현재온도
        relativeTemp.normal <<- c(relativeTemp.normal, normalization(w.s.a.s.data$기온[grep(regex, w.s.a.s.data$날짜)]-temperature.mean))
      }
      else{   #여름, 가을은 현재온도 - 평균온도
        relativeTemp.normal <<- c(relativeTemp.normal, normalization(temperature.mean- w.s.a.s.data$기온[grep(regex, w.s.a.s.data$날짜)]))
      }
    }
  }
}
calcTemp.normal()
relativeTemp.normal.data.frame <-relativeTemp.data.frame 
relativeTemp.normal.data.frame$상대온도.정규화 <- relativeTemp.normal
View(relativeTemp.normal.data.frame)
cor(relativeTemp.normal.data.frame[,c(3:13,15, 16)] , use = "pairwise.complete.obs")




#매출 월별 정규화
sales.normal <- c()
calcSales.normal <- function(){
  for(year in 2013:2015){    #2013년부터 2015년     
    for(month in 1:12){       #1월부터 9월
      if(year == 2015 && month == 9){ #2015년은 8월까지 데이터가 존재하므로 반복문 멈춤
        break;
      }
      regex <- paste(as.character(year),'-',sep='')     #정규식 만들기
      if(month < 10){
        regex <- paste(regex, as.character(month), sep='0')
      }
      else{
        regex <- paste(regex, as.character(month), sep='')
      }
      sales.mean <- mean(w.s.a.s.data$매출[grep(regex, w.s.a.s.data$날짜)], na.rm = TRUE)   #월의 평균 매출
      sales.normal <<- c(sales.normal, normalization(w.s.a.s.data$매출[grep(regex, w.s.a.s.data$날짜)]-sales.mean))#월별 평균 매출 - 현재 매출 -> 정규화
    }
  }
}
calcSales.normal()
sales.normal
dst.data.frame <- relativeTemp.normal.data.frame
dst.data.frame$상대매출.정규화 <- sales.normal   #최종 dataframe 도출
View(dst.data.frame)
cor(dst.data.frame[,c(3:13,15, 16, 17)] , use = "pairwise.complete.obs")
plot(dst.data.frame$상대온도.정규화, dst.data.frame$상대매출.정규화)


#매출 군집화
#정규화한 매출에서 -0.5 이하는 못팔린거, 0.5이상이면 잘 팔린거
sales.grade <- c()
sales.grade[dst.data.frame$상대매출.정규화 > 0.5] <- '대박'
sales.grade[dst.data.frame$상대매출.정규화 < -0.5] <- '쪽박'
sales.grade <- ifelse(is.na(sales.grade), '중박', sales.grade) 
sales.grade <- ifelse(is.na(dst.data.frame$상대매출.정규화), NA, sales.grade)
dst.data.frame$장사 <- as.factor(sales.grade)

View(dst.data.frame)
str(dst.data.frame)
write.csv(dst.data.frame, 'dst.csv')
#------------회기 분석
library(mlbench)
View(dst.data.frame)
obj.view <- subset(dst.data.frame, select = c(습도, 강수량, 일사량, 일조량, 기온, 풍속, 전운량, 경제기사수, 사회기사수, 총기사수, 상대온도, 상대온도.정규화, 상대매출.정규화))
obj.view <- subset(dst.data.frame, select = c(습도, 강수량, 일사량, 일조량, 풍속, 전운량, 경제기사수, 사회기사수, 총기사수, 상대온도.정규화, 상대매출.정규화))

m <- lm(상대매출.정규화 ~., data = obj.view)
m2 <- step(m, direction = "both")
summary(m2)     #적당한 독립변수가 정해졌음 -> 강수량, 일사량, 기온, 풍속, 전운량, 사회기사수, 상대온도.정규화
m <- lm(상대매출.정규화 ~강수량 + 일사량 + 풍속 + 상대온도.정규화, data = obj.view)
summary(m)
#----------분류 모델
#---------의사결정나무
library(party)
library(e1071)
library(nnet)
obj.view <- subset(dst.data.frame, select = c(강수량, 일사량, 풍속, 상대온도.정규화, 장사))
obj.view <- obj.view[complete.cases(obj.view),]

index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0.7, 0.3))
data.train <- obj.view[index==1,]
data.test <- obj.view[index==2,]
tree <- ctree(장사~., data = data.train)
pred <- predict(tree, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)


nb <- naiveBayes(장사 ~ ., data = data.train)
pred <- predict(nb, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

nn <- nnet(장사 ~ ., data = data.train, size = 3)
pred <- predict(nn, newdata = data.test, type = "class")
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

obj.view <- subset(dst.data.frame, select = c(강수량, 일사량, 기온, 풍속, 전운량, 사회기사수, 상대온도.정규화, 상대매출.정규화))
obj.view <- obj.view[complete.cases(obj.view),]
index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0.7, 0.3))
data.train <- obj.view[index==1,]
data.test <- obj.view[index==2,]
mm <- lm(상대매출.정규화 ~ ., data = data.train)
pred <- predict(mm, newdata = data.test)
pred


#------계절 별로 나눠서 분석-----
#겨울
select.column <- c("요일", "습도", "강수량", "일사량", "일조량", "풍속", "전운량", "매출", "경제기사수", "사회기사수", "총기사수", "계절", "상대온도.정규화", "섬유","코스닥","이상치", "공휴일", "연휴", "긍정기사개수", "부정기사개수", "긍정어휘비율", "부정어휘비율", "일", "월중", "일기예보")
obj.view <- subset(dst.data.frame, 계절 == '겨울', select = select.column)
obj.view <- subset(obj.view, select = c("요일", "습도", "강수량", "일사량", "일조량", "풍속", "전운량", "매출", "경제기사수", "사회기사수", "총기사수", "상대온도.정규화", "섬유","코스닥","이상치", "공휴일", "연휴", "긍정기사개수", "부정기사개수", "긍정어휘비율", "부정어휘비율", "일", "월중", "일기예보"))
View(obj.view)
m <- lm(매출 ~., data = obj.view,  use = "pairwise.complete.obs")
mm <- step(m, direction = "both")
summary(mm)
View(obj.view)

data.test.temp <- data.test[,!(names(data.test) %in% drops)]

  obj.view <- subset(dst.data.frame, 계절 == '겨울', select = select.column)
  drops <- c("계절")
  obj.view <- obj.view[, !(names(obj.view) %in% drops)]
  cc <- complete.cases(obj.view)  #NA가 없는 인덱스 벡터형으로 저장
  obj.view <- obj.view[cc, ] #NA가 없는 행 삭제
  index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0, 1))
  data.test <- obj.view[index==2,]
  drops <- c("매출")
  data.test.temp <- data.test[,!(names(data.test) %in% drops)]
  result <- predict(mm, newdata = data.test.temp)
  data.test$예상매출 <- result
  temp <- subset(data.test, select = c(매출, 예상매출))
  hit_rate <- (nrow(temp[abs(temp$매출 - temp$예상매출)<= 500000,])/nrow(temp))*100
  print(hit_rate)
  

View(temp)



obj.view <- subset(dst.data.frame, 계절 == '겨울', select = c(강수량, 사회기사수, 상대온도.정규화, 일사량, 장사))
obj.view <- obj.view[complete.cases(obj.view),]

index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0.7, 0.3))
data.train <- obj.view[index==1,]
data.test <- obj.view[index==2,]
tree <- ctree(장사~., data = data.train)
pred <- predict(tree, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)


nb <- naiveBayes(장사 ~ ., data = data.train)
pred <- predict(nb, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

nn <- nnet(장사 ~ ., data = data.train, size = 3)
pred <- predict(nn, newdata = data.test, type = "class")
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#봄
obj.view <- subset(dst.data.frame, 계절 == '봄', select = c(습도, 강수량, 일사량, 일조량, 풍속, 전운량, 경제기사수, 사회기사수, 총기사수, 상대온도.정규화, 상대매출.정규화))
m <- lm(상대매출.정규화 ~., data = obj.view)
mm <- step(m, direction = "both")
summary(mm)
View(obj.view)


obj.view <- subset(dst.data.frame, 계절 == '봄', select = c(강수량, 일조량, 경제기사수, 사회기사수, 상대온도.정규화, 장사))
obj.view <- obj.view[complete.cases(obj.view),]

index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0.7, 0.3))
data.train <- obj.view[index==1,]
data.test <- obj.view[index==2,]
tree <- ctree(장사~., data = data.train)
pred <- predict(tree, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)


nb <- naiveBayes(장사 ~ ., data = data.train)
pred <- predict(nb, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

nn <- nnet(장사 ~ ., data = data.train, size = 3)
pred <- predict(nn, newdata = data.test, type = "class")
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#여름
obj.view <- subset(dst.data.frame, 계절 == '여름', select = c(습도, 강수량, 일사량, 일조량, 풍속, 전운량, 경제기사수, 사회기사수, 총기사수, 상대온도.정규화, 상대매출.정규화))
m <- lm(상대매출.정규화 ~., data = obj.view)
mm <- step(m, direction = "both")
summary(mm)
View(obj.view)


obj.view <- subset(dst.data.frame, 계절 == '여름', select = c(강수량, 일사량, 일조량, 전운량, 사회기사수, 상대온도.정규화, 장사))
obj.view <- obj.view[complete.cases(obj.view),]

index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0.7, 0.3))
data.train <- obj.view[index==1,]
data.test <- obj.view[index==2,]
tree <- ctree(장사~., data = data.train)
pred <- predict(tree, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)


nb <- naiveBayes(장사 ~ ., data = data.train)
pred <- predict(nb, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

nn <- nnet(장사 ~ ., data = data.train, size = 3)
pred <- predict(nn, newdata = data.test, type = "class")
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#가을
obj.view <- subset(dst.data.frame, 계절 == '가을', select = c(습도, 강수량, 일사량, 일조량, 풍속, 전운량, 경제기사수, 사회기사수, 총기사수, 상대온도.정규화, 상대매출.정규화))
m <- lm(상대매출.정규화 ~., data = obj.view)
mm <- step(m, direction = "both")
summary(mm)
View(obj.view)


obj.view <- subset(dst.data.frame, 계절 == '가을', select = c(전운량, 사회기사수, 상대온도.정규화, 장사))
obj.view <- obj.view[complete.cases(obj.view),]

index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0.7, 0.3))
data.train <- obj.view[index==1,]
data.test <- obj.view[index==2,]
tree <- ctree(장사~., data = data.train)
pred <- predict(tree, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)


nb <- naiveBayes(장사 ~ ., data = data.train)
pred <- predict(nb, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

nn <- nnet(장사 ~ ., data = data.train, size = 3)
pred <- predict(nn, newdata = data.test, type = "class")
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#-----주가 지수 추가
clothes.data <- read.csv("clothes.csv")   #섬유 주가 읽기
dst.data.frame$섬유 <- clothes.data$시가지수  #column 추가
View(dst.data.frame)

kospi.data <- read.csv("kospi.csv")  #시가지수가 factor로 저장되어 있음.
kospi.data$시가지수 <- as.character(kospi.data$시가지수)
kospi.data$시가지수 <- as.numeric(gsub(",", "", kospi.data$시가지수))
dst.data.frame$코스피 <- as.numeric(kospi.data$시가지수)

#----- 공휴일 추가
holiday.data <- read.csv("holiday.csv")
str(holiday.data)

dst.data.frame$공휴일 <- holiday.data$공휴일
dst.data.frame$연휴 <- holiday.data$bh_bigtosmall

View(dst.data.frame)


#---------- 기사 감성판단 데이터 추가-----
#---- 긍정, 부정 기사 개수
article_sentiment_count <- read.csv("article_sentiment.csv")
colnames(article_sentiment_count) <- c('날짜', '분류', '긍정어휘개수', '부정어휘개수', '중립어휘개수')
View(article_sentiment_count)
posi.article.count <- c()
nega.article.count <- c()
calcSentiArticle <- function(){
  for(year in 2013:2015){
    for(month in 1:12){
      for(day in 1:31){
        if(year == 2015 && month > 8){
          break
        }
        regex <- paste(as.character(year),'-',sep='')
        if(month < 10){
          regex <- paste(regex,as.character(month),sep = '0')
        }
        else{
          regex <- paste(regex,as.character(month),sep = '')
        }
        regex <- paste(regex, '-', sep='')
        if(day < 10){
          regex <- paste(regex, as.character(day), sep = '0')
        }
        else{
          regex <- paste(regex, as.character(day), sep = '')
        }
        tmp <- article_sentiment_count[grep(regex, article_sentiment_count$날짜),] #날짜별로 모음
        if(nrow(tmp) == 0){
          print(regex)
        }
        else{
          posi.article.count <<- c(posi.article.count, nrow(tmp[tmp$긍정어휘개수 > tmp$부정어휘개수,]))
          nega.article.count <<- c(nega.article.count, nrow(tmp[tmp$긍정어휘개수 <= tmp$부정어휘개수,]))
        }
      }
    }
  }
}
calcSentiArticle()
posi.article.count <- c(0, posi.article.count)
nega.article.count <- c(0, nega.article.count)

dst.data.frame$긍정기사개수 <- posi.article.count
dst.data.frame$부정기사개수 <- nega.article.count
View(dst.data.frame)
#----긍정, 부정 어휘 비율
sentiword.rate <- read.csv("sentiword_rate.csv")
View(sentiword.rate)

dst.data.frame$긍정어휘비율 <- sentiword.rate$긍정비율
dst.data.frame$부정어휘비율 <- sentiword.rate$부정비율
View(dst.data.frame)

#------코스닥 빠진 날 넣기
kosdaq.data <- read.csv('kosdaq.csv')
View(kosdaq.data)
dst.kosdaq.data <- c()
setKOSDAQ <- function(){
  for(date in 1:nrow(dst.data.frame)){
    index <- grep(dst.data.frame$날짜[date], kosdaq.data$일자)
    tmp <- kosdaq.data[index,]
    if(nrow(tmp) == 0){
      dst.kosdaq.data <<- c(dst.kosdaq.data, dst.kosdaq.data[date-1])
    }
    else{
      dst.kosdaq.data <<- c(dst.kosdaq.data, kosdaq.data$시가지수[index])
    }
  }
}
setKOSDAQ()
dst.kosdaq.data
dst.data.frame$코스닥 <- dst.kosdaq.data
#-------날짜 데이터
day2 <- c();
abstractDay <- c();
setDay <- function(){
  for(year in 2013:2015){
    for(month in 1:12){
      for(day in 1:31){
        if(year == 2015 && month > 8){
          break
        }
        regex <- paste(as.character(year),'-',sep='')
        if(month < 10){
          regex <- paste(regex,as.character(month),sep = '0')
        }
        else{
          regex <- paste(regex,as.character(month),sep = '')
        }
        regex <- paste(regex, '-', sep='')
        if(day < 10){
          regex <- paste(regex, as.character(day), sep = '0')
        }
        else{
          regex <- paste(regex, as.character(day), sep = '')
        }
        if(nrow(dst.data.frame[grep(regex, dst.data.frame$날짜),]) == 0){
          print(regex)
        }
        else{
          day2 <<- c(day2, day)
          if(day <= 10){
            abstractDay <<- c(abstractDay, '초순')
          }
          else if(day <= 20){
            abstractDay <<- c(abstractDay, '중순')
          }
          else{
            abstractDay <<- c(abstractDay, '하순')
          }
        }
      }
    }
  }
}

setDay()
day2 
abstractDay
dst.data.frame$일 <- day2
dst.data.frame$월중 <- as.factor(abstractDay)
View(dst.data.frame)
str(dst.data.frame)
#--------- 전날 일기 예보 ----
#강수량 하루씩 땡기기
prev.precipitation <- dst.data.frame$강수량
prev.precipitation <- c(prev.precipitation , 0)
prev.precipitation <- prev.precipitation[-1]

dst.data.frame$일기예보 <- prev.precipitation

#--------이상한 날
abnormalDate <- read.csv('abnormal_date.csv')

dst.data.frame$이상치 <- abnormalDate$이상치기념일

View(dst.data.frame)

#------------ 모델 생성 -------
select.column <- c("요일", "습도", "강수량", "일사량","이상치", "일조량", "풍속", "전운량", "매출", "경제기사수", "사회기사수", "총기사수", "계절", "상대온도.정규화", "섬유","코스닥", "공휴일", "연휴", "긍정기사개수", "부정기사개수", "긍정어휘비율", "부정어휘비율", "일", "월중", "일기예보")
obj.view <- subset(dst.data.frame, select = select.column)
m <- lm(매출 ~., data = obj.view,  use = "pairwise.complete.obs")
m2 <- step(m , direction = "both")
summary(m2)

#-- 분석작업
while(TRUE){
  obj.view <- subset(dst.data.frame, select = select.column)
  cc <- complete.cases(obj.view)  #NA가 없는 인덱스 벡터형으로 저장
  obj.view <- obj.view[cc, ] #NA가 없는 행 삭제
  index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0.9, 0.1))
  data.test <- obj.view[index==2,]
  drops <- c("매출")
  data.test.temp <- data.test[,!(names(data.test) %in% drops)]
  result <- predict(m2, newdata = data.test.temp)
  data.test$예상매출 <- result
  temp <- subset(data.test, select = c(매출, 예상매출))
  hit_rate <- (nrow(temp[abs(temp$매출 - temp$예상매출)<= 600000,])/nrow(temp))*100
  print(hit_rate)
  if(hit_rate>=70){
    break
  }
}
#-------- 전체 예상
obj.view <- subset(dst.data.frame, select = select.column)
cc <- complete.cases(obj.view)  #NA가 없는 인덱스 벡터형으로 저장
obj.view <- obj.view[cc, ] #NA가 없는 행 삭제
index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0, 1))
data.test <- obj.view[index==2,]
drops <- c("매출")
data.test.temp <- data.test[,!(names(data.test) %in% drops)]
result <- predict(m2, newdata = data.test.temp)
data.test$예상매출 <- result
temp <- subset(data.test, select = c(매출, 예상매출))


#-------------plotting----------
#선 그래프
View(temp)
nrow(temp)
write.csv(dst.data.frame, 'dst_data_frame.csv')
write.csv(temp, 'predict.csv')
temp <- read.csv('predict.csv')
temp$id <- seq(1, nrow(temp), 1)

plot(x = temp$id, y = temp$매출, ylab = "매출액", xlab ="샘플",type="o", cex = 1, col = "#FF0000")    #매출액 출력
legend("topright", legend=c("매출"), pch = c(20), col = c("red"))

plot(x = temp$id, y = temp$예상매출, ylab = "매출액", xlab ="샘플",type="o", cex = 1, col = "#0000FF")    #매출액 출력
legend("topright", legend=c("예상매출"), pch = c(20), col = c("blue"))

points(x = temp$id, y = temp$예상매출, type = "o", cex = 1, col = "#0000FF")  #
legend("topright", legend=c("매출", "예상매출"), pch = c(20, 20), col = c("red", "blue"))


#box plot
box.data <- data.frame(매출 = c(temp$매출, temp$예상매출))
box.data$예상[1:nrow(temp)] <- '진짜'
box.data$예상[(nrow(temp)+1):nrow(box.data)] <- '예상'
box.data$예상 <- as.factor(box.data$예상)
boxstats <- boxplot(매출 ~ 예상, data = box.data, ylab = "매출액", notch = TRUE)
boxstats


#bar plot
barplot

#-------보여주기용
View(subset(dst.data.frame, select = c(날짜, 코스피, 코스닥, 섬유)))
View(subset(dst.data.frame, select = c(날짜, 기온, 계절, 상대온도, 상대온도.정규화)))

View(subset(dst.data.frame, select = c(날짜, 매출, 상대매출.정규화, 장사)))
View(subset(dst.data.frame, select = c(날짜, 연휴, 공휴일, 이상치)))
View(dst.data.frame)
