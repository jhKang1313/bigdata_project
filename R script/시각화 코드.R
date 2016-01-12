setwd("C:/Users/Jinhyuk/Documents")   #working directory 설정
predict <- read.csv("predict.csv")

#-------------plotting----------
#선 그래프
View(predict)
index <- sample(2, nrow(obj.view), replace = TRUE, prob = c(0.9, 0.1))
predict.sample <- predict[index==2,]

predict.sample$id <- seq(1, nrow(predict.sample), 1)
png("C:/Users/Jinhyuk/Documents/Plot.png")  #이미지 파일 열어서 생성
plot(x = predict.sample$id, y = predict.sample$매출, ylab = "매출액", xlab ="샘플",type="o", cex = 1, col = "#FF0000")    #매출액 출력
points(x = predict.sample$id, y = predict.sample$예상매출, type = "o", cex = 1, col = "#0000FF")  #
legend("topright", legend=c("매출", "예상매출"), pch = c(20, 20), col = c("red", "blue"))
dev.off()     #이미지 파일 닫기

#box plot

box.data <- data.frame(매출 = c(predict.sample$매출, predict.sample$예상매출))
box.data$예상[1:nrow(predict.sample)] <- '진짜'
box.data$예상[(nrow(predict.sample)+1):nrow(box.data)] <- '예상'
box.data$예상 <- as.factor(box.data$예상)
png("C:/Users/Jinhyuk/Documents/BoxPlot.png")
boxstats <- boxplot(매출 ~ 예상, data = box.data, ylab = "매출액", notch = TRUE)
boxstats
dev.off()
