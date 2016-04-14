package unibonn.eis.fuhsen.engine

import java.util.ArrayList
import com.hp.hpl.jena.rdf.model.Model
import com.hp.hpl.jena.rdf.model.ModelFactory
import de.ddb.common.ApiConsumer

class QueryExecutor {
	
	def search(ArrayList<String> queryStrings, def urlQuery) {
		
		def models = [:]
		//Model model = ModelFactory.createDefaultModel()
		
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
		
		try
		{
			log.info "Searching in Google Plus"
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
				
			//model.add(resultsGoogle)
			models.put("gplus", resultsGoogle)
			
		} catch(Exception) {
			log.error("Something when wrong in G+ search...")
		}
		
		//http://localhost:9000/ldw/v1/restApiWrapper/id/twitter/search?query=Collarana
		//--------------------------------------------------------------------
		//(3) Searching in Twitter
		//--------------------------------------------------------------------
		
		try
		{
			log.info "Searching in Twitter"
			
			def apiResponseTwitter = ApiConsumer.getJson('http://localhost:9000' ,'ldw/v1/restApiWrapper/id/twitter/search', false, urlQuery2)
			if(!apiResponseTwitter.isOk()){
				log.error "Json: Json file was not found"
				apiResponseTwitter.throwException(null)
			}
				
			def resultsItemsRdfTwitter = apiResponseTwitter.getResponse()
				
			Model resultsTwitter = ModelFactory.createDefaultModel()
			String modelTextTwitter = resultsItemsRdfTwitter.toString()
				
			//log.info("RDF Twitter: "+modelTextTwitter)
				
			resultsTwitter.read(new ByteArrayInputStream(modelTextTwitter.getBytes("UTF-8")), null, "JSON-LD")
				
			//model.add(resultsTwitter)
			models.put("twitter", resultsTwitter)
			
		} catch(Exception) {
			log.error("Something when wrong in Twitter search...")
		}
				
		//--------------------------------------------------------------------
		//(4) Searching in eBay
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
			
			//model.add(resultsEBay)
			models.put("ebay", resultsEBay)
		} catch(Exception) {
			log.error("Something when wrong in eBay search...")
		}
		
		//http://localhost:9000/ldw/v1/restApiWrapper/id/facebook/search?query=Auer
		//--------------------------------------------------------------------
		//(5) Searching in FaceBook
		//--------------------------------------------------------------------
		
		try
		{
			log.info "Searching in facebook"
			
			def apiResponseFacebook = ApiConsumer.getJson('http://localhost:9000' ,'ldw/v1/restApiWrapper/id/facebook/search', false, urlQuery2)
			if(!apiResponseFacebook.isOk()){
				log.error "Json: Json file was not found"
				apiResponseFacebook.throwException(null)
			}
				
			def resultsItemsRdfFacebook = apiResponseFacebook.getResponse()
				
			Model resultsFacebook = ModelFactory.createDefaultModel()
			String modelTextFacebook = resultsItemsRdfFacebook.toString()
				
			//log.info("RDF: "+modelTextTwitter)
				
			resultsFacebook.read(new ByteArrayInputStream(modelTextFacebook.getBytes("UTF-8")), null, "JSON-LD")
				
			//model.add(resultsFacebook)
			models.put("facebook", resultsFacebook)
			
		} catch(Exception) {
			log.error("Something when wrong in facebook search...")
		}
		
		//http://localhost:9000/ldw/v1/restApiWrapper/id/gkb/search?query=Auer
		//--------------------------------------------------------------------
		//(6) Searching in Google Knowledge Graph
		//--------------------------------------------------------------------
		
		try
		{
			log.info "Searching in Goohle Knowledge Graph"
			
			def apiResponseGkb = ApiConsumer.getJson('http://localhost:9000' ,'ldw/v1/restApiWrapper/id/gkb/search', false, urlQuery2)
			if(!apiResponseGkb.isOk()){
				log.error "Json: Json file was not found"
				apiResponseGkb.throwException(null)
			}
				
			def resultsItemsRdfGkb = apiResponseGkb.getResponse()
				
			Model resultsGkb = ModelFactory.createDefaultModel()
			String modelTextGkb = resultsItemsRdfGkb.toString()
				
			//log.info("RDF: "+modelTextTwitter)
				
			resultsGkb.read(new ByteArrayInputStream(modelTextGkb.getBytes("UTF-8")), null, "JSON-LD")
				
			//model.add(resultsGkb)
			models.put("gkb", resultsGkb)
			
		} catch(Exception) {
			log.error("Something when wrong in facebook search...")
		}

		//http://localhost:9000/ldw/v1/restApiWrapper/id/gplus/search?query=Auer
		//--------------------------------------------------------------------
		//(7) Searching in Google Plus for Organizations
		//--------------------------------------------------------------------
		
		try
		{
			log.info "Searching in Google Plus for Organizations"
			
			def apiResponseGplus = ApiConsumer.getJson('http://localhost:9000' ,'ldw/v1/restApiWrapper/id/gplus/search', false, urlQuery2)
			if(!apiResponseGplus.isOk()){
				log.error "Json: Json file was not found"
				apiResponseGplus.throwException(null)
			}
				
			def resultsItemsRdfGplus = apiResponseGplus.getResponse()
				
			Model resultsGplus = ModelFactory.createDefaultModel()
			String modelTextGplus = resultsItemsRdfGplus.toString()
				
			//log.info("RDF: "+modelTextTwitter)
				
			resultsGplus.read(new ByteArrayInputStream(modelTextGplus.getBytes("UTF-8")), null, "JSON-LD")
				
			//model.add(resultsGkb)
			models.put("gpluso", resultsGplus)
			
		} catch(Exception) {
			log.error("Something when wrong in facebook search...")
		}
				
		//return model
		return models
	}

}
