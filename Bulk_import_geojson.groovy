def name = getCurrentImageName() //gives name with file extension
def path = "E:\\Steensma_Lab\\Abigail\\QuPath_Involution_Random_ROIs\\"
importObjectsFromFile(path+name[0..-5]+".geojson")