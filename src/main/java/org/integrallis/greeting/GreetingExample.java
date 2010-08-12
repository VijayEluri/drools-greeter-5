package org.integrallis.greeting;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;

import static org.integrallis.greeting.Person.Education.*;
import static org.integrallis.greeting.Person.Gender.*;
import static org.integrallis.greeting.Person.MaritalStatus.*;

public class GreetingExample {

	public static final void main(String[] args) {
		try {
			// 1 - load the rules in to a knowledge builder
			KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
			knowledgeBuilder.add(ResourceFactory.newClassPathResource("greetings.drl"), ResourceType.DRL);
			KnowledgeBuilderErrors errors = knowledgeBuilder.getErrors();
			if (errors.size() > 0) {
				for (KnowledgeBuilderError error: errors) {
					System.err.println(error);
				}
				throw new IllegalArgumentException("Could not parse knowledge.");
			}
			// 2 - create a knowledge base using a knowledge builder
			KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
			knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
			
			// 3 - create a stateful knowledge session
			StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
			
			// create a logger for the knowledge session
			KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(knowledgeSession, "test");
			
			// 4 - create and assert some facts
			TimeOfDay nineAm = new TimeOfDay(9, 0);
			
			Person stephenFalken = new Person("Stephen Falken", MALE, 48, SINGLE, PHD);
			Person richie = new Person("Richie Rich", MALE, 9, SINGLE, NONE);
			Person marieCurie = new Person("Marie Curie", FEMALE, 45, MARRIED, PHD);

			knowledgeSession.insert(nineAm);
			knowledgeSession.insert(marieCurie);
			knowledgeSession.insert(stephenFalken);
			knowledgeSession.insert(richie);
		
			// 5 - fire the rules
			knowledgeSession.fireAllRules();
			
			logger.close();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}