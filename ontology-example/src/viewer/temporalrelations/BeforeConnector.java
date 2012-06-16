package viewer.temporalrelations;

import org.semanticweb.owlapi.reasoner.OWLReasoner;

import util.ReasonableOntology;
import util.Timeable;

public class BeforeConnector implements Timeable {

	private ReasonableOntology _ontology;

	public BeforeConnector(ReasonableOntology ontology) {
		this._ontology = ontology;
	}

	@Override
	public void time() {
		OWLReasoner reasoner = this._ontology.getReasoner();
		// Zeiten auslesen und Before/After-Graph erstellen.
		MinuteGraphCreator minuteGraphCreator = new MinuteGraphCreator(
				this._ontology);
		// Ticker auslesen

		// F�r jeden Ticker: TickerMessages auslesen

		// In einer Map jeder TickerMessage ihre TimeIntervals zuordnen

		// Graph mit TickerMessages erstellen, die �ber Before/After verbunden
		// sind

		// Graph mit TimeIntervals erstellen, die �ber Before/After verbunden
		// sind. Dabei Relationen zwischen TimeIntervals beachten.

		// TimeInterval-Graph ausd�nnen
	}
}
