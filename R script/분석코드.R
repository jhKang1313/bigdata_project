
setwd("C:/Users/Jinhyuk/Documents")   #working directory 설정
dst.data.frame <- read.csv("dst_data_frame.csv")

#---------- 분석
#----------자동분류
library(party)
library(e1071)
library(nnet)
select.column <- c('요일', '습도', '강수량', '일사량', '일조량', '기온', '풍속', '전운량', 
                   '경제기사수','사회기사수', '총기사수', '계절', '상대온도', '상대온도.정규화',
                   '장사', '섬유', '코스피','코스닥', '공휴일', '연휴', '긍정기사개수',
                   '부정기사개수', '긍정어휘비율', '부정어휘비율','일', '월중', '일기예보')

obj.view <- subset(dst.data.frame, select = select.column)
obj.view <- obj.view[complete.cases(obj.view),]

index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0.7, 0.3))
data.train <- obj.view#[index==1,]
data.test <- obj.view#[index==2,]

#의사결정나무
tree <- ctree(장사~., data = data.train)
pred <- predict(tree, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)


#나이브 베이지안
nb <- naiveBayes(장사 ~ ., data = data.train)
pred <- predict(nb, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#뉴럴 네트워크
nn <- nnet(장사 ~ ., data = data.train, size = 3)
pred <- predict(nn, newdata = data.test, type = "class")
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)


#------계절 별로 나눠서 분석-----
#------------------------------겨울
obj.view <- subset(dst.data.frame, 계절 == '겨울', select = select.column)
obj.view <- obj.view[complete.cases(obj.view),]
drops <- c("계절")
obj.view <- obj.view[, !(names(obj.view) %in% drops)]

index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0.7, 0.3))
data.train <- obj.view[index==1,]
data.test <- obj.view[index==2,]

#의사결정나무
tree <- ctree(장사~., data = data.train)
pred <- predict(tree, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#나이브 베이지안
nb <- naiveBayes(장사 ~ ., data = data.train)
pred <- predict(nb, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#뉴럴 네트워크
nn <- nnet(장사 ~ ., data = data.train, size = 3)
pred <- predict(nn, newdata = data.test, type = "class")
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#---------------------------봄
obj.view <- subset(dst.data.frame, 계절 == '봄', select = select.column)
obj.view <- obj.view[complete.cases(obj.view),]
drops <- c("계절")
obj.view <- obj.view[, !(names(obj.view) %in% drops)]

index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0.7, 0.3))
data.train <- obj.view[index==1,]
data.test <- obj.view[index==2,]

#의사결정나무
tree <- ctree(장사~., data = data.train)
pred <- predict(tree, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#나이브 베이지안
nb <- naiveBayes(장사 ~ ., data = data.train)
pred <- predict(nb, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#뉴럴 네트워크
nn <- nnet(장사 ~ ., data = data.train, size = 3)
pred <- predict(nn, newdata = data.test, type = "class")
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#----------------------------------여름
obj.view <- subset(dst.data.frame, 계절 == '여름', select = select.column)
obj.view <- obj.view[complete.cases(obj.view),]
drops <- c("계절")
obj.view <- obj.view[, !(names(obj.view) %in% drops)]

index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0.7, 0.3))
data.train <- obj.view[index==1,]
data.test <- obj.view[index==2,]

#의사결정나무
tree <- ctree(장사~., data = data.train)
pred <- predict(tree, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#나이브 베이지안
nb <- naiveBayes(장사 ~ ., data = data.train)
pred <- predict(nb, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#뉴럴 네트워크
nn <- nnet(장사 ~ ., data = data.train, size = 3)
pred <- predict(nn, newdata = data.test, type = "class")
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#---------------------------------- 가을
obj.view <- subset(dst.data.frame, 계절 == '가을', select = select.column)
obj.view <- obj.view[complete.cases(obj.view),]
drops <- c("계절")
obj.view <- obj.view[, !(names(obj.view) %in% drops)]

index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0.7, 0.3))
data.train <- obj.view[index==1,]
data.test <- obj.view[index==2,]

#의사결정나무
tree <- ctree(장사~., data = data.train)
pred <- predict(tree, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#나이브 베이지안
nb <- naiveBayes(장사 ~ ., data = data.train)
pred <- predict(nb, data.test)
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)

#뉴럴 네트워크
nn <- nnet(장사 ~ ., data = data.train, size = 3)
pred <- predict(nn, newdata = data.test, type = "class")
conf.mat <- table(pred, data.test$장사)
conf.mat
(accuracy <- sum(diag(conf.mat))/sum(conf.mat) * 100)


#회귀 분석
#------------ 모델 생성 -------
select.column <- c("요일", "습도", "강수량", "일사량","이상치", "일조량", "풍속", "전운량", "매출", 
                   "경제기사수", "사회기사수", "총기사수", "계절", "상대온도.정규화", "섬유","코스닥",
                   "공휴일", "연휴", "긍정기사개수", "부정기사개수", "긍정어휘비율", "부정어휘비율",
                   "일", "월중", "일기예보", "이상치")
