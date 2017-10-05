<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="send-mail">
   <div id="${el}-dialogTitle" class="hd">${msg("title")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="post">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-type">${msg("label.type")}:</label></div>
    <label for="recipients">To:</label>&nbsp;&nbsp;
    <input type="text" name="recipients" />
    <label for="attachments">Attachments:</label>&nbsp;&nbsp;    

    <br /><br />
         </div>
         <div class="bdft">
            <input type="button" id="${el}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>