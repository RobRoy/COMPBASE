Encoding Verfahren (Stand 26.05.2014)
	- Identifier werden (alt) in CompOntologyAccess encodiert. Wenn die Formatierung des Strings zum Display wichtig ist, wird eine lesbare Form
	als Dataproperty "definition abgespeichert"
	- Die DAO-Schicht kodiert die Identifier in CompetenceOntologyDao (�hnlich zu dem alten Verfahren)
	- F�r die SingletonDaos werden die Identifier nach dem alten Verfahren (oben) kodiert
	- Identifier d�rfen nicht mit einem Sonderzeichen oder einer Zahl beginnen
	- Moodle Kurs Ids werden als "n"+Id kodiert. 

Festgelegte Strings (Stand 26.05.2014)
Prefixe, Dateinamen ... etc sind in der Klasse MagicStrings als default definiert