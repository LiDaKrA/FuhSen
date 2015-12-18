package unibonn.eis.fuhsen.engine

import com.hp.hpl.jena.rdf.model.Model
import java.util.ArrayList;

class SearchEngine {

	public Model getResults(String keyWord, def urlQuery) {
		
		log.info("Search Starts: "+System.currentTimeMillis())
		// 1) Expand query
		log.info("Expanded Search Generation Starts: "+System.currentTimeMillis())
		QueryExpander qe = new QueryExpander()
		ArrayList<String> queryStrings = qe.expandQuery(keyWord)
		log.info("Expanded Search Generation Ends: "+System.currentTimeMillis())

		log.info("Federated Query Execution Starts: "+System.currentTimeMillis())
		// 2) Execute Search
		QueryExecutor qex = new QueryExecutor()
		Model results = qex.search(queryStrings, urlQuery)
		log.info("Federated Query Execution Ends: "+System.currentTimeMillis())
		
		// 3) Ranking results
		log.info("Ranking results starts: "+System.currentTimeMillis())
		RankingResults ra = new RankingResults()
		ra.rank(results)
		log.info("Ranking results ends: "+System.currentTimeMillis())
		
		log.info("Search Ends: "+System.currentTimeMillis())
		
		return results;		
	}
	
}
