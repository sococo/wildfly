package org.jboss.as.test.clustering.cluster.dispatcher.bean;

import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.jboss.msc.service.ServiceName;
import org.wildfly.clustering.Node;
import org.wildfly.clustering.dispatcher.Command;
import org.wildfly.clustering.dispatcher.CommandDispatcher;
import org.wildfly.clustering.dispatcher.CommandDispatcherFactory;
import org.wildfly.clustering.dispatcher.CommandResponse;

@Singleton
@Startup
@Local(CommandDispatcher.class)
public class CommandDispatcherBean implements CommandDispatcher<Node> {
    private static final ServiceName SERVICE_NAME = ServiceName.JBOSS.append("test", "dispatcher");

    @EJB
    private CommandDispatcherFactory factory;
    private CommandDispatcher<Node> dispatcher;

    @PostConstruct
    public void init() {
        this.dispatcher = this.factory.createCommandDispatcher(SERVICE_NAME, this.factory.getLocalNode(), null);
    }

    @PreDestroy
    public void destroy() {
        this.close();
    }

    @Override
    public <R> CommandResponse<R> executeOnNode(Command<R, Node> command, Node node) {
        return this.dispatcher.executeOnNode(command, node);
    }

    @Override
    public <R> Map<Node, CommandResponse<R>> executeOnCluster(Command<R, Node> command, Node... excludedNodes) {
        return this.dispatcher.executeOnCluster(command, excludedNodes);
    }

    @Override
    public <R> Future<R> submitOnNode(Command<R, Node> command, Node node) {
        return this.dispatcher.submitOnNode(command, node);
    }

    @Override
    public <R> Map<Node, Future<R>> submitOnCluster(Command<R, Node> command, Node... excludedNodes) {
        return this.dispatcher.submitOnCluster(command, excludedNodes);
    }

    @Override
    public void close() {
        this.dispatcher.close();
    }
}
