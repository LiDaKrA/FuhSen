<html>
  <head>
    <title><g:message encodeAs="html" code="error.500.title"/> - <g:message encodeAs="html" code="ddbnext.Deutsche_Digitale_Bibliothek"/></title>
    
    <meta name="page" content="500" />
    <meta name="layout" content="main" />
    
  </head>
  <body>
    <div class="errorpage">
      <h1>
        <g:message encodeAs="html" code="error.500.title"/>
      </h1>
      <p>
        <g:message encodeAs="html" code="error.500.body"/>
      </p>
      <hr />
      <g:if test="${exception}">
        <g:renderException exception="${exception}" />
      </g:if>
      <g:else>
        <b>DEV-Message:</b> No stacktrace available. Most likely, it was already consumed and logged to your console.
      </g:else>  
    </div>
    
  </body>
</html>
