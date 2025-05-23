//Script writes out a file with the name of the current image, and the Affine Transformation in effect in the current viewer.
//Can get confused if there is more than one overlay active at once.
//Current image should be the destination image
// Michael Nelson 03/2020

def name = getProjectEntry().getImageName()
path = buildFilePath('D:/Haab Lab/Alfredo/Tumor_glycan_CellPose_Test', 'Affine')
mkdirs(path)
path = buildFilePath('D:/Haab Lab/Alfredo/Tumor_glycan_CellPose_Test', 'Affine', name)



import qupath.ext.align.gui.ImageServerOverlay

def overlay = getCurrentViewer().getCustomOverlayLayers().find {it instanceof ImageServerOverlay}

affine = overlay.getAffine()

print affine
afString = affine.toString()
afString = afString.minus('Affine [').minus(']').trim().split('\n')
cleanAffine =[]
afString.each{
    temp = it.split(',')
    temp.each{cleanAffine << Double.parseDouble(it)}
}

def matrix = []
affineList = [0,1,3,4,5,7]
for (i=0;i<12; i++){
if (affineList.contains(i))
    matrix << cleanAffine[i]
}

new File(path).withObjectOutputStream {
    it.writeObject(matrix)
}
print 'Done!'