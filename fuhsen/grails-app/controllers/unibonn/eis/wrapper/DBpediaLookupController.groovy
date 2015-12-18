package unibonn.eis.wrapper

class DBpediaLookupController {

	def settingsService
	def searchService
	
    def index() { 
		
		String kQuery = ""
		if (params["query"] != null)
			kQuery = params["query"]
			
		log.info("Executing DBPedia Lookup With: "+kQuery)
		
		def cookieParametersMap = searchService.getSearchCookieAsMap(request, request.cookies)
		def urlQuery = searchService.convertQueryParametersToSearchParameters(params, cookieParametersMap)
		
		try
		{
			def apiResponse = ApiConsumer.getJson('http://lookup.dbpedia.org' ,'/api/search/KeywordSearch', false, urlQuery)
			if(!apiResponse.isOk()){
				log.error "Json: Json file was not found"
				apiResponse.throwException(null)
			}
			
			def results = apiResponse.getResponse()
			
		} catch(Exception) {
			log.error("Something when wrong in DBPeadia search...")
		}
		
	}
}
