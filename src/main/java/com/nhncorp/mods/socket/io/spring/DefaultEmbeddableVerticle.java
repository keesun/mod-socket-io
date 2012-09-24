package com.nhncorp.mods.socket.io.spring;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.vertx.java.core.Vertx;

import javax.annotation.PostConstruct;

/**
 * @author Keesun Baik
 */
public abstract class DefaultEmbeddableVerticle implements EmbeddableVerticle {

	@Autowired
	protected BeanFactory beanFactory;

	@PostConstruct
	public void runVerticle(){
		Vertx vertx = null;
		try {
			vertx = beanFactory.getBean(Vertx.class);
		} catch (NoSuchBeanDefinitionException e) {
			if(host() != null) {
				if(port() != 0) {
					vertx = Vertx.newVertx(port(), host());
				} else {
					vertx = Vertx.newVertx(host());
				}
			} else {
				vertx = Vertx.newVertx();
			}
		}

		beanFactory.getBean(this.getClass()).start(vertx);
	}

	public String host(){
		return null;
	}

	public int port(){
		return 0;
	}

}
