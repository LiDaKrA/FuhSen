package unibonn.eis.wrapper

import com.hp.hpl.jena.rdf.model.Model

import unibonn.eis.fuhsen.wrapper.rdf.*

class PepController {

	def settingsService
	
    def index() {
		
		String kQuery = ""
		if (params["query"] != null)
			kQuery = params["query"] 
			
		log.info("Executing PEP LDW: "+kQuery)
		
		StringWriter output = new StringWriter()
	
		PepWrapper pw = new PepWrapper();
		Model result = pw.searchPerson(kQuery)
		
		result.write(output, settingsService.getRdfOutPutFormat())
		
		output.flush()
		
		render(text: output.toString(), contentType: settingsService.getContentTypeOutPutFormat(), encoding: "UTF-8")
		
	}
}
