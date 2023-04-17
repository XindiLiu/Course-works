library("TH.data")
library(car)
library(leaps)
library(latex2exp)
library(caret)
library(glmnet)
data("bodyfat")
bodyfat <- bodyfat[!(row.names(bodyfat) %in% c("73")),]
bodyfat$DEXfat <- log(log(bodyfat$DEXfat))
model <- lm(DEXfat ~ ., data=bodyfat)
par(mfrow=c(1, 2), mar=c(5, 5, 4, 2) + 0.1, oma=c(0,0,0,0), cex.axis=2, cex.lab=2, pch=19, col=rgb(0, 0, 0, 0.3))
qqnorm(rstudent(model))
qqline(rstudent(model))
plot(model$fitted.values, model$residuals, xlab=TeX(r'($\hat{y}$)'), ylab="residuals")

# added variable
avPlots(model)
par(mfrow=c(1, 1))
#par(mfrow = c(3, 3))
#for (i in 1:length(bodyfat)){
#  if(i!=2){
#    partialmodel <- lm(DEXfat ~ . -names(bodyfat)[i] , data=bodyfat)
#    plot(bodyfat[,i], partialmodel$residuals, xlab = names(bodyfat)[i], ylab = "residuals")
#  }
#}

# outliner
n <- nrow(bodyfat)
p <- ncol(bodyfat)
leverage.cutoff <- 2 * p / n  # MPV p. 213
# MPV p. 215
dfbetas.cutoff <- 2 / sqrt(n)  # MPV p. 218
dffits.cutoff <- 2 * sqrt(p / n)  # MPV p. 219
studres.cutoff <- qt(0.05 / 2, n - p, lower.tail = FALSE)  # MPV p. 135

cooks <- cooks.distance(model)
cooks[cooks > cooks.cutoff]

dfbetas <- dfbetas(model)
for (i in 1:length(bodyfat)){
  if(i!=2){
    print(dfbetas[abs(dfbetas[, i]) > dfbetas.cutoff, i])
  }
}

dffits <- dffits(model)
dffits[abs(dffits) > dffits.cutoff]

hat <- hatvalues(model)
hat[hat > leverage.cutoff]


full.model <- regsubsets(subset(bodyfat, select=-c(DEXfat)), bodyfat$DEXfat, nbest=1, nvmax = 9)
fs <- summary(full.model)

ctrl <- trainControl(method = "cv", number = 5, savePredictions='all')
k<-5
adjr2 <- integer(length(fs$rss))
mse<-integer(length(fs$rss))
models<-list()
for (i in 2:length(fs$which[,1])){
  vars <- names(which(fs$which[i,]==TRUE))[-1] 
  formula <- as.formula(paste0("DEXfat~ ", paste(vars, collapse=" + ")))
  folds <- createFolds(bodyfat$DEXfat, k = k, list = TRUE, returnTrain = FALSE)
  adjr2v <- integer(k)
  msev <- integer(k)
  for (j in 1:k){
    mod <- lm(formula, data=bodyfat[-folds[[j]],])
    yhat <- predict(mod, newdata = bodyfat[folds[[j]],vars])
    # mod <- cv.glmnet(as.matrix(bodyfat[-folds[[j]],vars]), as.numeric(bodyfat[-folds[[j]], "DEXfat"]), alpha=1, lambda = seq(.1,10,.1))
    # yhat <- predict(mod, newx = as.matrix(bodyfat[folds[[j]],vars]))
    # 
    SSres <- sum((bodyfat[folds[[j]],]$DEXfat - yhat)^2)
    SStot <- sum((bodyfat[folds[[j]],]$DEXfat -  mean(bodyfat[folds[[j]],]$DEXfat))^2)
    numobs <-length(folds[[j]])
    r2 = 1-(SSres/SStot)
    # adjr2v[j] <- 1 - ((SSres / (numobs-(length(vars)+1))) / (SStot/(numobs - 1)))
    adjr2v[j] <- 1 - (1-r2)*((numobs - 1)/(numobs-length(vars)-1))
    msev[j] <- SSres / numobs
  }
  adjr2[i]<-mean(adjr2v)
  mse[i] <-mean(msev)
  # modtemp <- cv.glmnet(as.matrix(bodyfat[,vars]), as.numeric(bodyfat$DEXfat), alpha=1, lambda = seq(.1,10,.1))
  # models[[i]] <-modtemp
  # if (i>=2) {
  #   best <- lm(formula, data=bodyfat)
  # }
}
par(mfrow=c(2, 2),mar=c(5, 6, 2, 1) + 0.1)
indice<- seq(4,25)
plot(rownames(fs$which[indice,]), adjr2[indice], xlab="Number of variables", ylab=TeX(r'(Adjusted $R^2$)'),type = "b")
plot(rownames(fs$which[indice,]), mse[indice], xlab="Number of variables", ylab="MSE",type = "b")
plot(rownames(fs$which[indice,]), fs$cp[indice], xlab="Number of variables", ylab=TeX(r'(Mallows’ $C_p$)'),type = "b")
plot(rownames(fs$which[indice,]), fs$bic[indice], xlab="Number of variables", ylab="BIC",type = "b")

