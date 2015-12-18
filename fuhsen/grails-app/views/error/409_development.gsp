<html>
  <head>
    <title><g:message encodeAs="html" code="error.409.title"/> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
    
    <meta name="page" content="409" />
    <meta name="layout" content="main" />
    
  </head>
  <body>
    <div class="errorpage">
      <h1>
        <g:message encodeAs="html" code="error.409.title"/>
      </h1>
      <p>
        <g:message encodeAs="html" code="error.409.body"/>
      </p>
      <hr />
      <g:if test="${error_message}">
        <div>
          <b>DESCRIPTION:</b> ${error_message}
        </div>
      </g:if>
      <g:else>
        <div>
          <b>DESCRIPTION:</b> No description available. This is most likely the cause when you called an url of the DDB-NEXT that does not exist.
        </div>
      </g:else>   
      <hr />
      <g:if test="${apiResponse}">
        <div>
          <b>ADDITIONAL INFORMATION:</b>
        </div>
        <div>
          <ul>
            <li><b>Called URL: </b> ${apiResponse.calledUrl}</li>
            <li><b>HTTP Method: </b> ${apiResponse.method}</li>
            <li><b>HTTP Content Type: </b> ${apiResponse.content}</li>
            <li><b>Request Duration: </b> ${apiResponse.duration}ms</li>
            <li>
              <b>Headers: </b>
              <ul>
                <g:each in="${apiResponse.headers}" var="header">
                  <li>${header}</li>
                </g:each>
              </ul>
            </li>
            <li><b>Response Data </b> ${apiResponse.response}</li>
            <li><b>Attached Exception </b> ${apiResponse.exception}</li>
          </ul>
        </div>
      </g:if>
      <g:else>
        <b>ADDITIONAL INFORMATION:</b> None.
      </g:else>        
    </div>
  </body>
</html>
