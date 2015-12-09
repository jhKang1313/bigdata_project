library(party)
library(e1071)
library(nnet)

select.column <- c("요일","월중", "습도", "강수량", "일사량", "일조량", "풍속", "전운량", "장사", "경제기사수", "사회기사수", "총기사수",
                   "상대온도.정규화", "섬유","코스닥", "공휴일", "연휴", "긍정기사개수", "부정기사개수", "긍정어휘비율", "부정어휘비율" )
obj.view <- subset(dst.data.frame, select = select.column)
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



