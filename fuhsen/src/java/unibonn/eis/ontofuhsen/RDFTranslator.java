package unibonn.eis.ontofuhsen;

import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.RDF;

public abstract class RDFTranslator {
	
	public static Model createRdfModel(List<Person> persons, List<Organization> organizations, List<Product> products ) {
		
		Model model = ModelFactory.createDefaultModel();
		
		addPersons(model, persons);
		addOrganizations(model, organizations);
		addProduct(model, products);
		
		return model;
		
	}

	private static void addPersons(Model model, List<Person> persons) {
		
		for (Person person : persons) {
			
			Resource resourcePerson	= model.createResource(person.uri);
			
			resourcePerson.addProperty(RDF.type, FOAF.Person);
			resourcePerson.addProperty(FOAF.family_name, person.familyName);
			resourcePerson.addProperty(FOAF.givenname, person.givenName);
			resourcePerson.addProperty(FOAF.depiction, person.imgUrl);
			//resourcePerson.addProperty(FOAF.name, person.name);
			
			//FOAF.per
			
		}
		
	}
	
	private static void addOrganizations(Model model, List<Organization> organizations) {
		
	}
	
	private static void addProduct(Model model, List<Product> products) {
		
	}
	
}
