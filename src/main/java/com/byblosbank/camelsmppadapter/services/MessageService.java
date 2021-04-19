package com.byblosbank.camelsmppadapter.services;



import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.smpp.SmppCommandType;
import org.apache.camel.component.smpp.SmppConstants;

import org.springframework.stereotype.Service;

import com.byblosbank.camelsmppadapter.model.Message;
import com.byblosbank.camelsmppadapter.model.MessageType;
import com.byblosbank.camelsmppadapter.model.QueryMessageResult;
import static com.byblosbank.camelsmppadapter.CamelSmppAdapterApplication.toZonedDateTime;
import static com.byblosbank.camelsmppadapter.CamelSmppAdapterApplication.toLocalDateTime;



/**
 * Service that determines type of message to send
 * @author Julius Krah
 *
 */
@Service
public class MessageService {
	//private static final Logger log = LoggerFactory.getLogger(MessageService.class);
	private  ProducerTemplate template;
	private  CamelContext context;

	public MessageService(ProducerTemplate template, CamelContext context) {
		this.template = template;
		this.context = context;
	}

	public void send(Message message) {
		MessageType type = message.getType();
		switch (type) {
		case SUBMIT_SM:
			List<String> messageIds = submitSm(message);
			//log.info("From submit-sm {}", messageIds);
			break;
		case REPLACE_SM:
			replaceSm(message);
			break;
		case QUERY_SM:
			QueryMessageResult result = querySm(message);
		//	log.info("From query {}", result);
			break;
		case CANCEL_SM:
			cancelSm(message);
			break;
		case DATA_SHORT_MESSAGE:
			dataSm(message);
			break;
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> submitSm(Message message) {
		List<String> destinationAddresses = message.getTo();
		Date scheduleDeliveryTime = toDate(message.getScheduleDeliveryTime());
		//log.info("Sending messages to {}", destinationAddresses);

		ExchangeBuilder builder = ExchangeBuilder.anExchange(context) //
				.withHeader(SmppConstants.DEST_ADDR, destinationAddresses) //
				.withBody(message.getContent());
		if (destinationAddresses.size() > 1)
			builder.withHeader(SmppConstants.COMMAND, SmppCommandType.SUBMIT_MULTI.getCommandName());
		if (scheduleDeliveryTime != null)
			builder.withHeader(SmppConstants.SCHEDULE_DELIVERY_TIME, scheduleDeliveryTime);
		if (message.getFrom() != null)
			builder.withHeader(SmppConstants.SOURCE_ADDR, message.getFrom());

		Exchange result = template.send("smpp://{{camel.component.smpp.configuration.host}}", builder.build());
		if (result.getException() == null)
			return result.getMessage().getHeader(SmppConstants.ID, List.class);
		else
			throw new RuntimeException(result.getException());
	}

	private void replaceSm(Message message) {

	}

	public QueryMessageResult querySm(Message message) {
		ExchangeBuilder builder = ExchangeBuilder.anExchange(context) //
				.withHeader(SmppConstants.ID, message.getMessageId()) //
				.withHeader(SmppConstants.COMMAND, SmppCommandType.QUERY_SM.getCommandName());
		if (message.getFrom() != null)
			builder.withHeader(SmppConstants.SOURCE_ADDR, message.getFrom());
		Exchange result = template.send("smpp://{{camel.component.smpp.configuration.host}}", builder.build());
		if (result.getException() == null) {
			QueryMessageResult queryResult = new QueryMessageResult();
			Date finalDate = result.getMessage().getHeader(SmppConstants.FINAL_DATE, Date.class);
			queryResult.setErrorCode(result.getMessage().getHeader(SmppConstants.ERROR, byte.class));
			queryResult.setMessageId(result.getMessage().getHeader(SmppConstants.ID, String.class));
			queryResult.setFinalDate(fromDate(finalDate));
			queryResult.setMessageStatus(result.getMessage().getHeader(SmppConstants.MESSAGE_STATE, String.class));
			result.getMessage().getHeader(SmppConstants.ID, List.class);
			return queryResult;
		} else
			throw new RuntimeException(result.getException());

	}

	private void cancelSm(Message message) {

	}

	private void dataSm(Message message) {

	}

	private Date toDate(LocalDateTime dateTime) {
		ZonedDateTime zonedDateTime = toZonedDateTime(dateTime);
		if (zonedDateTime == null)
			return null;
		return Date.from(zonedDateTime.toInstant());
	}

	private LocalDateTime fromDate(Date date) {
		if (date == null)
			return null;
		return toLocalDateTime(date.toInstant());
	}

}
