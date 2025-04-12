double XY = 200
double size = 1024
def plane = ImagePlane.getDefaultPlane()
def roi = ROIs.createRectangleROI(XY, XY, size, size, plane)
def annotation = PathObjects.createAnnotationObject(roi)
addObject(annotation)