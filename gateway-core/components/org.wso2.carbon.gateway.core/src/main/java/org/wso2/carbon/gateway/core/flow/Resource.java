package org.wso2.carbon.gateway.core.flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.carbon.gateway.core.config.AnnotationNotSupportedException;
import org.wso2.carbon.gateway.core.config.ConfigConstants;
import org.wso2.carbon.gateway.core.config.annotations.Annotation;
import org.wso2.carbon.gateway.core.config.annotations.IAnnotation;
import org.wso2.carbon.gateway.core.config.annotations.common.Description;
import org.wso2.carbon.gateway.core.config.annotations.resource.Trigger;
import org.wso2.carbon.gateway.core.flow.templates.uri.URITemplate;
import org.wso2.carbon.gateway.core.flow.triggers.EndpointTrigger;
import org.wso2.carbon.gateway.core.flow.triggers.HTTPEndpointTrigger;
import org.wso2.carbon.gateway.core.inbound.InboundEndpoint;
import org.wso2.carbon.messaging.CarbonCallback;
import org.wso2.carbon.messaging.CarbonMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Object model representing a single resource.
 */
public class Resource {
    private static final Logger log = LoggerFactory.getLogger(Resource.class);

    /**
     * Metadata holders
     */
    private String name;
    private Map<String, Annotation> annotations = new HashMap<>();
    private EndpointTrigger trigger;


    public Resource(String name, InboundEndpoint source, String condition, URITemplate uriTemplate,
                    EndpointTrigger trigger) {
        this.name = name;
        this.trigger = trigger;

        annotations.put(ConfigConstants.AN_DESCRIPTION, new Description());
        annotations.put(ConfigConstants.AN_TRIGGER, new Trigger(getTriggerInstance(source, condition, uriTemplate)));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean receive(CarbonMessage carbonMessage, CarbonCallback carbonCallback) {
        log.info("Resource " + name + " received a message.");
        return true;
    }

    public Annotation getAnnotation(String name) {
        return annotations.get(name);
    }

    public Map<String, Annotation> getAnnotations() {
        return annotations;
    }

    public void addAnnotation(String name, IAnnotation value) throws AnnotationNotSupportedException {
        if (annotations.get(name) != null) {
            annotations.get(name).setValue(value);
        } else {
            throw new AnnotationNotSupportedException("Annotation " + name + " is not supported by Integration");
        }
    }

    private EndpointTrigger getTriggerInstance(InboundEndpoint source, String condition, URITemplate uriTemplate) {
        String protocol = source.getProtocol();

        switch (protocol) {
            case "HTTP":
                return new HTTPEndpointTrigger(source, condition, uriTemplate);
            default:
                return new HTTPEndpointTrigger(source, condition, uriTemplate);
        }
    }
}