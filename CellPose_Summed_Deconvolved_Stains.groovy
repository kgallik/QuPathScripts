/**
 * Cellpose Detection Template script
 * @author Olivier Burri
 *
 * This script is a template to detect objects using a Cellpose model from within QuPath.
 * After defining the builder, it will:
 * 1. Find all selected annotations in the current open ImageEntry
 * 2. Export the selected annotations to a temp folder that can be specified with tempDirectory()
 * 3. Run the cellpose detction using the defined model name or path
 * 4. Reimport the mask images into QuPath and create the desired objects with the selected statistics
 *
 * NOTE: that this template does not contain all options, but should help get you started
 * See all options in https://biop.github.io/qupath-extension-cellpose/qupath/ext/biop/cellpose/CellposeBuilder.html
 * and in https://cellpose.readthedocs.io/en/latest/command.html
 *
 * NOTE 2: You should change pathObjects get all annotations if you want to run for the project. By default this script
 * will only run on the selected annotations.
 */

// Specify the model name (cyto, nuclei, cyto2, ... or a path to your custom model as a string)
// Other models for Cellpose https://cellpose.readthedocs.io/en/latest/models.html
// And for Omnipose: https://omnipose.readthedocs.io/models.html
import static qupath.lib.gui.scripting.QPEx.*
import qupath.opencv.ops.ImageOps
def pathModel = 'cyto3'
def cellpose = Cellpose2D.builder( pathModel )
        .pixelSize( 0.2523 )                      // Resolution for detection in um
        .channels( 0 )	               // Select detection channel(s)
//        .tempDirectory( new File( '/tmp' ) )         // Temporary directory to export images to. defaults to 'cellpose-temp' inside the QuPath Project
//        .preprocess()  // List of preprocessing ImageOps to run on the images before exporting them
//        .normalizePercentilesGlobal( 0.1, 99.8, 10 ) // Convenience global percentile normalization. arguments are percentileMin, percentileMax, dowsample.
        .setOverlap(0)
        .tileSize(2048)                  // If your GPU can take it, make larger tiles to process fewer of them. Useful for Omnipose
//        .cellposeChannels( 1,2 )           // Overwrites the logic of this plugin with these two values. These will be sent directly to --chan and --chan2
//        .cellprobThreshold( 0.0 )          // Threshold for the mask detection, defaults to 0.0
//        .flowThreshold( 0.4 )              // Threshold for the flows, defaults to 0.4
//        .diameter( 15 )                    // Median object diameter. Set to 0.0 for the `bact_omni` model or for automatic computation
//        .useOmnipose()                     // Use omnipose instead
//        .useCellposeSAM()                  // Use cellposeSAM (i.e. cellpose 4.x.x) env instead of previous versions of cellpose <= v3.x.x
//        .addParameter( "cluster" )         // Any parameter from cellpose or omnipose not available in the builder.
//        .addParameter( "save_flows" )      // Any parameter from cellpose or omnipose not available in the builder.
//        .addParameter( "anisotropy", "3" ) // Any parameter from cellpose or omnipose not available in the builder.
//        .cellExpansion( 5.0 )              // Approximate cells based upon nucleus expansion
//        .cellConstrainScale( 1.5 )         // Constrain cell expansion using nucleus size
//        .classify( "My Detections" )       // PathClass to give newly created objects
//        .measureShape()                    // Add shape measurements
//        .measureIntensity()                // Add cell measurements (in all compartments)
//        .createAnnotations()               // Make annotations instead of detections. This ignores cellExpansion
//        .simplify( 0 )                     // Simplification 1.6 by default, set to 0 to get the cellpose masks as precisely as possible
//        .useGPU(false)                     // Force using CPU. Default useGPU(true)
        .build()

// Run detection for the selected objects
def imageData = getCurrentImageData()
def stains = imageData.getColorDeconvolutionStains()
def imgOps = ImageOps.buildImageDataOp().appendOps(
                ImageOps.Channels.deconvolve(stains),
                ImageOps.Channels.extract(0,1),
                ImageOps.Channels.sum(),
                ImageOps.Filters.median(1),
                ImageOps.Core.divide(2))
def opsServer = ImageOps.buildServer(imageData, imgOps, imageData.getServer().getPixelCalibration())

def transformedImageData = new ImageData<>(opsServer, imageData.getHierarchy())

def pathObjects = getSelectedObjects() // To process only selected annotations, useful while testing
// def pathObjects = getAnnotationObjects() // To process all annotations. For working in batch mode
if (pathObjects.isEmpty()) {
    Dialogs.showErrorMessage( "Cellpose", "Please select a parent object!" )
    return
}

cellpose.detectObjects( transformedImageData, pathObjects )

// You could do some post-processing here, e.g. to remove objects that are too small, but it is usually better to
// do this in a separate script so you can see the results before deleting anything.

println 'Cellpose detection script done'

import qupath.ext.biop.cellpose.Cellpose2D