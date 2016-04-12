package unibonn.eis.fuhsen

import com.hp.hpl.jena.query.QueryExecution
import com.hp.hpl.jena.query.QuerySolution
import com.hp.hpl.jena.query.ResultSet
import com.hp.hpl.jena.query.QueryExecutionFactory
import com.hp.hpl.jena.rdf.model.Model

class FacetsController {

    /**
     * Returns the values for a specific search facet for the item search.
     *
     * @return the values for a specific search facet for the item search.
     */
    def facetsList() {

        def facetName = params.name
        //def facetQuery = params[SearchParamEnum.QUERY.getName()]
		
		log.debug(facetName)
		
		def facetResults = [:]
		def facetValues = []
		
		String query = ""
		
		def isProduct = false
		def isPerson = false
		def isOrgnization = false
		
		//Products facets
		if (facetName == "product_condition_fct") {
			query = getProductConditionFacetQuery()
			isProduct = true
		}
		if (facetName == "product_price_fct") {
			query = getProductPriceFacetQuery()
			isProduct = true
		}
		if (facetName == "product_country_fct") {
			query = getProductCountryFacetQuery()
			isProduct = true
		}
		
		//Person facets
		if (facetName == "person_gender_fct") {
			query = getPersonGenderFacetQuery()
			isPerson = true
		}
		if (facetName == "person_birthday_fct") {
			query = getPersonBirthdayFacetQuery()
			isPerson = true
		}
		if (facetName == "person_occupation_fct") {
			query = getPersonOccupationFacetQuery()
			isPerson = true
		}
		if (facetName == "person_livesat_fct") {
			query = getPersonLivesAtFacetQuery()
			isPerson = true
		}
		if (facetName == "person_worksat_fct") {
			query = getPersonWorksAtFacetQuery()
			isPerson = true
		}
		if (facetName == "person_studiesat_fct") {
			query = getPersonStudiesAtFacetQuery()
			isPerson = true
		}
		
		def models = [:]
		models = session["Models"]
		Model model = null
		
		//Getting the model for the facets
		if (isPerson)
		{
			if (facetName == "person_location_fct")
				model = models["twitter"]
			else
				model = models["gplus"]
		}
		if (isProduct)
		{
			model = models["ebay"]
		}
		
		//Printing to load in a triplet store and make tests
		//model.write(System.out, "TTL")
		
		if (query != "") {
			QueryExecution qexec = QueryExecutionFactory.create(query, model)
			ResultSet results = qexec.execSelect()
		
			int aSize = 0
		
			while(results.hasNext()) {
				
				QuerySolution row = results.next();
				
				//Patch the case when there is not values NELEMENTS = 0
				if (row.getLiteral("NELEMENTS").getString() != '0')
				{
					def facetValue = [:]				
					facetValue["count"] = row.getLiteral("NELEMENTS").getString()
					facetValue["value"] = row.getLiteral("FACETS").getString()
					facetValue["localizedValue"] = row.getLiteral("FACETS").getString()
					facetValues.add(facetValue)
				}
				
			}
		}
		
		facetResults["type"] = facetName 
		facetResults["values"] = facetValues
		
        render (contentType:"text/json"){facetResults}
		
    }
	
	private def getProductConditionFacetQuery() {
		
		String query = ("PREFIX fuhsen: <http://unibonn.eis.de/fuhsen/common_entities/> "
			+ "PREFIX gr: <http://purl.org/goodrelations/v1#> "
			+ "SELECT (SAMPLE(?condition ) AS ?FACETS) (COUNT(?condition) as ?NELEMENTS) WHERE { "
			+ "?product fuhsen:hadPrimarySource 'EBAY' . "
			+ "?product gr:condition ?condition . "
			+ "} GROUP BY ?condition ")
			
		return query
				
	}
	
	private def getProductPriceFacetQuery() {
		
		String query = ("PREFIX fuhsen: <http://unibonn.eis.de/fuhsen/common_entities/> "
			+ "SELECT (SAMPLE(?price ) AS ?FACETS) (COUNT(?price) as ?NELEMENTS) WHERE { "
			+ "?product fuhsen:hadPrimarySource 'EBAY' . "
			+ "?product fuhsen:price ?price . "
			+ "} GROUP BY ?price ")
			
		return query

	}
	
