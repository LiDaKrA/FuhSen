var apisWrapper = {

  Config : {
    ddbBackendUrl : '/apis/'
  },

  /*
   * sends request with given parameters to frontend (ApisController)
   * @param name name of fucntion in ApisController
   * @param requestType (GET,POST,PUT, DELETE)
   * @param dataType (text, json)
   * @param parameters GET-Parameters as Map
   */
  callApisFunction : function(name, requestType, dataType, parameters) {
    //build String out of parameters-map
    var paramString = new StringBuilder();
    if (parameters != null) {
      paramString.append("?");
      var concat = false;
      for ( var key in parameters) {
        if (concat) {
          paramString.append("&");
        }
        concat = true;
        paramString.append(key).append("=").append(parameters[key]);
      }
    }
    var request = $.ajax({
      type : requestType,
      dataType : dataType,
      async : false,
      url : jsContextPath + apisWrapper.Config.ddbBackendUrl + name + paramString.toString()
    });
    if (request.status === 200) {
      if (dataType === 'json') {
        return jQuery.parseJSON(request.responseText);
      } else {
        return request.responseText;
      }
    } else {
      return request.statusText;
    }
  }
};