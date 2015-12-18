package unibonn.eis.fuhsen.engine

import java.util.ArrayList
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import de.ddb.common.ApiConsumer

class QueryExecutor {
	
	public Model search(ArrayList<String> queryStrings, def urlQuery) {
		
		Model model = ModelFactory.createDefaultModel()
		
		def urlQuery2 = urlQuery.clone()
		
		//--------------------------------------------------------------------
		//(1) Searching in PEP List
		//--------------------------------------------------------------------
//		try
//		{
//			def apiResponse = ApiConsumer.getJson('http://localhost:8080/fuhsen' ,'/ldw/pep/search', false, urlQuery)
//			if(!apiResponse.isOk()){
//				log.error "Json: Json file was not found"
//				apiResponse.throwException(null)
//			}
//			def resultsItemsRdf = apiResponse.getResponse()
//			
//			Model resultsPep = ModelFactory.createDefaultModel()
//			String modelText = resultsItemsRdf.toString()
//			resultsPep.read(new ByteArrayInputStream(modelText.getBytes()), null, "JSON-LD")
//			
//			model.add(resultsPep)
//			
//		}catch(Exception) {
//			log.error("Something when wrong in PEP list search...")
//		}
		
		//--------------------------------------------------------------------
		//(2) Searching in Google Plus
		//--------------------------------------------------------------------
		
		//for (int i = 0; i < 10; i++) {
			try
			{
				def apiResponseGoogle = ApiConsumer.getJson('http://linkeddatawrapper-dataextraction.rhcloud.com' ,'/ldw/googleplus/search.html', false, urlQuery2)
				if(!apiResponseGoogle.isOk()){
					log.error "Json: Json file was not found"
					apiResponseGoogle.throwException(null)
				}
				
				def resultsItemsRdfGoogle = apiResponseGoogle.getResponse()
				
				Model resultsGoogle = ModelFactory.createDefaultModel()
				String modelTextGoogle = resultsItemsRdfGoogle.toString()
				
				//System.out.println("RDF: "+modelTextGoogle)
				
				resultsGoogle.read(new ByteArrayInputStream(modelTextGoogle.getBytes("UTF-8")), null, "JSON-LD")
				
				model.add(resultsGoogle)
			} catch(Exception) {
				log.error("Something when wrong in G+ search...")
			}
		//}
				
		//--------------------------------------------------------------------
		//(3) Searching in eBay
		//--------------------------------------------------------------------
		try
		{
			def apiResponseEbay = ApiConsumer.getJson('http://linkeddatawrapper-dataextraction.rhcloud.com' ,'/ldw/ebay/search.html', false, urlQuery2)
			if(!apiResponseEbay.isOk()){
				log.error "Json: Json file was not found"
				apiResponseEbay.throwException(null)
			}
			
			def resultsItemsRdfEBay = apiResponseEbay.getResponse()
			
			Model resultsEBay = ModelFactory.createDefaultModel()
			String modelTextEBay = resultsItemsRdfEBay.toString()
			
			//System.out.println("RDF: "+modelTextGoogle)
			
			resultsEBay.read(new ByteArrayInputStream(modelTextEBay.getBytes("UTF-8")), null, "JSON-LD")
			
			model.add(resultsEBay)
				
			//model.write(System.out, "TTL")
			
		} catch(Exception) {
			log.error("Something when wrong in eBay search...")
		}
				
		return model		
	}

}
