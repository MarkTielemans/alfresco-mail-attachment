package nl.open.mail;

import java.io.IOException;
import java.io.Serializable;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.jscript.People;
import org.alfresco.repo.jscript.ScriptNode;
import org.alfresco.repo.jscript.ScriptUtils;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ContentIOException;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.TemplateService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.util.UrlUtil;
import org.apache.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;

public class EMailWithAttachmentsService {

    private static final Logger logger = Logger.getLogger(EMailWithAttachmentsService.class);

    private ContentService contentService;
    private String defaultTemplatePath;
    private JavaMailSender mailService;
    private NodeService nodeService;
    private People people;
    private ScriptUtils scriptUtils;
    private SearchService searchService;
    private SysAdminParams sysAdminParams;
    private TemplateService templateService;
    private ScriptNode person;

    public EMailWithAttachmentsService(final String defaultTemplatePath) {
        this.defaultTemplatePath = defaultTemplatePath;
    }

    private void addAttachment(final NodeRef nodeRef, final MimeMultipart content) throws MessagingException, ContentIOException, IOException {
        final MappedByteBuffer buf;
        final ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
        final String fileName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);

        byte[] array = new byte[0];
        buf = reader.getFileChannel().map(FileChannel.MapMode.READ_ONLY, 0, reader.getSize());
        if (reader.getSize() <= Integer.MAX_VALUE) {
            array = new byte[(int) reader.getSize()];
            buf.get(array);
        }

        final ByteArrayDataSource ds = new ByteArrayDataSource(array, reader.getMimetype());
        final MimeBodyPart attachment = new MimeBodyPart();
        attachment.setFileName(fileName);
        attachment.setDataHandler(new DataHandler(ds));
        content.addBodyPart(attachment);
    }

    private void addAttachments(final EmailWithAttachmentsTask emailTask, final MimeMessage mimeMessage) throws MessagingException, ContentIOException, IOException {
        final MimeMultipart mail = new MimeMultipart("mixed");
        mail.addBodyPart(getMessageBody(emailTask));
        mimeMessage.setContent(mail);

        for (final NodeRef nodeRef : emailTask.getAttachments()) {
            addAttachment(nodeRef, mail);
        }
    }

    private Address[] getAddresses(final String[] recipients) throws AddressException {
        final String s = Arrays.toString(recipients);
        return InternetAddress.parse(s.substring(1, s.length() - 1));
    }

    private MimeBodyPart getMessageBody(final EmailWithAttachmentsTask emailTask) throws MessagingException {
        if (emailTask.getTemplate() == null) {
            emailTask.setTemplate(DefaultTemplateRefCache.getValue(defaultTemplatePath, searchService));
        }

        emailTask.setTemplateModel(getTemplateModel(emailTask));
        final String body = templateService.processTemplate(emailTask.getTemplate().toString(), emailTask.getTemplateModel());
        final MimeBodyPart bodyText = new MimeBodyPart();
        bodyText.setText(body, null, "html");
        return bodyText;
    }

    public void init() {
        // This seems to cause a hang
        //		try {
        //			logger.debug("Resolving default template ref at path: " + this.defaultTemplatePath);
        //			final NodeRef nr = DefaultTemplateRefCache.getValue(defaultTemplatePath, searchService);
        //			logger.info("Resolved default template at " + nr.toString());
        //		} catch (final IllegalArgumentException iae) {
        //			logger.warn("Default template couldn't be resolved on startup", iae);
        //		}
    }

    public void sendEmail(final EmailWithAttachmentsTask emailTask) throws IOException {
        try {
            final MimeMessage mimeMessage = mailService.createMimeMessage();
            person = people.getPerson(AuthenticationUtil.getFullyAuthenticatedUser());
            final String senderEmail = (String) person.getProperties().get(ContentModel.PROP_EMAIL.toPrefixString());

            mimeMessage.setFrom(new InternetAddress(senderEmail));
            mimeMessage.setRecipients(Message.RecipientType.TO, getAddresses(emailTask.getRecipients()));
            mimeMessage.setSubject(emailTask.getSubject());
            mimeMessage.setHeader("Content-Transfer-Encoding", "text/html; charset=UTF-8");

            // Create body with attachments
            addAttachments(emailTask, mimeMessage);

            mailService.send(mimeMessage);
            logger.info("E-mail message sent successfully");
        } catch (final MessagingException ex) {
            throw new IOException("Error processing e-mail", ex);
        }
    }

    public void setContentService(final ContentService contentService) {
        this.contentService = contentService;
    }

    public final void setMailService(final JavaMailSender mailService) {
        this.mailService = mailService;
    }

    public final void setNodeService(final NodeService nodeService) {
        this.nodeService = nodeService;
    }
    
    public final void setPeople(final People people) {
        this.people = people;
    }

    public final void setSearchService(final SearchService searchService) {
        this.searchService = searchService;
    }
    
    public final void setScriptUtils(final ScriptUtils scriptUtils) {
        this.scriptUtils = scriptUtils;
    }
    
    public final void setSysAdminParams(final SysAdminParams sysAdminParams) {
        this.sysAdminParams = sysAdminParams;
    }

    public final void setTemplateService(final TemplateService templateService) {
        this.templateService = templateService;
    }

    private Map<String, Serializable> getTemplateModel(final EmailWithAttachmentsTask emailTask) {
        final List<ScriptNode> nodes = new ArrayList<>(emailTask.getAttachments().length);

        for (final NodeRef attachment : emailTask.getAttachments()) {
            final ScriptNode sn = scriptUtils.getNodeFromString(attachment.toString());
            nodes.add(sn);
        }
        
        final Map<String, Serializable> model = new HashMap<>(1);
        model.put("nodes", (Serializable) nodes);
        model.put("person", person);
        model.put("shareUrl", UrlUtil.getShareUrl(sysAdminParams));
        model.put("date", new Date());
        return model;
    }
}
