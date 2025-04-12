clearCellMeasurements()
import qupath.lib.analysis.DelaunayTools
import static java.lang.Math.*

// Define cell variables
def cells = getCellObjects()
def tumourCells = cells.findAll { cell -> cell.getPathClass() == getPathClass("EGFP:mCher") }

// Set cluster parameters
double distanceMicrons = 25 
double minClusterSize = 0  

// Translate microns into pixels
def server = getCurrentServer()
def cal = server.getPixelCalibration()
def height = cal.getPixelHeight()
def distancePixels = distanceMicrons.div(height)
def numPixels = distancePixels.round(0)

// Define clusters
//def clusters = new DelaunayTools()
//                	.newBuilder(tumourCells)
//                	.calibration(cal)
//                	.centroids()
//               		.build()
                	//.getClusters(DelaunayTools.centroidDistancePredicate(numPixels, true) )

// Remove small clusters      
//clusters.removeAll { it.size() < minClusterSize }

def subdivision = new DelaunayTools()
                	.newBuilder(tumourCells)
                	.calibration(cal)
                	.centroids()
               		.build()

def clusters = subdivision.getClusters(DelaunayTools.centroidDistancePredicate(numPixels, true) )

// Other code kept identical .............
def tumourAnnotation = DelaunayTools.createAnnotationsFromSubdivision(subdivision, null)
addObjects( tumourAnnotation )


// Classify clusters and count them
//def annotateClusters = new DelaunayTools().nameObjectsByCluster(clusters)
//def tumourAnnotation = new DelaunayTools.Subdivision().createAnnotationsFromSubdivision(clusters)
//
//def numClusters = clusters.size()
//println("Number of clusters: " + numClusters )
//getAnnotationObjects().each {
//    it.getMeasurementList().putMeasurement("Number of Tumour Clusters", numClusters)
//    }
//
// Finished
fireHierarchyUpdate()
println("Done!")