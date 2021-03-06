package uzuzjmd.competence.main

import java.io.IOException
import java.util.Arrays
import comparison.verification.SimpleCompetenceVerifier
import org.apache.solr.client.solrj.SolrServerException
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.common.SolrDocumentList
import uzuzjmd.competence.comparison.analysis.SentenceToOperator
import uzuzjmd.competence.comparison.verification.ValidOperators
import uzuzjmd.competence.crawler.solr.SolrConnector
import uzuzjmd.competence.logging.Logging
import uzuzjmd.competence.util.LanguageConverter

import scala.collection.JavaConverters._
import scala.collection.mutable


/**
  * Created by dehne on 16.03.2016.
  */
object CompetenceCrawler extends LanguageConverter with Logging {
  private val solrUrl: String = "http://learnlib.soft.cs.uni-potsdam.de:80/solr/test2"
  private val blockingQueue: mutable.Set[String] = mutable.Set.empty[String]
  private var counter: Int = 0
  private var analyzeCounter: Int = 0
  private var competencesFound: List[String] = List.empty[String]

  @throws(classOf[IOException])
  @throws(classOf[SolrServerException])
  def main(args: Array[String]) {
    computeVerification
  }

  def computeVerification: Unit = {
    val simpleCompetenceVerifier: SimpleCompetenceVerifier = new SimpleCompetenceVerifier
    val solrConnector: SolrConnector = new SolrConnector(solrUrl, 200)
    // ++ ValidSubjects.values().map(x=>x.toString)
    val inputList: Array[String] = ValidOperators.values().map(x => x.toString)
    val inputListFolded = inputList.reduce((a, b) => a + "\" OR \"" + b)
    val result: QueryResponse = solrConnector.connectToSolr(inputListFolded)
    val documents: SolrDocumentList = result.getResults

    val documentIterator = documents.listIterator();
    while (documentIterator.hasNext) {
      val document = documentIterator.next()
      val content: String = document.getFieldValue("content").asInstanceOf[String]
      val sentences: List[String] = Arrays.asList(content.split("\\.")).asScala.flatten.
        toList.map(x => x.split("\n")).flatten
        .map(x => x.split(":")).flatten
        .map(x => x + ".")
      sentences.foreach(x => blockingQueue.add(x))
    }
    logger.info("############")
    logger.info("analyzing: " + blockingQueue.size + " sentences")
    blockingQueue.toList.par.foreach(verifySentence(simpleCompetenceVerifier, _))
    logger.info("found " + counter + " competencies!")

    logger.info("##########")
    logger.info("competences found: ")
    competencesFound.foreach(x => logger.info(x))
  }

  @throws[IndexOutOfBoundsException]
  def verifySentence(simpleCompetenceVerifier: SimpleCompetenceVerifier, strings: String): Unit = {
    //logger.info("checking: "+strings)

    analyzeCounter = analyzeCounter + 1;

    logger.info("####")
    logger.info("Analyzing number " + analyzeCounter + " of " + blockingQueue.size);

    try {
      if (!checkString(strings)) {
        return
      }
      val cleanedString: java.lang.String = cleanString(strings)
      //println(strings)

      logger.trace("analyzing string:" + cleanedString);

      val verbs = SentenceToOperator.convertSentenceToFilteredElement(cleanedString)
      if (!verbs.isEmpty) {
        val operator: String = verbs.head
        logger.trace("verb is: " + operator)
      }

      val nouns = SentenceToOperator.convertSentenceToFilteredElement(cleanedString)
      if (!nouns.isEmpty) {
        val noun: String = nouns.head
        logger.trace("noun is: " + noun)
      }

      if (!verbs.isEmpty && !nouns.isEmpty) {
        if (simpleCompetenceVerifier.isCompetence(cleanedString, edu.stanford.nlp.trees.GrammaticalRelation.Language.Any)) {
          counter += 1
          competencesFound = cleanedString :: competencesFound
          logger.trace(" found competence" + strings)
        }
      }
    } catch {
      case e: Exception => print("hello")
    }
  }

  def checkString(strings: String): Boolean = {
    if (strings == null || strings.equals("") || strings.size < 20) {
      return false;
    }
    if (strings.split(" ").size > 124) {
      return false;
    }

    if (strings.contains("„") || strings.contains("\"")) {
      return false;
    }

    if (strings.contains("?")) {
      return false;
    }

    if (strings.contains("|")) {
      return false;
    }

    if (strings.contains("•")) {
      return false;
    }



    return true;
  }

  def cleanString(strings: String): String = {
    return strings;
  }
}