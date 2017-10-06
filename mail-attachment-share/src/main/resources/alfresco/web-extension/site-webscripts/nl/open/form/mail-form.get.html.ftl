<#assign el=args.htmlid?html>

<div id="${el}-dialog" class="send-mail">
   <div id="${el}-dialogTitle" class="hd">${msg("title")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="post" enctype="application/x-www-form-urlencoded">
         <div class="yui-gd">
            <label for="recipients">${msg("send-email.form.field.recipients")}:</label>&nbsp;&nbsp;
            <p>
              <input type="text" name="recipients" title="${msg("send-email.form.field.recipients.description")}" /></p>
              <label for="subject">${msg("send-email.form.field.subject")}:</label>&nbsp;&nbsp;
            <p>
                <input type="text" name="subject" />
                <div id="attachments-container">
                    <#list args.attachmentRefs?split(",") as attachment>
                        <input type="hidden" name="attachments" value="${attachment}" />
                    </#list>
                </div>
            </p>
         </div>
         <div class="bdft">
            <input type="button" id="${el}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>