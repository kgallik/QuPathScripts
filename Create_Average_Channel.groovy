import qupath.lib.images.ImageData
import qupath.lib.images.servers.ConcatChannelsImageServer
import qupath.lib.images.servers.TransformedServerBuilder
import javafx.application.Platform
import qupath.lib.scripting.QP
import qupath.lib.projects.Project
import qupath.lib.gui.scripting.QPEx
import qupath.lib.images.writers.ome.OMEPyramidWriter

def avgServer = new TransformedServerBuilder( getCurrentServer() ).extractChannels(1,2,3).averageChannelProject().build()
def imageData = new ImageData(avgServer)
def outpath = "Your/Path/Here"

// OME.Tiff parameters
def tilesize = 2048
def outputDownsample = 1
def pyramidscaling = 4
def nThreads = 6
def compression = OMEPyramidWriter.CompressionType.UNCOMPRESSED  
def name = getProjectEntry().getImageName()
def filename = name[0..-5] + "_average.ome.tif" //name[0..-5] gets the names of the iamge without the .svs at the end
def pathOutput = outpath + filename

println('Writing OME-TIFF ' + filename)

new OMEPyramidWriter.Builder(avgServer)
   .compression(compression)
   .parallelize(nThreads)
   .channelsInterleaved()     
   .tileSize(tilesize)
   .scaledDownsampling(outputDownsample, pyramidscaling)
   .build()
   .writePyramid(pathOutput)

println('Done:' + filename) 