//Use the cellposeChannels set to (n,n) for getting a custom model to run properly 1 = red 2 = green 3 = blue
//Parameters for filtering
//def min_nuc_area=250 //remove any nuclei with an area less than or equal to this value
//nuc_area_measurement='Area µm^2'
//def min_nuc_intensity=0 //remove any detections with an intensity less than or equal to this value
//nuc_intensity_measurement='Channel 1: Mean'

selectAnnotations()

import qupath.ext.biop.cellpose.Cellpose2D
// For all the options from cellpose: https://cellpose.readthedocs.io/en/latest/cli.html
// For all the options from omnipose: https://omnipose.readthedocs.io/command.html#all-options

// Specify the model name (cyto, nuc, cyto2, omni_bact or a path to your custom model)
def pathModel = 'cyto3'
def cellpose = Cellpose2D.builder( pathModel )
        .pixelSize( 0.325 )            // Resolution for detection in um
        .setOverlap(60)
        .channels('NaKATPase','DAPI')	              // Select detection channel(s)
//        .preprocess( ImageOps.Filters.median(1) )                // List of preprocessing ImageOps to run on the images before exporting them
//        .normalizePercentilesGlobal(0.1, 99.8, 10) // Convenience global percentile normalization. arguments are percentileMin, percentileMax, dowsample.
        .tileSize(2048)                  // If your GPU can take it, make larger tiles to process fewer of them. Useful for Omnipose
//        .cellposeChannels(1)           // Overwrites the logic of this plugin with these two values. These will be sent directly to --chan and --chan2
        .cellprobThreshold(-0.6)          // Threshold for the mask detection, defaults to 0.0
        .flowThreshold(0.6)              // Threshold for the flows, defaults to 0.4 
        .diameter(30)                    // Median object diameter. Set to 0.0 for the `bact_omni` model or for automatic computation
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

// Run detection for the selected objects
def imageData = getCurrentImageData()
def pathObjects = getSelectedObjects()
if (pathObjects.isEmpty()) {
    Dialogs.showErrorMessage("Cellpose", "Please select a parent object!")
    return
}
cellpose.detectObjects(imageData, pathObjects)

//def toDelete = getDetectionObjects().findAll {measurement(it, nuc_area_measurement) <= min_nuc_area}
//removeObjects(toDelete, true)
//def toDelete2 = getDetectionObjects().findAll {measurement(it, nuc_intensity_measurement) <= min_nuc_intensity}
//removeObjects(toDelete2, true)

println 'Done!'