	private def getProductCountryFacetQuery() {
		
		String query = ("PREFIX fuhsen: <http://unibonn.eis.de/fuhsen/common_entities/> "
			+ "SELECT (SAMPLE(?country ) AS ?FACETS) (COUNT(?country) as ?NELEMENTS) WHERE { "
			+ "?product fuhsen:hadPrimarySource 'EBAY' . "
			+ "?product fuhsen:country ?country . "
			+ "} GROUP BY ?country ")
			
		return query
				
	}
	
	private def getPersonGenderFacetQuery() {
		
		String query = ("PREFIX fuhsen: <http://unibonn.eis.de/fuhsen/common_entities/> "
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
			+ "SELECT (SAMPLE(?gender ) AS ?FACETS) (COUNT(?gender) as ?NELEMENTS) WHERE { "
			+ "?person fuhsen:hadPrimarySource 'GOOGLE+' . "
			+ "?person foaf:gender ?gender . "
			+ "} GROUP BY ?gender ")
			
		return query
				
	}
	
	private def getPersonBirthdayFacetQuery() {
		
		String query = ("PREFIX fuhsen: <http://unibonn.eis.de/fuhsen/common_entities/> "
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
			+ "SELECT (SAMPLE(?birthday ) AS ?FACETS) (COUNT(?birthday) as ?NELEMENTS) WHERE { "
			+ "?person fuhsen:hadPrimarySource 'GOOGLE+' . "
			+ "?person foaf:birthday ?birthday . "
			+ "} GROUP BY ?birthday ")
			
		return query
				
	}
	
	private def getPersonOccupationFacetQuery() {
		
		String query = ("PREFIX fuhsen: <http://unibonn.eis.de/fuhsen/common_entities/> "
			+ "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
			+ "SELECT (SAMPLE(?occupation ) AS ?FACETS) (COUNT(?occupation) as ?NELEMENTS) WHERE { "
			+ "?person fuhsen:hadPrimarySource 'GOOGLE+' . "
			+ "?person fuhsen:occupation ?occupation . "
			+ "} GROUP BY ?occupation ")
			
		return query
				
	}
	
	private def getPersonLivesAtFacetQuery() {
		
		String query = ("PREFIX fuhsen: <http://unibonn.eis.de/fuhsen/common_entities/> "
			+ "SELECT (SAMPLE(?currentAddress ) AS ?FACETS) (COUNT(?currentAddress) as ?NELEMENTS) WHERE { "
			+ "?person fuhsen:hadPrimarySource 'GOOGLE+' . "
			+ "?person fuhsen:placesLived ?placesLived . "
			+ "?placesLived fuhsen:placesLivedprimary 'true' . "
			+ "?placesLived fuhsen:livedAt ?currentAddress . "
			+ "} GROUP BY ?currentAddress ")
			
		return query
				
	}
	
	private def getPersonWorksAtFacetQuery() {
		
		String query = ("PREFIX fuhsen: <http://unibonn.eis.de/fuhsen/common_entities/> "
			+ "SELECT (SAMPLE(?currentWork ) AS ?FACETS) (COUNT(?currentWork) as ?NELEMENTS) WHERE { "
			+ "?person fuhsen:hadPrimarySource 'GOOGLE+' . "
			+ "?person fuhsen:organization ?organization . "
			+ "?organization fuhsen:organizationprimary 'true' . "
			+ "?organization fuhsen:organizationtype 'work' . "
			+ "?organization fuhsen:organizationname ?currentWork . "
			+ "} GROUP BY ?currentWork ")
			
		return query
				
	}
	
	private def getPersonStudiesAtFacetQuery() {
		
		String query = ("PREFIX fuhsen: <http://unibonn.eis.de/fuhsen/common_entities/> "
			+ "SELECT (SAMPLE(?currentSchool ) AS ?FACETS) (COUNT(?currentSchool) as ?NELEMENTS) WHERE { "
			+ "?person fuhsen:hadPrimarySource 'GOOGLE+' . "
			+ "?person fuhsen:organization ?organization . "
			+ "?organization fuhsen:organizationprimary 'true' . "
			+ "?organization fuhsen:organizationtype 'school' . "
			+ "?organization fuhsen:organizationname ?currentSchool . "
			+ "} GROUP BY ?currentSchool ")
			
		return query
				
	}
	
	
}
