import qupath.ext.stardist.StarDist2D
import qupath.ext.biop.cellpose.Cellpose2D
import qupath.lib.objects.PathObjects
import qupath.lib.analysis.features.ObjectMeasurements

selectAnnotations()

// Set some variables
var imageData = getCurrentImageData()
var server = getCurrentServer()
var pathObjects = getSelectedObjects()
if (pathObjects.isEmpty()) {
    createSelectAllObject(true)
    }
var cal = server.getPixelCalibration()
var downsample = 1.0

// Run cellpose
cytoPathModel = "D:/Laird Lab/Christy_Nathan/Christy_Nathan_SOW20230501/CellPose_Training/models/CP_20230605_152954"
def cellpose1 = Cellpose2D.builder(cytoPathModel)
        .pixelSize( 0.3450 )                  // Resolution for detection in um
//        .channels('DAPI','EGFP')	               // Select detection channel(s)
//        .preprocess( ImageOps.Filters.median(1) )                // List of preprocessing ImageOps to run on the images before exporting them
//        .normalizePercentilesGlobal(0.1, 99.8, 10) // Convenience global percentile normalization. arguments are percentileMin, percentileMax, dowsample.
        .tileSize(2048)                  // If your GPU can take it, make larger tiles to process fewer of them. Useful for Omnipose
        .cellposeChannels(2,1)           // Overwrites the logic of this plugin with these two values. These will be sent directly to --chan and --chan2
        .cellprobThreshold(-6)          // Threshold for the mask detection, defaults to 0.0
        .flowThreshold(2.1)              // Threshold for the flows, defaults to 0.4 
        .diameter(17.37)                    // Median object diameter. Set to 0.0 for the `bact_omni` model or for automatic computation
//        .useOmnipose()                   // Use the omnipose instead
//        .addParameter("cluster")         // Any parameter from cellpose or omnipose not available in the builder. 
//        .addParameter("save_flows")      // Any parameter from cellpose or omnipose not available in the builder.
//        .addParameter("anisotropy", "3") // Any parameter from cellpose or omnipose not available in the builder.
//        .cellExpansion(5.0)              // Approximate cells based upon nucleus expansion
//        .cellConstrainScale(1.5)         // Constrain cell expansion using nucleus size
//        .classify("My Detections")       // PathClass to give newly created objects
//        .measureShape()                  // Add shape measurements
//        .measureIntensity()              // Add cell measurements (in all compartments)  
//        .createAnnotations()             // Make annotations instead of detections. This ignores cellExpansion
//        .simplify(0)                     // Simplification 1.6 by default, set to 0 to get the cellpose masks as precisely as possible
        .build()
cellpose1.detectObjects(imageData, pathObjects)

// Save cytoplasm
//def cytoplasms = getDetectionObjects()
//selectDetections() //This option constrains the second round of detection to just the celloutlines (many subregions). Not a bad option but causes few cells and
//less accruate nuclear outlines to be generated.
//clearDetections()

// Run second round of CellPose to get Nuclei
nucPathModel = 'nuc'
def cellpose2 = Cellpose2D.builder(nucPathModel)
        .pixelSize( 0.3450 )                  // Resolution for detection in um
        .channels(0)	               // Select detection channel(s)
//        .preprocess( ImageOps.Filters.median(1) )                // List of preprocessing ImageOps to run on the images before exporting them
//        .normalizePercentilesGlobal(0.1, 99.8, 10) // Convenience global percentile normalization. arguments are percentileMin, percentileMax, dowsample.
        .tileSize(2048)                  // If your GPU can take it, make larger tiles to process fewer of them. Useful for Omnipose
//        .cellposeChannels(2,1)           // Overwrites the logic of this plugin with these two values. These will be sent directly to --chan and --chan2
        .cellprobThreshold(0)          // Threshold for the mask detection, defaults to 0.0
        .flowThreshold(2.1)              // Threshold for the flows, defaults to 0.4 
        .diameter(17.37)                    // Median object diameter. Set to 0.0 for the `bact_omni` model or for automatic computation
//        .useOmnipose()                   // Use the omnipose instead
//        .addParameter("cluster")         // Any parameter from cellpose or omnipose not available in the builder. 
//        .addParameter("save_flows")      // Any parameter from cellpose or omnipose not available in the builder.
//        .addParameter("anisotropy", "3") // Any parameter from cellpose or omnipose not available in the builder.
//        .cellExpansion(5.0)              // Approximate cells based upon nucleus expansion
//        .cellConstrainScale(1.5)         // Constrain cell expansion using nucleus size
//        .classify("My Detections")       // PathClass to give newly created objects
        .measureShape()                  // Add shape measurements
        .measureIntensity()              // Add cell measurements (in all compartments)  
//        .createAnnotations()             // Make annotations instead of detections. This ignores cellExpansion
//        .simplify(0)                     // Simplification 1.6 by default, set to 0 to get the cellpose masks as precisely as possible
        .build()
cellpose2.detectObjects(imageData, pathObjects)

//clearSelectedObjects()

// Save nuclei
def nuclei = getDetectionObjects()

// Start with a clean slate
clearDetections()
println("Creating Cells")
// Create cells
cells = []
cytoplasms.each { cytoplasm ->
    nuclei.each{ nucleus ->      
        if ( cytoplasm.getROI().contains( nucleus.getROI().getCentroidX() , nucleus.getROI().getCentroidY())){
            cells.add(PathObjects.createCellObject(cytoplasm.getROI(), nucleus.getROI(), getPathClass("Stroma"), null ));
            }
        }
    }
            
addObjects(cells)
fireHierarchyUpdate()
println("Done making cells!")

//// Add measurements
//def measurements = ObjectMeasurements.Measurements.values() as List
//def compartments = ObjectMeasurements.Compartments.values() as List
//def shape = ObjectMeasurements.ShapeFeatures.values() as List
//def cells = getCellObjects()
//for ( cell in cells ) {
//    ObjectMeasurements.addIntensityMeasurements( server, cell, downsample, measurements, compartments )
//    ObjectMeasurements.addCellShapeMeasurements( cell, cal,  shape )
//    }
//
//// Finished!
//fireHierarchyUpdate()
//println("Done!")