obj.view <- subset(dst.data.frame, select = select.column)
m <- lm(매출 ~., data = obj.view,  use = "pairwise.complete.obs")
m2 <- step(m , direction = "both")
summary(m2)

#-------- 전체 매출 예상
obj.view <- subset(dst.data.frame, select = select.column)
#cc <- complete.cases(obj.view)  #NA가 없는 인덱스 벡터형으로 저장
#obj.view <- obj.view[cc, ] #NA가 없는 행 삭제
#index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0, 1))
data.test <- obj.view#[index==2,]
drops <- c("매출")
data.test.temp <- data.test[,!(names(data.test) %in% drops)]
result <- predict(m2, newdata = data.test.temp)
data.test$예상매출 <- result
predict <- subset(data.test, select = c(매출, 예상매출))
predict$날짜 <- dst.data.frame$날짜

#--------- 분석 결과 출력
View(predict)
write.csv(predict, 'predict.csv')











#----------계절별 분석-----
#-----------겨울
select.column <- c("요일", "습도", "강수량", "일사량","이상치", "일조량", "풍속", "전운량", "매출", 
                   "경제기사수", "사회기사수", "총기사수", "계절", "상대온도.정규화", "섬유","코스닥",
                   "공휴일", "연휴", "긍정기사개수", "부정기사개수", "긍정어휘비율", "부정어휘비율",
                   "일", "월중", "일기예보", "이상치")
obj.view <- subset(dst.data.frame, 계절 == '겨울', select = select.column)
drops <- c("계절")
obj.view <- obj.view[, !(names(obj.view) %in% drops)]
View(obj.view)
m <- lm(매출 ~., data = obj.view,  use = "pairwise.complete.obs")
m2 <- step(m , direction = "both")
summary(m2)

#-------- 전체 매출 예상
index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0, 1))
data.test <- obj.view[index==2,]
drops <- c("매출")
data.test.temp <- data.test[,!(names(data.test) %in% drops)]
result <- predict(m2, newdata = data.test.temp)
data.test$예상매출 <- result
predict <- subset(data.test, select = c(매출, 예상매출))

hit_rate <- (nrow(predict[abs(predict$매출 - predict$예상매출)<= 600000,])/nrow(predict))*100
print(hit_rate)
View(predict)


#-----------봄
obj.view <- subset(dst.data.frame, 계절 == '봄', select = select.column)
drops <- c("계절")
obj.view <- obj.view[, !(names(obj.view) %in% drops)]
m <- lm(매출 ~., data = obj.view,  use = "pairwise.complete.obs")
m2 <- step(m , direction = "both")
summary(m2)

#-------- 전체 매출 예상
index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0, 1))
data.test <- obj.view[index==2,]
drops <- c("매출")
data.test.temp <- data.test[,!(names(data.test) %in% drops)]
result <- predict(m2, newdata = data.test.temp)
data.test$예상매출 <- result
predict <- subset(data.test, select = c(매출, 예상매출))

hit_rate <- (nrow(predict[abs(predict$매출 - predict$예상매출)<= 600000,])/nrow(predict))*100
print(hit_rate)
View(predict)

#-----------여름
obj.view <- subset(dst.data.frame, 계절 == '여름', select = select.column)
drops <- c("계절")
obj.view <- obj.view[, !(names(obj.view) %in% drops)]
m <- lm(매출 ~., data = obj.view,  use = "pairwise.complete.obs")
m2 <- step(m , direction = "both")
summary(m2)

#-------- 전체 매출 예상
index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0, 1))
data.test <- obj.view[index==2,]
drops <- c("매출")
data.test.temp <- data.test[,!(names(data.test) %in% drops)]
result <- predict(m2, newdata = data.test.temp)
data.test$예상매출 <- result
predict <- subset(data.test, select = c(매출, 예상매출))

hit_rate <- (nrow(predict[abs(predict$매출 - predict$예상매출)<= 600000,])/nrow(predict))*100
print(hit_rate)
View(predict)

#-----------가을
obj.view <- subset(dst.data.frame, 계절 == '가을', select = select.column)
drops <- c("계절")
obj.view <- obj.view[, !(names(obj.view) %in% drops)]
m <- lm(매출 ~., data = obj.view,  use = "pairwise.complete.obs")
m2 <- step(m , direction = "both")
summary(m2)

#-------- 전체 매출 예상
index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0, 1))
data.test <- obj.view[index==2,]
drops <- c("매출")
data.test.temp <- data.test[,!(names(data.test) %in% drops)]
result <- predict(m2, newdata = data.test.temp)
data.test$예상매출 <- result
predict <- subset(data.test, select = c(매출, 예상매출))

hit_rate <- (nrow(predict[abs(predict$매출 - predict$예상매출)<= 600000,])/nrow(predict))*100
print(hit_rate)
View(predict)



