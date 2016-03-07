library(RSQLite)
library(geosphere)
library(data.table)

options(scipen=999)

dbVersion <- 4
path <- paste0("data/V", dbVersion, "-Locations.db")

db <- dbConnect(SQLite(), path)
rows <- dbGetQuery(db, "SELECT * FROM locations")
setDT(rows)
invisible(dbDisconnect(db))
NROW(rows)

rows$tsReal <- as.POSIXct(rows$ts, tz="GMT")






setDistanceToNext <- function(rowsParam) {
  rowsParam$distanceToNext <- rep(-1, NROW(rowsParam))
  for (i in 1:(NROW(rowsParam)-1)) {
    
    rowsParam$distanceToNext[i] <- geosphere::distHaversine(c(rowsParam[i,]$long, rowsParam[i,]$lat), 
                                                            c(rowsParam[i+1,]$long, rowsParam[i+1,]$lat))

  }
  rowsParam
}