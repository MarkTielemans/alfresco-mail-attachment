var MailAttachment = {};
MailAttachment.Actions = {};

(function() {
	/**
	 * Alfresco Slingshot aliases
	 */
	var $html = Alfresco.util.encodeHTML, $combine = Alfresco.util.combinePaths, $siteURL = Alfresco.util.siteURL, $isValueSet = Alfresco.util.isValueSet;

	function createIconStyle() {
		var styleText = '/* This style is added as part of the MailAttachment connection extension. */';
		styleText += '\n.toolbar .onActionMailAttachment { background-image: url(/share/res/mail-attachment-share/documentlibrary/actions/send-attachment-icon-16.png); }';
		
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
			var nodeRefs = '';
			records.forEach(function(item, index) {
				var uuid = item.nodeRef.replace('workspace://SpacesStore/', '')
				nodeRefs += uuid + ',';
			});
			nodeRefs = nodeRefs.substring(0, nodeRefs.length - 1);
			var url = '/share/openatos/archiveren/index.html#' + nodeRefs;
			var windowOptions = 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=yes, height=600, width=900';
			window.open(url, 'documentarchive', windowOptions);
		},
	
		onActionMailAttachmentSingleNode : function _onActionMailAttachmentSingleNode (record) {
			var url = '/share/openatos/archiveren/index.html#' + record.nodeRef.replace('workspace://SpacesStore/', '');
			var windowOptions = 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=yes, height=600, width=900';
			window.open(url, 'documentarchive', windowOptions);
		}
	};
})();

(function() {
	if (Alfresco.DocumentList) {
		YAHOO.lang.augmentProto(Alfresco.DocumentList, OS.doclib.Actions);
	}

	if (Alfresco.DocListToolbar) {
		YAHOO.lang.augmentProto(Alfresco.DocListToolbar, OS.doclib.Actions);
	}

	if (Alfresco.DocumentActions) {
		YAHOO.lang.augmentProto(Alfresco.DocumentActions, OS.doclib.Actions);
	}

	if (Alfresco.FolderActions) {
		YAHOO.lang.augmentProto(Alfresco.FolderActions, OS.doclib.Actions);
	}

	if (Alfresco.doclib.Actions) {
		YAHOO.lang.augmentProto(Alfresco.doclib.Actions, OS.doclib.Actions);
	}

	if (Alfresco.doclib.FolderActions) {
		YAHOO.lang.augmentProto(Alfresco.doclib.FolderActions, OS.doclib.Actions);
	}
})();