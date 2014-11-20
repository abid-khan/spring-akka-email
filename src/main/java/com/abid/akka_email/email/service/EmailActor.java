package com.abid.akka_email.email.service;

import static akka.actor.SupervisorStrategy.escalate;
import static akka.actor.SupervisorStrategy.resume;
import static akka.actor.SupervisorStrategy.stop;
import static com.abid.akka_email.email.config.SpringExtension.SpringExtProvider;

import javax.inject.Named;
import javax.mail.MessagingException;

import org.springframework.context.annotation.Scope;

import scala.concurrent.duration.Duration;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.UntypedActor;
import akka.japi.Function;

@Named(value = "EmailActor")
@Scope("prototype")
public class EmailActor extends UntypedActor {
	private static SupervisorStrategy strategy = new OneForOneStrategy(10,
			Duration.create("1 minute"), new Function<Throwable, Directive>() {

				public Directive apply(Throwable arg0) throws Exception {
					if (arg0 instanceof MessagingException) {
						return resume();
					} else if (arg0 instanceof Exception) {
						return stop();
					} else {
						return escalate();
					}
				}

			});

	@Override
	public void onReceive(Object message) {
		getContext().actorOf(
				SpringExtProvider.get(getContext().system()).props(
						"EmailWorkerActor"), "EmailWorkerActor").tell(message,
				self());
	}

	@Override
	public SupervisorStrategy supervisorStrategy() {
		return strategy;
	}

}