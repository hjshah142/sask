package chatbot.core.classifier;

import chatbot.core.handlers.eliza.ElizaHandler;
import chatbot.core.handlers.qa.QAServiceHandler;
import chatbot.core.handlers.rivescript.RiveScriptQueryHandler;
import chatbot.core.handlers.sessa.SessaHandler;
import chatbot.io.incomingrequest.FeedbackRequest;
import chatbot.io.incomingrequest.IncomingRequest;
import chatbot.io.incomingrequest.RequestContent;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class IntentLearnerTest {
	
	public IncomingRequest createInitialRequest(String incomingText) {
	
		IncomingRequest request =new IncomingRequest() ;
		RequestContent addingContent = new RequestContent();	
		addingContent.setPayload("text");
		addingContent.setText(incomingText);
		List<RequestContent> messageData = 	new ArrayList<RequestContent>();
		messageData.add(addingContent);
		request.setRequestContent(messageData);
		return request;
	}
	
	public Object classifyInput(IncomingRequest input) {
		IntentLearner intentLeaner = new IntentLearner(null);
		Object actualOutput =intentLeaner.classify(input);	
		return actualOutput;
	}

	@Test
	public void testClassifyForRiveScript() {

		IncomingRequest input = createInitialRequest("Hello");
		Object actualOutput= classifyInput(input);
        assertTrue(actualOutput instanceof RiveScriptQueryHandler);
	}
	
	@Test
	public void testClassifyForQAHandler() {
		
		IncomingRequest input = createInitialRequest("what is Obama's birthplace");
		Object actualOutput= classifyInput(input);
        assertTrue(actualOutput instanceof QAServiceHandler);
	}

	// TODO: 21/01/2019 Sajjad: Below two tests are ingored for the purpose of demonstration
	//TODO: they should be included once further work on these features is required
	@Ignore
	@Test
	public void testClassifyForElizaHandler() {

		IncomingRequest input = createInitialRequest("I want to know something");
		Object actualOutput= classifyInput(input);
        assertTrue(actualOutput instanceof ElizaHandler);
	}

	@Ignore
	@Test
	public void testClassifyForSessaHandler() {
		
		IncomingRequest input = createInitialRequest("obama wife");
		Object actualOutput= classifyInput(input);
        assertTrue(actualOutput instanceof SessaHandler);
	}
	
	@AfterClass
	public static void tearDown() {
		FeedbackRequest feedback = new FeedbackRequest();
		feedback.setFeedback("negative");
		feedback.setQuery("obama wife");
		IntentLearner intentLeaner = new IntentLearner(null);
		intentLeaner.processFeedback(feedback);
	}
}
