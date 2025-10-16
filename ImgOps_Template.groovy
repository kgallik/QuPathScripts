import qupath.lib.images.servers.TransformedServerBuilder

def imageData = getCurrentImageData()
def stains = imageData.getColorDeconvolutionStains()
def imgOps = ImageOps.buildImageDataOp().appendOps(
                ImageOps.Channels.deconvolve(stains),
                ImageOps.Channels.extract(0,1),
                ImageOps.Channels.sum(),
                ImageOps.Filters.median(2),
                ImageOps.Core.divide(2))
def opsServer = ImageOps.buildServer(imageData, imgOps, imageData.getServer().getPixelCalibration())