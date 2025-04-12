def server = getCurrentServer()
def name = getCurrentImageName()
def threshold = 'Triangle_Floor2000'
def requestFull = RegionRequest.createInstance(server, 2)
def viewer = getCurrentViewer()
def path = '//pn.vai.org/projects/roybon/vari-core-generated-data/OIC/OIC-125_PSynSpheroids/Snapshots/Triangle_Floor_Testing/'
writeRenderedImageRegion(viewer, requestFull, path+name+'_'+threshold+'.png')