def name = getCurrentImageName() //gives name with file extension
def path = "E:\\Steensma_Lab\\Abigail\\QuPath_Involution\\2025.01.06_batch_outlines\\" //where to save the geojson files
exportAllObjectsToGeoJson(path + name[0..-5] + ".geojson", "FEATURE_COLLECTION") //name[0..-5] gives name without extension (.svs in this case)