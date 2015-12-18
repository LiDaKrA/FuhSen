package unibonn.eis.fuhsen.wrapper.rdf;

import java.util.ArrayList;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;

public class PepWrapper {

	private String pepEndPoint = "http://dydra.com/collarad/pep/sparql?auth_token=mST4j4DksmFzoIxK69xO";
	
	public Model searchPerson(String keyWord) {
		
		//QueryExecution qexec = QueryExecutionFactory.sparqlService(pepEndPoint, "CONSTRUCT * where { ?s ?p ?o }");
		//Model model = qexec.execConstruct();
		
		Model model = ModelFactory.createDefaultModel();
		
		ArrayList<FoafPerson> personsFound = new ArrayList<FoafPerson>(); 
		
		QueryExecution qexec = QueryExecutionFactory.sparqlService(pepEndPoint,
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
				+ "	PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "	PREFIX type: <http://dbpedia.org/class/yago/> "
				+ "	PREFIX prop: <http://dbpedia.org/property/> "
				+ "	PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
				+ "SELECT * WHERE { "
				+ "?person a foaf:Person . "
				+ "?person foaf:name ?name . "
				+ "?person foaf:givenName ?givenName . "
				+ "?person foaf:familyName ?familyName . "
				+ "?person foaf:depiction ?depiction . " 
				+ "FILTER contains(str(?name), '"+keyWord+"')"
				+ "} limit 50");

		ResultSet results = qexec.execSelect();
		
		System.out.println("Executing PEP->searchPerson with keyword: "+keyWord+" hasNext: "+results.hasNext());
		
		while(results.hasNext()){
			QuerySolution row = results.nextSolution();
			 
		FoafPerson dbpPerson = new FoafPerson();
			dbpPerson.ressourceUri = row.get("person").toString();
			dbpPerson.familyName = row.getLiteral("familyName").getString();
			dbpPerson.givenName = row.getLiteral("givenName").getString();
			dbpPerson.thumbnail = row.getResource("depiction").toString();		 
			dbpPerson.name = row.getLiteral("name").getString();
			
			personsFound.add(dbpPerson);
		}
		
		
		for (FoafPerson foafPerson : personsFound) {
			
			Resource resourcePerson	= model.createResource(foafPerson.ressourceUri);
			resourcePerson.addProperty(FOAF.family_name, foafPerson.familyName);
			resourcePerson.addProperty(FOAF.givenname, foafPerson.givenName);
			resourcePerson.addProperty(FOAF.depiction, foafPerson.thumbnail);
			resourcePerson.addProperty(FOAF.name, foafPerson.name);
			//model.add(resourcePerson, null, resourcePerson);
		}
		
		return model;
		
	}
	
}
