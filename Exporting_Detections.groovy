import qupath.lib.images.servers.LabeledImageServer
def imageData = getCurrentImageData()
// Define output path (relative to project)
def name = GeneralTools.getNameWithoutExtension(imageData.getServer().getMetadata().getName())
def pathOutput = buildFilePath(PROJECT_BASE_DIR, 'export', name)
mkdirs(pathOutput) 
// Export at full resolution
double downsample = 1.0
// Create an ImageServer where the pixels are derived from annotations
def labelServer = new LabeledImageServer.Builder(imageData)
    .backgroundLabel(0, ColorTools.WHITE) // Specify background label (usually 0 or 255)
    .downsample(1)    // Choose server resolution; this should match the resolution at which tiles are exported
    .addLabel('Positive', 1)      // Choose output labels (the order matters!)
    .multichannelOutput(false)
    .setBoundaryLabel('Anything*', 0)// If true, each label refers to the channel of a multichannel binary image (required for multiclass probability)
    .lineThickness(3 as float)
    .useDetections()
    .build()
// Export each region

for (annotation in getAnnotationObjects()) {
    def region = RegionRequest.createInstance(labelServer.getPath(), downsample, annotation.getROI())
    def title = annotation.getName()
    def outputPath = buildFilePath(pathOutput, name + '_' + title + '.png')
    writeImageRegion(labelServer, region, outputPath)
}
