$().ready(function(){
		
    $(document).scroll(function(){
    	
    	//Checking if we need to display GoToTop button
    	if (!isScrolledIntoView($("#results-paginator-options")))
    		$('.fake').css('visibility','visible');
    	else
    		$('.fake').css('visibility','hidden');
    	
    	
    	//Chrome patch review and fix better in the future
    	var wScrollTop = $(window).scrollTop();
    	var docHeight = $(document).height() - $(window).height();
    	if  (docHeight - wScrollTop < 1.0){
        //if  ($(window).scrollTop() == $(document).height() - $(window).height()){        	
        	var numberOfResultsIntValue = parseInt($('input[name=numberOfResultsH]').val());
        	var rowsIntValue = parseInt($('input[name=rowH]').val());
            var newOffSet = parseInt($('input[name=offsetH]').val()) + rowsIntValue;
            if (numberOfResultsIntValue > newOffSet)
            	loadNewData();
        }
    });

    $('#load_more_results').click(function(){
    	loadNewData();
    });
    
    function loadNewData(){
    
    	if ($('input[name=isLoadingInformation]').val() == 'false') {
    		
    		$('.fake').css('visibility','hidden');
        	
        	var paramsArray = null;
            var selectedFacetValues = [];
            var newOffSet = 0;
        	
        	// Update Url (We want to keep the already selected facet values, but throw away the offset etc.)
            var facetValuesFromUrl = de.ddb.common.search.getFacetValuesFromUrl();

            if (facetValuesFromUrl) {
              $.each(facetValuesFromUrl, function(key, value) {
            		  selectedFacetValues.push(decodeURIComponent(value.replace(/\+/g, '%20')));
              });
            }
            
            //The facet values will be stored in a two dimensional Array ["facetValues[]",['type_fctyDmediatype_003','time_begin_fct=1014', 'time_end_fct=2014',]]
            paramsArray = [['facetValues[]', selectedFacetValues]];

            var rowsIntValue = parseInt($('input[name=rowH]').val());
            newOffSet = parseInt($('input[name=offsetH]').val()) + rowsIntValue;
            $('input[name=offsetH]').val(newOffSet);
            paramsArray.push(['offset', newOffSet]);

            var newUrl = $.addParamToCurrentUrl(paramsArray);

            de.ddb.next.search.loadInfiniteScrollResultsList(newUrl, function() { });
            //de.ddb.next.search.fetchResultsList('http://localhost:8080/ddb-next/searchresults?query=Berlin&rows=20&offset=20', function() {});
        
    	}
    }
    
    function isScrolledIntoView(elem)
    {
        var $elem = $(elem);
        var $window = $(window);

        var docViewTop = $window.scrollTop();
        var docViewBottom = docViewTop + $window.height();

        var elemTop = $elem.offset().top;
        var elemBottom = elemTop + $elem.height();

        return ((elemBottom <= docViewBottom) && (elemTop >= docViewTop));
    }
    
});