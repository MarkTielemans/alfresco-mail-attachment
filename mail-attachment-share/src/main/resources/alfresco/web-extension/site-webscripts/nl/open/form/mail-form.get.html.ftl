<#assign el=args.htmlid?html>


<div id="${el}-dialog" class="send-mail">
   <div id="${el}-dialogTitle" class="hd">${msg("title")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="post">
         <div class="yui-gd">
    <label for="recipients">Recipients:</label>&nbsp;&nbsp;
    <p><input type="text" name="recipients" /></p>
    <label for="subject">Subject:</label>&nbsp;&nbsp;
    <p><input type="text" name="subject" /></p>
    <label for="attachments">Attachments:</label>&nbsp;&nbsp;    
		<div id="attachments" name="attachments"></div><br />
         </div>
         <div class="bdft">
            <input type="button" id="${el}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>