package unibonn.eis.fuhsen.engine

import java.util.ArrayList;

class QueryExpander {

	public ArrayList<String> expandQuery(String query) {
		
		ArrayList<String> queryStrings = new ArrayList<String>()
				
		String[] parts = query.split(" ")
		if (parts.length > 2)
		{
			queryStrings.add(query)
			
			//Remove last
			String queryHelper = ""
			for (int i = 0; i < parts.length - 1; i++) {
				queryHelper += parts[i] + " "
			}
			queryHelper = queryHelper.trim()
			queryStrings.add(queryHelper)
			
			//Remove first
			queryHelper = ""
			for (int i = 1; i < parts.length; i++) {
				queryHelper += parts[i] + " "
			}
			queryHelper = queryHelper.trim()
			queryStrings.add(queryHelper)
			
		}
		else
			queryStrings.add(query)
		
		System.out.println("Query Strings: "+queryStrings)
		
		return queryStrings
	}
	
}
