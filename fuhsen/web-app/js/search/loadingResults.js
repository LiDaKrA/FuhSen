$().ready(function(){

	//alert('Hello World LoadingResults.js');
	$.ajax({
	    type : 'GET',
	    dataType : 'json',
	    async : true,
	    url : './loadingresults?query='+$('input[name=queryH]').val()+'&reqType=ajax',
	    success : function(data) {
	    	//alert('Successfull call');
	    	// similar behavior as clicking on a link
	    	window.location.href = './searchresults?query='+$('input[name=queryH]').val();
	    },
	    error : function() {
	    	alert('Ups!!! Something went wrong with the search engine, please try again.');
	    }
	});
    
});