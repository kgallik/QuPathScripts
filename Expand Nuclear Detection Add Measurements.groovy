import qupath.lib.analysis.features.ObjectMeasurements
def serverOriginal = getCurrentServer()
param_expansion = 5
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