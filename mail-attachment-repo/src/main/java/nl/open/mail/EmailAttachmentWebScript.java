package nl.open.mail;

import java.util.Map;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class EmailAttachmentWebScript extends DeclarativeWebScript {

	@Override
	protected void executeFinallyImpl(final WebScriptRequest req, final Status status, 
			final Cache cache, final Map<String, Object> model) {
		
	}
}
