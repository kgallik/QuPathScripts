//In this case, it will add the total area taken up by Positive class cells within each annotation to their parent
//annotation as "Positive Area"

import qupath.lib.objects.PathDetectionObject

hierarchy = getCurrentHierarchy()

for (annotation in getAnnotationObjects()){
    //Block 1
    def CBI = PathObjectTools.getDescendantObjects(annotation,null, PathDetectionObject)
    .findAll{it.getPathClass() == getPathClass("CellBodyInclusion")}
    double totalArea = 0
    for (def cell in CBI){
        totalArea += cell.getMeasurementList().getMeasurementValue("Area µm^2")
    }
    //Comment the following in or out depending on whether you want to see the output
    //println("Mean area for Positive is: " + totalArea/positiveCells.size)
    //println("Total Positive Area is: " + totalArea)
    
    //Add the total as "Positive Area" to each annotation.
    annotation.getMeasurementList().putMeasurement("CellBodyInclusionArea µm^2", totalArea)
    def annotationArea = annotation.getROI().getArea()
    annotation.getMeasurementList().putMeasurement("CellBodyInclusionArea %", totalArea/annotationArea*100)
    //Block 2 - add as many blocks as you have classes
    //repeat above
}