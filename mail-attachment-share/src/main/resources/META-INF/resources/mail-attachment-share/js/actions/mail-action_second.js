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
				
				var actionUrl = Alfresco.constants.PROXY_URI + "/api/mail-with-attachment/send";

		         var mailDialog = new AlfDialog({
		             generatePubSubScope: false,
		             title: this.message("Send an email with attachments"),
		             widgetsContent: [
		                {
		                   name: "alfresco/documentlibrary/views/AlfDocumentListView",
		                   config: {
		                      additionalCssClasses: "no-highlight",
		                      currentData: {
		                         items: records
		                      },
		                      widgets: [
		                         {
		                            name: "alfresco/documentlibrary/views/layouts/Row",
		                            config: {
		                               widgets: [
		                                  {
		                                     name: "alfresco/documentlibrary/views/layouts/Cell",
		                                     config: {
		                                        widgets: [
		                                           {
		                                              name: "alfresco/renderers/SmallThumbnail"
		                                           }
		                                        ]
		                                     }
		                                  },
		                                  {
		                                     name: "alfresco/documentlibrary/views/layouts/Cell",
		                                     config: {
		                                        widgets: [
		                                           {
		                                              name: "alfresco/renderers/Property",
		                                              config: {
		                                                 propertyToRender: "node.properties.cm:name"
		                                              }
		                                           }
		                                        ]
		                                     }
		                                  },
		                                 {
			                              	name : "alfresco/forms/controls/DojoValidationTextBox",
			                            	config : {
			                            		fieldId : "EMAIL",
			                            		name : "email",
			                            		label : "Contact",
			                            		description : "Your e-mail address",
			                            		placeHolder : "e-mail",
			                            		validationConfig : [ {
			                            			validation : "regex",
			                            			regex : "^([0-9a-zA-Z]([-.w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-w]*[0-9a-zA-Z].)+[a-zA-Z]{2,9})$",
			                            			errorMessage : "Valid E-mail Address Required"
			                            		} ]
		                            		}
		                                 }
		                               ]
		                            }
		                         }
		                      ]
		                   }
		                }
		             ],
		             widgetsButtons: [
		                {
		                   name: "alfresco/buttons/AlfButton",
		                   config: {
		                      label: this.message("services.ActionService.button.ok"),
		                      publishTopic: responseTopic,
		                      publishPayload: {
		                         nodes: records,
		                         completionTopic: payload.completionTopic
		                      }
		                   }
		                },
		                {
		                   name: "alfresco/buttons/AlfButton",
		                   config: {
		                      label: this.message("services.ActionService.button.cancel"),
		                      publishTopic: "close"
		                   }
		                }
		             ]
		          });
		         mailDialog.show();
			},
			
		      /**
		       * This function is called when the user confirms that they wish to delete a document
		       *
		       * @instance
		       * @param {object} payload An object containing the details of the document(s) to be deleted.
		       */
			onActionMailAttachmentConfirmation: function alfresco_services_ActionService__onActionMailAttachmentConfirmation(payload) {
		       //  this.alfUnsubscribeSaveHandles([this._actionDeleteHandle]);

		         var nodeRefs = array.map(payload.nodes, function(item) {
		            return item.nodeRef;
		         });
		         var responseTopic = this.generateUuid();
		         var subscriptionHandle = this.alfSubscribe(responseTopic + "_SUCCESS", lang.hitch(this, this.onActionDeleteSuccess), true);

		         this.serviceXhr({
		            alfTopic: responseTopic,
		            subscriptionHandle: subscriptionHandle,
		            url: AlfConstants.PROXY_URI + "api/mail-with-attachment/send",
		            method: "POST",
		            data: {
		               nodeRefs: nodeRefs
		            }
		         });
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