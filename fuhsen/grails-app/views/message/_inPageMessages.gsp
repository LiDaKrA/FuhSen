<div class="messages-container fullwidth-container span12">
  <div>
    <ul class="unstyled">
    <g:each in="${ messages }">
      <li>
        <i class="icon-ok-circle"></i><span><g:message encodeAs="html" code="${it}" /></span>
      </li>
    </g:each>
    </ul>
  </div>
</div>
