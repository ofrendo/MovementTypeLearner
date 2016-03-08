library(RSQLite)
library(geosphere)
library(data.table)
library(ggplot2)

options(scipen=999)

dbVersion <- 4
path <- paste0("data/V", dbVersion, "-Locations.db")

db <- dbConnect(SQLite(), path)
rows <- dbGetQuery(db, "SELECT * FROM locations")
setDT(rows)
invisible(dbDisconnect(db))
NROW(rows)

f3 <- "%Y-%m-%d %H:%M:%OS3"
rows$tsReal <- as.POSIXct(rows$ts, tz="GMT") # format(as.POSIXct(rows$ts, tz="GMT"), f3)

setDistanceSpeedToNext <- function(rowsParam) {
  n <- NROW(rowsParam)
  
  rowsParam[ , dsToNext := c(geosphere::distHaversine(matrix(c(long[1:(n-1)], lat[1:(n-1)]), ncol=2), 
                                                      matrix(c(long[2:n],     lat[2:n]), ncol=2)), -1)]
  rowsParam[, dtToNext := c(as.numeric(abs(rowsParam$tsReal[1:(n-1)] - rowsParam$tsReal[2:n])), NA)]
  
  rowsParam[, speedToNext := distanceToNext / dtToNext]                    
  rowsParam[, movingAverage := c(NA, NA, rollmean(rowsParam$speedToNext, k=5), NA, NA)]
  
  # Revise this calculation
  rowsParam[, accelerationToNext:=c(speedToNext[1:(n-1)] - speedToNext[2:n], NA) / c(dtToNext[1:(n-1)]+dtToNext[2:n], NA)]
  
  rowsParam
}


#=============== Visualization ================
ggplot(rows1, aes(x=tsReal, y=speedToNext)) + geom_line()
ggplot(rows1, aes(x=tsReal, y=movingAverage)) + geom_line()
ggplot(rows1, aes(x=tsReal, y=accelerationToNext)) + geom_line() + ylim(c(-30, 30))







