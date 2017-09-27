var MailAttachment = {};
MailAttachment.Actions = {};

(function() {
	/**
	 * Alfresco Slingshot aliases
	 */
	var $html = Alfresco.util.encodeHTML, $combine = Alfresco.util.combinePaths, $siteURL = Alfresco.util.siteURL, $isValueSet = Alfresco.util.isValueSet;

	function createIconStyle() {
		var styleText = '/* This style is added as part of the MailAttachment connection extension. */';
		styleText += '\n.toolbar .onActionMailAttachment { background-image: url(/share/res/resources/mail-attachment-share/documentlibrary/actions/send-attachment-icon-16.png); }';

		var style = document.createElement('style');
		style.type = 'text/css';
		if (style.styleSheet){
			style.styleSheet.cssText = styleText;
		} else {
			style.appendChild(document.createTextNode(styleText));
		}
		document.getElementsByTagName('head')[0].appendChild(style);
	}

	createIconStyle();

	MailAttachment.Actions.prototype = {
			/**
			 * @param record array of nodes actioned upon
			 */
			onActionMailAttachment : function _onActionMailAttachment(records) {
				var owner = {
						className: 'onActionMailAttachment'
				};
		         
		         // this nodeRef is the destination for the action
				 // the action does only work with one document
		         var node1 = records[0].nodeRef;

		         // Set the params for the action like in the share-config
		         var action = this.getAction(records, owner),
	               params = {
		             	itemKind: "action",
		             	itemId: "send-as-email",
		             	mode: "create",
		             	destination: node1,
		             	successMessage: "e-mail was sent!",
		             	failureMessage: "Couldn't sent e-mail!"
		             },
		            config =
		            {
		        	 title: this.msg("Send e-mail with attachment(s)")
		            }

		         // Make sure we don't pass the function as a form parameter
		         delete params["function"];

		         // Add configured success callback
		         var success = params["success"];
		         delete params["success"];
		         config.success =
		         {
		            fn: function(response, obj)
		            {
		               // Invoke callback if configured and available
		               if (YAHOO.lang.isFunction(this[success]))
		               {
		                  this[success].call(this, response, obj);
		               }

		               // Fire metadataRefresh so other components may update themselves
		               YAHOO.Bubbling.fire("metadataRefresh", obj);
		            },
		            obj: records,
		            scope: this
		         };

		         // Add configure success message
		         if (params.successMessage)
		         {
		            config.successMessage = this.msg(params.successMessage);
		            delete params["successMessage"];
		         }

		         // Add configured failure callback
		         if (YAHOO.lang.isFunction(this[params.failure]))
		         {
		            config.failure =
		            {
		               fn: this[params.failure],
		               obj: records,
		               scope: this
		            };
		            delete params["failure"];
		         }
		         // Add configure success message
		         if (params.failureMessage)
		         {
		            config.failureMessage = this.msg(params.failureMessage);
		            delete params["failureMessage"];
		         }

		         // Use the remaining properties as form properties
		         config.properties = params;

		         Alfresco.util.PopupManager.displayForm(config);
			}
	};
})();

(function() {
	if (Alfresco.DocumentList) {
		YAHOO.lang.augmentProto(Alfresco.DocumentList, MailAttachment.Actions);
	}

	if (Alfresco.DocListToolbar) {
		YAHOO.lang.augmentProto(Alfresco.DocListToolbar, MailAttachment.Actions);
	}

	if (Alfresco.DocumentActions) {
		YAHOO.lang.augmentProto(Alfresco.DocumentActions, MailAttachment.Actions);
	}

	if (Alfresco.FolderActions) {
		YAHOO.lang.augmentProto(Alfresco.FolderActions, MailAttachment.Actions);
	}

	if (Alfresco.doclib.Actions) {
		YAHOO.lang.augmentProto(Alfresco.doclib.Actions, MailAttachment.Actions);
	}

	if (Alfresco.doclib.FolderActions) {
		YAHOO.lang.augmentProto(Alfresco.doclib.FolderActions, MailAttachment.Actions);
	}
})();