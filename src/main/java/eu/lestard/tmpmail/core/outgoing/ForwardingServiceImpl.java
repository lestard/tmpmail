package eu.lestard.tmpmail.core.outgoing;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.lestard.tmpmail.persistence.TempEmailAddress;
import eu.lestard.tmpmail.persistence.User;

public class ForwardingServiceImpl implements ForwardingService {

	private static final Logger LOG = LoggerFactory.getLogger(ForwardingServiceImpl.class);

	private final EntityManagerFactory emf;

	private final String smtpHost;

	private final int smtpPort;

	public ForwardingServiceImpl(EntityManagerFactory emf, String smtpHost, Integer smtpPort) {
		this.emf = emf;
		this.smtpHost = smtpHost;
		this.smtpPort = smtpPort;
	}

	@Override
	public void forwardMessage(MimeMessage message, final TempEmailAddress tempEmailAddress) {

		User user = loadUserFromDatabase(tempEmailAddress);

		replaceRecipient(message, user);

		sendMessage(message);
	}

	protected void sendMessage(final MimeMessage message) {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", smtpHost);

		try {
			Session session = Session.getDefaultInstance(properties);
			Transport transport = session.getTransport(new URLName("smtp", smtpHost, smtpPort, null, "", ""));

			MimeMessage messageToSend = new MimeMessage(message);

			transport.connect();
			transport.sendMessage(messageToSend, message.getAllRecipients());
			transport.close();

		} catch (MessagingException e) {
			LOG.error(e.getMessage(), e);
		}

	}

	protected void replaceRecipient(MimeMessage message, final User user) {
		try {
			message.setRecipient(RecipientType.TO, new InternetAddress(user.getEmailAddress()));
		} catch (final MessagingException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * Load the {@link User} instance that has saved the given
	 * {@link TempEmailAddress}.
	 * 
	 * If no user can be found then <code>null</code> is returned.
	 * 
	 * 
	 * @param tempEmailAddress
	 * @return
	 */
	protected User loadUserFromDatabase(final TempEmailAddress tempEmailAddress) {
		EntityManager entityManager = emf.createEntityManager();

		TypedQuery<User> query = entityManager.createNamedQuery(User.FIND_BY_TEMP_EMAIL_ADDRESS_ID, User.class);

		query.setParameter("id", tempEmailAddress.getId());

		List<User> resultList = query.getResultList();

		if (resultList.isEmpty()) {
			return null;
		}

		return resultList.get(0);
	}

}