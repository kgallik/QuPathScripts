//Parameters for filtering
//def min_nuc_area=250 //remove any nuclei with an area less than or equal to this value
//nuc_area_measurement='Area µm^2'
//def min_nuc_intensity=0 //remove any detections with an intensity less than or equal to this value
//nuc_intensity_measurement='Channel 1: Mean'

//def toDelete = getDetectionObjects().findAll {measurement(it, nuc_area_measurement) <= min_nuc_area}
//removeObjects(toDelete, true)
//def toDelete2 = getDetectionObjects().findAll {measurement(it, nuc_intensity_measurement) <= min_nuc_intensity}
//removeObjects(toDelete2, true)