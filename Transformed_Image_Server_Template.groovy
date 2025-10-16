import qupath.lib.images.servers.TransformedServerBuilder
import qupath.lib.gui.images.servers.ChannelDisplayTransformServer
import qupath.lib.images.ImageData
import qupath.lib.color.ColorTransformer

def imageData = getCurrentImageData()
def server = imageData.getServer()
double[] offset = [255, 255, 255]
double[] scale = [-1,-1,-1]
// def transformedServer = new TransformedServerBuilder(server).deconvolveStains(stains, 2).build() // 1:HTX, 2:DAB, 3:Residual
def opticaldensity = ColorTransformer()
def transformedServer = new TransformedServerBuilder(server)
                                .subtractOffsetAndScale(offset,scale)
                                .averageChannelProject()
                                .build()
def transformedImageData = new ImageData<>(transformedServer, imageData.getHierarchy())