

// SET ME! Delete existing objects
def deleteExisting = false

// SET ME! Change this if things end up in the wrong place
def createInverse = true

import qupath.lib.objects.PathCellObject
import qupath.lib.objects.PathDetectionObject
import qupath.lib.objects.PathObject
import qupath.lib.objects.PathObjects
import qupath.lib.objects.PathTileObject
import qupath.lib.roi.RoiTools
import qupath.lib.roi.interfaces.ROI

import java.awt.geom.AffineTransform

import static qupath.lib.gui.scripting.QPEx.*

path = buildFilePath('D:/Haab Lab/Alfredo/Tumor_glycan_CellPose_Test', 'Affine')

new File(path).eachFile { f ->
    f.withObjectInputStream {
        matrix = it.readObject()

        def name = getProjectEntry().getImageName()

        // Get the project & the requested image name
        def project = getProject()
        def entry = project.getImageList().find { it.getImageName() == f.getName() }
        if (entry == null) {
            print 'Could not find image with name ' + f.getName()
            return
        }

        def otherHierarchy = entry.readHierarchy()
        def pathObjects = otherHierarchy.getAnnotationObjects()

        // Define the transformation matrix
        def transform = new AffineTransform(
                matrix[0], matrix[3], matrix[1],
                matrix[4], matrix[2], matrix[5]
        )
        if (createInverse)
            transform = transform.createInverse()

        if (deleteExisting)
            clearAllObjects()

        def newObjects = []
        for (pathObject in pathObjects) {
            newObjects << transformObject(pathObject, transform, pathObject.getDetectionAttribute("original_object_id"))
        }
        addObjects(newObjects)
    }
}
print 'Done!'

/**
 * Transform object, recursively transforming all child objects
 *
 * @param pathObject
 * @param transform
 * @param originalObjectId
 * @return
 */
PathObject transformObject(PathObject pathObject, AffineTransform transform, originalObjectId) {
    // Create a new object with the converted ROI
    def roi = pathObject.getROI()
    def roi2 = transformROI(roi, transform)
    def newObject = null
    if (pathObject instanceof PathCellObject) {
        def nucleusROI = pathObject.getNucleusROI()
        if (nucleusROI == null)
            newObject = PathObjects.createCellObject(roi2, pathObject.getPathClass(), pathObject.getMeasurementList())
        else
            newObject = PathObjects.createCellObject(roi2, transformROI(nucleusROI, transform), pathObject.getPathClass(), pathObject.getMeasurementList())
    } else if (pathObject instanceof PathTileObject) {
        newObject = PathObjects.createTileObject(roi2, pathObject.getPathClass(), pathObject.getMeasurementList())
    } else if (pathObject instanceof PathDetectionObject) {
        newObject = PathObjects.createDetectionObject(roi2, pathObject.getPathClass(), pathObject.getMeasurementList())
    } else {
        newObject = PathObjects.createAnnotationObject(roi2, pathObject.getPathClass(), pathObject.getMeasurementList())
    }
    // Set original object ID as detection attribute
    newObject.addDetectionAttribute("original_object_id", originalObjectId)

    // Handle child objects
    if (pathObject.hasChildren()) {
        newObject.addPathObjects(pathObject.getChildObjects().collect({ transformObject(it, transform, it.getDetectionAttribute("original_object_id")) }))
    }
    return newObject
}

/**
 * Transform ROI (via conversion to Java AWT shape)
 *
 * @param roi
 * @param transform
 * @return
 */
ROI transformROI(ROI roi, AffineTransform transform) {
    def shape = RoiTools.getShape(roi) // Should be able to use roi.getShape() - but there's currently a bug in it for rectangles/ellipses!
    shape2 = transform.createTransformedShape(shape)
    return RoiTools.getShapeROI(shape2, roi.getImagePlane(), 0.5)
}
