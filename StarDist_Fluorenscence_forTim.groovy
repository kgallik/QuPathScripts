/**
 * This script provides a general template for cell detection using StarDist in QuPath.
 * This example assumes you have fluorescence image, which has a channel called 'DAPI' 
 * showing nuclei.
 * 
 * If you use this in published work, please remember to cite *both*:
 *  - the original StarDist paper (https://doi.org/10.48550/arXiv.1806.03535)
 *  - the original QuPath paper (https://doi.org/10.1038/s41598-017-17204-5)
 *  
 * There are lots of options to customize the detection - this script shows some 
 * of the main ones. Check out other scripts and the QuPath docs for more info.
 */

import qupath.ext.stardist.StarDist2D
import qupath.lib.gui.dialogs.Dialogs
import qupath.lib.scripting.QP
import qupath.lib.analysis.features.ObjectMeasurements



//filtering parameters
def min_nuc_area = 10
nuc_area_measurement = 'Area µm^2'
def min_nuc_intensity = 4500
nuc_intensity_measurement = 'DAPI: Mean'
param_expansion = 5
def serverOriginal = getCurrentServer()
selectAnnotations()

// IMPORTANT! Replace this with the path to your StarDist model
// that takes a single channel as input (e.g. dsb2018_heavy_augment.pb)
// You can find some at https://github.com/qupath/models
// (Check credit & reuse info before downloading)
def modelPath = "/home/kristin.gallik/Desktop/StarDistModels/dsb2018_heavy_augment.pb"

// Customize how the StarDist detection should be applied
// Here some reasonable default options are specified
def stardist = StarDist2D
    .builder(modelPath)
    .channels('DAPI')            // Extract channel called 'DAPI'
    .normalizePercentiles(1, 99) // Percentile normalization
    .threshold(0.5)              // Probability (detection) threshold
    .pixelSize(0.5)              // Resolution for detection
//    .cellExpansion(5)            // Expand nuclei to approximate cell boundaries
    .measureShape()              // Add shape measurements
    .measureIntensity()          // Add cell measurements (in all compartments)
    .build()
	
// Define which objects will be used as the 'parents' for detection
// Use QP.getAnnotationObjects() if you want to use all annotations, rather than selected objects
def pathObjects = QP.getSelectedObjects()

// Run detection for the selected objects
def imageData = QP.getCurrentImageData()
if (pathObjects.isEmpty()) {
    QP.getLogger().error("No parent objects are selected!")
    return
}
stardist.detectObjects(imageData, pathObjects)
stardist.close() // This can help clean up & regain memory

def toDelete = getDetectionObjects().findAll {measurement(it, nuc_area_measurement) <= min_nuc_area}
removeObjects(toDelete, true)
def toDelete2 = getDetectionObjects().findAll {measurement(it, nuc_intensity_measurement) <= min_nuc_intensity}
removeObjects(toDelete2, true)


img_resolution=getCurrentImageData().getServer().getPixelCalibration().getAveragedPixelSizeMicrons() //Get the current image's resolution
def detections = getDetectionObjects()
def cells = CellTools.detectionsToCells(detections, param_expansion/img_resolution, -1)
clearCellMeasurements()
ObjectMeasurements.addShapeMeasurements(cells, serverOriginal.getPixelCalibration())
cells.each(p -> ObjectMeasurements.addIntensityMeasurements(serverOriginal, p, 1.0, 
                            ObjectMeasurements.Measurements.values() as List,
                            ObjectMeasurements.Compartments.values() as List))
removeObjects(detections, true)
addObjects(cells)
resolveHierarchy()
runObjectClassifier("Composite_Classifier");

createAnnotationsFromDensityMap("Dual Positive HotSpots", [0: 37.0], "EGFP: mCher")
findDensityMapHotspots("Dual Positive HotSpots", 0, 10, 37.000000, false, true)

println('Done!')


