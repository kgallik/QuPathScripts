import qupath.lib.geom.GeometryTools
import qupath.lib.geom.Point2
import qupath.lib.geom.Polygon2
import qupath.lib.images.ImageData
import qupath.lib.images.ImagePlane
import qupath.lib.images.servers.ImageServer
import qupath.lib.objects.PathObject
import qupath.lib.objects.PathObjectTools

// Load geoJSON file and affine transform matrix
def geoJsonFile = new File('D:/Laird Lab/Christy_Nathan/Christy_Nathan_SOW20230501/CyCIF_C1-12-12_QuPath/C1-12-12_rd2.geojson')
def transformMatrix = [
        [0.1, 0, 0],   // Affine transform matrix values
        [0, 0.1, 0],   // Modify these values according to your requirements
        [0, 0, 1]
]

// Read the geoJSON file
def geoJsonContent = geoJsonFile.text

// Parse the geoJSON content
def geoJson = new groovy.json.JsonSlurper().parseText(geoJsonContent)

// Iterate over the features in the geoJSON file
geoJson.features.each { feature ->
    // Get the geometry coordinates
    def coordinates = feature.geometry.coordinates

    // Create a polygon from the coordinates
    def polygon = new Polygon2()
    coordinates.each { point ->
        polygon.addVertex(new Point2(point[0], point[1]))
    }

    // Apply the affine transform matrix to the polygon
    def transformedPolygon = GeometryTools.transformCoordinates(polygon, transformMatrix)

    // Create a new annotation based on the transformed polygon
    def annotation = PathObjectTools.createAnnotationObject(transformedPolygon)
    // Set other properties of the annotation if needed
    // annotation.setColor(Color.RED)

    // Add the annotation to the current image
    // Assuming QuPath is already open and the desired image is active
    ImageServer.getServer().getImageData().getHierarchy().getSelectionModel().forEach { imageData ->
        imageData.getHierarchy().addPathObject(annotation)
    }
}

// Refresh the viewer to display the imported annotations
qupath.gui.GUIManager.getInstance().refreshPanels()
