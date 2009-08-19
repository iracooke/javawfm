
pointsInPolygons <- function(points,polygons){ # Like overlay but it returns the actual objects not indices
  points[which(!is.na(overlay(points,polygons))),]
}

cleanupSoilsAndRainfall <- function(soilsrf,polygons){ # Removes points with little or no data and overlays the polygons
  exclude=which(soilsrf@data$OTH>0.2 | is.na(soilsrf@data$RF))
  soilsrf=soilsrf[-exclude,]
  pointsInPolygons(soilsrf,polygons)
}


arableArea <- function(datum){ # Calculates arable area for an FBS farm datum
  totalArea=datum$utilizedArea
  arableArea=datum$totalMainSetAside
  arableArea/totalArea
}

lowlandArable <- function(data,arableThreshold,altitudeThreshold){ # Filters an FBS dataframe to lowland arable farms only
  aas=arableArea(data)
  arableI=which(aas>=arableThreshold & is.finite(aas) )
  altI=which(data$altitude <= altitudeThreshold)
  allI=intersect(altI,arableI)
  data[allI,]
}

getLowlandFarmsInCounties <- function(data,countypolygons,soilandrainfall,arableThreshold=0.85,altitudeThreshold=1){
  print("Getting Lowland Farms")
  lowland=lowlandArable(data,arableThreshold,altitudeThreshold)
  farmdata=data.frame()
  for(i in 1:length(lowland)){ # Prepare the farmdata
    county=lowland[i,]$county
    pi=which(countypolygons@data[,"LABEL"]==county)
    if ( length(pi)>0){
      ppt=pointsInPolygons(soilandrainfall,countypolygons[pi,]) # Just one polygon actually
      farmdatum=data.frame(lowland[i,],t(data.frame(colMeans(ppt@data,na.rm=TRUE))))
      if ( !is.na(sum(farmdatum)) ){ # Only proceed if all the data is OK
        farmdata=rbind(farmdatum,farmdata)
      }
    }
  }
  farmdata
}
