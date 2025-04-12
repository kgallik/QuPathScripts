/**
0.2.0
 * Script to help with annotating tumor regions, resulting in expansions out from the manually created tumor area (s).
 * Derived from the post at: https://forum.image.sc/t/reduce-annotations/24305/12
 * Here, each of the margin regions is approximately 100 microns in width.
 **************************************************************************
 *When starting this script, have one "Tissue" and one "Tumor" annotation.*
 **************************************************************************
 * @author Pete Bankhead
 * @editor Mike Nelson
 */
 
//Alternatively, automatically add one or both of the annotations.
//classifier = "Tissue"
//createAnnotationsFromPixelClassifier(classifier, 1000000.0, 0.0)

//-----
// Some things you might want to change

// How much to expand each region
double expandMarginMicrons = 100.0
// How many times you want to chop into your annotation. Edit color script around line 115 if you go over 5
int howManyTimes = 1
// Define the colors
// Inner layers are given scripted colors, but gretaer than 6 or 7 layers may require adjustments
def colorOuterMargin = getColorRGB(0, 200, 0)



// Extract the main info we need
def imageData = getCurrentImageData()
def hierarchy = imageData.getHierarchy()
def server = imageData.getServer()
// We need the pixel size
def cal = server.getPixelCalibration()
if (!cal.hasPixelSizeMicrons()) {
  print 'We need the pixel size information here!'
  return
}


// Choose whether to lock the annotations or not (it's generally a good idea to avoid accidentally moving them)
def lockAnnotations = true
PrecisionModel PM = new PrecisionModel(PrecisionModel.FIXED)
//-----
//Setup - Merge all Tumor objects into one, they can be split later. Get Geometries for each object
selectObjectsByClassification("Hole Punch")
mergeSelectedAnnotations()
double expandPixels = expandMarginMicrons / cal.getAveragedPixelSizeMicrons()
initialTumorObject = getAnnotationObjects().find{it.getPathClass() == getPathClass("Hole Punch")}
def tumorGeom = getAnnotationObjects().find{it.getPathClass() == getPathClass("Hole Punch")}.getROI().getGeometry()
def plane = ImagePlane.getDefaultPlane()
def tissueGeom = getAnnotationObjects().find{it.getPathClass() == getPathClass("Tissue")}.getROI().getGeometry()

//Clean up the Tumor geometry
cleanTumorGeom = tissueGeom.intersection(tumorGeom)
tumorROIClean = GeometryTools.geometryToROI(cleanTumorGeom, plane)


cleanTumor = PathObjects.createAnnotationObject(tumorROIClean, getPathClass("Hole Punch"))
cleanTumor.setName("Hole Punch")

//Create a list of objects we need to add back in at the end, keep adding to it as we go proceed
annotationsToAdd = []
annotationsToAdd << cleanTumor
/*
addObject(cleanTumor)*/

for (i=0; i<howManyTimes;i++){
    currentArea = annotationsToAdd[annotationsToAdd.size()-1].getROI().getGeometry()
    println(currentArea)
    //Expand from the current area, starting with the tumor
    areaExpansion = currentArea.buffer(expandPixels)
    //Clip off anything outside of the tissue
    areaExpansion = areaExpansion.intersection(tissueGeom)
    //Remove anything that intersects with the tumor
    areaExpansion = areaExpansion.buffer(0)
    areaExpansion = areaExpansion.difference(cleanTumorGeom)
    //If we have already expanded once, include the prevous geometry in the exclusion
    if(i>=1){
            for (k=1; k<=i;k++){
            remove = annotationsToAdd[annotationsToAdd.size()-k].getROI().getGeometry()
            areaExpansion = areaExpansion.difference(remove)
         
            }
    }   
        areaExpansion= GeometryPrecisionReducer.reduce(areaExpansion, PM)
    roiExpansion = GeometryTools.geometryToROI(areaExpansion, plane)
    j = i+1
    int nameValue = j*expandMarginMicrons
    annotationExpansion = PathObjects.createAnnotationObject(roiExpansion, getPathClass(nameValue.toString()))

    annotationExpansion.setName("Margin "+nameValue+" microns")
    annotationExpansion.setColorRGB(getColorRGB(50*j, 40*j, 200-30*j))
    annotationsToAdd << annotationExpansion

}
//remainingTissueGeom = tissueGeom.difference(cleanTumorGeom)
//annotationsToAdd.each{
//    remainingTissueGeom = remainingTissueGeom.difference(it.getROI().getGeometry())
//}
//remainingTissueROI = GeometryTools.geometryToROI(remainingTissueGeom, plane)
//remainingTissue = PathObjects.createAnnotationObject(remainingTissueROI)
//remainingTissue.setName("Other Tissue")
//remainingTissue.setPathClass(getPathClass("Other Tissue"))
//addObject(remainingTissue)

// Add the annotations

addObjects(annotationsToAdd)
removeObject(initialTumorObject, true)
resetSelection()
fireHierarchyUpdate()
println("Done! Wheeeee!")

import org.locationtech.jts.geom.Geometry
import qupath.lib.common.GeneralTools
import qupath.lib.objects.PathObject
import qupath.lib.objects.PathObjects
import qupath.lib.roi.GeometryTools
import qupath.lib.roi.ROIs
import org.locationtech.jts.precision.GeometryPrecisionReducer
import org.locationtech.jts.geom.PrecisionModel
import java.awt.Rectangle
import java.awt.geom.Area