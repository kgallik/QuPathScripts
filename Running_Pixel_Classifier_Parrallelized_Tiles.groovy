def imageData = getCurrentImageData()
def classifier = loadPixelClassifier('Mammary_Gland_Pixel_Classifier_V4')
def classifierServer = PixelClassifierTools.createPixelClassificationServer(imageData, classifier)

classifierServer.getTileRequestManager().getAllTileRequests()
    .parallelStream()
    .forEach { classifierServer.readBufferedImage(it.getRegionRequest()) }
selectAnnotations()
addPixelClassifierMeasurements("Mammary_Gland_Pixel_Classifier_V4", "Mammary_Gland_Pixel_Classifier_V4")