vars <- names(which(fs$which[16,]==TRUE))[-1] 
formula <- as.formula(paste0("DEXfat~ ", paste(vars, collapse=" + ")))
best <- lm(formula, data=bodyfat)
best0 <- cv.glmnet(as.matrix(bodyfat[,vars]), as.numeric(bodyfat$DEXfat), alpha=0, lambda = seq(.001,10,.005))
best1 <- cv.glmnet(as.matrix(bodyfat[,vars]), as.numeric(bodyfat$DEXfat), alpha=1, lambda = seq(.001,10,.005))
vif(best)
ag0 = assess.glmnet(best0, as.matrix(bodyfat[,vars]), as.numeric(bodyfat$DEXfat))
ag1 = assess.glmnet(best1, as.matrix(bodyfat[,vars]), as.numeric(bodyfat$DEXfat))


par(mfrow=c(1, 2), mar=c(5, 5, 4, 2) + 0.1, oma=c(0,0,0,0), cex.axis=2, cex.lab=2, pch=19, col=rgb(0, 0, 0, 0.3))
qqnorm(rstudent(best1))
qqline(rstudent(best1))
plot(model$fitted.values, model$residuals, xlab=TeX(r'($\hat{y}$)'), ylab="residuals")

# totit <- 10
# coefs<-matrix(nrow = totit, ncol=8)
# colnames(coefs) = c(names(which(fs$which[16,]==TRUE)), "lambda")
# data<-bodyfat[,c(vars, "DEXfat")]
# for (i in 1:totit){
#   mod <- cv.glmnet(as.matrix(data[,vars]), as.numeric(data$DEXfat), alpha=1, lambda = seq(.001,10,.5))
#   coefs[i,1:7]<-as.matrix(coef(mod))
#   coefs[i,8]<-mod$lambda.1se
#   yhat <- predict(mod, newx = as.matrix(data[,vars]))
#   res <- data$DEXfat - yhat
#   data$DEXfat <- yhat + res[sample(nrow(data), replace=T)]
# }

vars <- c( "age", "waistcirc", "hipcirc", "kneebreadth", "anthro3b", "anthro3c")
formula <- as.formula(paste0("DEXfat~ ", paste(vars[-5], collapse=" + ")))
best <- lm(formula, data=bodyfat)
formula <- as.formula(paste0("DEXfat~ ", paste(vars[-6], collapse=" + ")))
best1 <- lm(formula, data=bodyfat)
vars <- c( "age", "waistcirc", "kneebreadth", "anthro3c")
best3 <- lm(formula, data=bodyfat)


bootstrap <- Boot(best1, method="residual") 
confint(bootstrap)


vars <- c( "age", "waistcirc", "hipcirc", "kneebreadth", "anthro3b", "anthro3c")
formula <- as.formula(paste0("DEXfat~ ", paste(vars[-1], collapse=" + ")))
remove.age <- lm(formula, data=bodyfat)
formula <- as.formula(paste0("DEXfat~ ", paste(vars[-4], collapse=" + ")))
remove.age <- lm(formula, data=bodyfat)
formula <- as.formula(paste0("DEXfat~ ", paste(vars[-1-4], collapse=" + ")))
remove.both <- lm(formula, data=bodyfat)
