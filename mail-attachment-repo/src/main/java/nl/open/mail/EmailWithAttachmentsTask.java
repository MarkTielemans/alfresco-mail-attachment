package nl.open.mail;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;

public class EmailWithAttachmentsTask {

	private NodeRef[] attachments;
	private String[] recipients;
	private String subject;
	private NodeRef template;
	private Map<String, Serializable> templateModel;

	public EmailWithAttachmentsTask() {}
	
	public EmailWithAttachmentsTask(final NodeRef[] attachments, final String[] recipients, final String subject, 
			final NodeRef template, final Map<String, Serializable> templateModel) {
		super();
		this.attachments = attachments;
		this.recipients = recipients;
		this.subject = subject;
		this.template = template;
		this.templateModel = templateModel;
	}

	public NodeRef[] getAttachments() {
		return attachments;
	}

	public String[] getRecipients() {
		return recipients;
	}

	public String getSubject() {
		return subject;
	}

	public NodeRef getTemplate() {
		return template;
	}

	public Map<String, Serializable> getTemplateModel() {
		return templateModel;
	}

	public void setAttachments(final NodeRef... attachments) {
		this.attachments = attachments;
	}
	
	public void setRecipients(final String... recipients) {
		this.recipients = recipients;
	}

	public void setSubject(final String subject) {
		this.subject = subject;
	}

	public void setTemplate(final NodeRef template) {
		this.template = template;
	}

	public void setTemplateModel(final Map<String, Serializable> templateModel) {
		this.templateModel = templateModel;
	}
}