package unibonn.eis.fuhsen

import grails.transaction.Transactional
import unibonn.eis.fuhsen.constans.*

class SettingsService {

	def transactional = false
	
    def String getRdfOutPutFormat() {
		return SettingEnum.RDF_JSON_LD.getValue()
		//return SettingEnum.RDF_TURTLE.getValue()
    }
	
	def String getContentTypeOutPutFormat() {
		return SettingEnum.CT_JSON_LD.getValue()
		//return SettingEnum.CT_TURTLE.getValue()
	}
	
}
