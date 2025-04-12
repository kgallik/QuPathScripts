clearDetections();
selectAnnotations();
createDetectionsFromPixelClassifier("DAB_pixel_classifier", 100.0, 50.0, "SPLIT");
selectDetections();
runPlugin('qupath.lib.algorithms.IntensityFeaturesPlugin', '{"pixelSizeMicrons":1.0,"region":"ROI","tileSizeMicrons":25.0,"colorOD":true,"colorStain1":true,"colorStain2":true,"colorStain3":true,"colorRed":false,"colorGreen":false,"colorBlue":false,"colorHue":true,"colorSaturation":true,"colorBrightness":true,"doMean":true,"doStdDev":true,"doMinMax":true,"doMedian":true,"doHaralick":true,"haralickDistance":1,"haralickBins":32}')
addShapeMeasurements("AREA", "LENGTH", "CIRCULARITY", "SOLIDITY", "MAX_DIAMETER", "MIN_DIAMETER", "NUCLEUS_CELL_RATIO")
runObjectClassifier("CellBodyInclusion_V2");