package uzuzjmd.rcd.mapper

import uzuzjmd.owl.util.CompOntologyAccess
import uzuzjmd.rcd.competence.RCDFilter
import uzuzjmd.rcd.generated.Rdceo
import scala.collection.mutable.Buffer
import com.hp.hpl.jena.ontology.Individual
import com.hp.hpl.jena.ontology.OntClass
import uzuzjmd.owl.competence.ontology.CompObjectProperties
import scala.collection.JavaConverters._
import uzuzjmd.owl.competence.ontology.CompOntClass

object RCD2Operators extends RCDImplicits {
  /**
   * Looks up Competence for each Title
   * Then it searches for all Operators of this Competence
   * Then it makes the operator inherit them
   */
  def createSubOperatorRels(util: uzuzjmd.owl.util.CompOntologyAccess, rcdeos: Buffer[Rdceo]) {
    rcdeos.foreach(handleRcdeoSubOperators(util))
  }

  /**
   * maps the (sub)operator parts of the rcdeo to a model represantation
   * Hilfsfunktion f�r createSubOperatorRels
   */
  private def handleRcdeoSubOperators(util: CompOntologyAccess)(rcdeo: Rdceo) {
    // suboperatorTriples.foreach(handleSubOperatorTriple(util))
    val title = rcdeo.getTitle()
    val statements = rcdeo.getDefinition().asScala.head.getStatement().asScala
    val operators = statements.filter(statement => statement.getStatementname().equals(CompOntClass.Operator.name())).map(statement => statement.getStatementtext())
    val subOperators = statements.filter(statement => statement.getStatementname().equals(CompOntClass.SubOperator.name())).map(statement => statement.getStatementtext())
    subOperators.foreach(subOperators => operators.foreach(operator => handleSubOperatorTriple(util)((title, operator, subOperators))))
  }

  /**
   * suboperatortriples: x._1 competence, x._2 operator,  x._3 suboperator,
   * Hilfsfunktion f�r handleRcdeoSubOperators
   */
  private def handleSubOperatorTriple(util: CompOntologyAccess)(input: RCDFilter.OperatorTriple) {
    val operatorIndividual: Individual = util.createSingleTonIndividualWithClass2(input._3)
    val competenceIndividual: Individual = util.createSingleTonIndividual(input._1)
    val superOperatorClass: OntClass = util.createSingleTonIndividualWithClass(input._2)
    operatorIndividual.getOntClass().addSuperClass(superOperatorClass)
    util.createObjectPropertyWithIndividual(operatorIndividual, competenceIndividual, CompObjectProperties.OperatorOf)
  }

}