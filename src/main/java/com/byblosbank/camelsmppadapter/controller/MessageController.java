package com.byblosbank.camelsmppadapter.controller;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.byblosbank.camelsmppadapter.model.Message;
import com.byblosbank.camelsmppadapter.model.QueryMessageResult;
import com.byblosbank.camelsmppadapter.services.MessageService;





/**
 * Controller that supports the
 * <ul>
 * <li>The sending of messages {@code SubmitSm}, {@code SubmitMulti}</li>
 * <li>The replacement of messages {@code ReplaceSm}</li>
 * <li>The canceling of messages {@code CancelSm}</li>
 * <li>The querying of messages {@code QuerySm}</li>
 * <li>The sending of data messages {@code DataSm}</li>
 * </ul>
 * 
 * @author Julius Krah
 */
@RestController

@RequestMapping("/api/messages")
public class MessageController {
	
//	private static final Logger log = LoggerFactory.getLogger(MessageController.class);
	private final MessageService service;

	
	public MessageController(MessageService service) {
		this.service = service;
	}
	
	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	String returnHello() {
		return "Hello";
	}
	
	@RequestMapping(value ="/{id}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public QueryMessageResult queryMessage(@PathVariable String id) {
		System.out.println("bobbbbbbb "+3);
		Message message = new Message();
		message.setMessageId(id);
		return service.querySm(message);
	}
}
