package nl.open.mail;

import java.io.IOException;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.util.Assert;

public class EmailAttachmentWebScript extends DeclarativeWebScript {

    private static final String PARAM_AS_LINK = "asLinks";
    private static final String PARAM_ATTACHMENTS = "attachments";
    private static final String PARAM_RECIPIENTS = "recipients";
    private static final String PARAM_SUBJECT = "subject";
    private static final String PARAM_TEMPLATE = "template";

    private static final Logger logger = LogManager.getLogger(EmailAttachmentWebScript.class);

    private EMailWithAttachmentsService emailWithAttachmentsService;

    protected NodeRef[] asNodeRefs(final String... refs) {
        final NodeRef[] nodeRefs = new NodeRef[refs.length];
        for (int i = 0; i < refs.length; i++) {
            nodeRefs[i] = new NodeRef(refs[i]);
        }

        return nodeRefs;
    }

    private void checkAvailability(final WebScriptRequest req, final Status status, final String... paramKeys) {
        try {
            for (final String paramKey : paramKeys) {
                Assert.hasText(req.getParameter(paramKey), 
                        "Parameter " + paramKey + " is mandatory!");
            }
        } catch (final IllegalArgumentException iae) {
            status.setCode(HttpStatus.SC_BAD_REQUEST);
            throw iae;
        }
    }

    @Override
    protected void executeFinallyImpl(final WebScriptRequest req, final Status status, 
            final Cache cache, final Map<String, Object> model) {

        checkAvailability(req, status, PARAM_ATTACHMENTS, PARAM_RECIPIENTS);

        final EmailWithAttachmentsTask task = new EmailWithAttachmentsTask();
        task.setAttachments(asNodeRefs(req.getParameterValues(PARAM_ATTACHMENTS)));
        task.setRecipients(req.getParameterValues(PARAM_RECIPIENTS));
        task.setSubject(req.getParameter(PARAM_SUBJECT));

        if (StringUtils.isNotBlank(req.getParameter(PARAM_TEMPLATE))) {
            task.setTemplate(new NodeRef(req.getParameter(PARAM_TEMPLATE)));
        }
        
        if (StringUtils.isNotBlank(req.getParameter(PARAM_AS_LINK))) {
            task.setSendLinkOnly("on|true".contains(req.getParameter(PARAM_AS_LINK)));
        }

        try {
            emailWithAttachmentsService.sendEmail(task);
        } catch (final IOException e) {
            logger.error("Failed to send email with attachments", e);
            status.setException(e);
            status.setCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

    public final void setEmailWithAttachmentsService(final EMailWithAttachmentsService emailWithAttachmentsService) {
        this.emailWithAttachmentsService = emailWithAttachmentsService;
    }
}
