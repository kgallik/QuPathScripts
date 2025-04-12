selectObjectsByClassification("Training");
clearSelectedObjects();

//Delete Specific Annotations
//def toDelete1 = getAnnotationObjects().findAll {it.getPathClass() == getPathClass('Cell_classifier')}
//removeObjects(toDelete1, true)
//Another way to delete objects
//def toDelete = getDetectionObjects().findAll {measurement(it, nuc_intensity_measurement) <= min_nuc_intensity}
//removeObjects(toDelete2, true)
