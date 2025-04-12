def cells = getCellObjects()

def positive = cells.findAll { cell -> cell.getPathClass() == getPathClass('Positive')}
def areas = positive.collect { cell -> measurement(cell, 'Nucleus: Area') } as double[]
print areas.average()