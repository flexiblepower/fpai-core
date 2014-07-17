package org.flexiblepower.runtime.wiring;

import org.flexiblepower.control.ControllerManager;
import org.flexiblepower.rai.CommunicationValidator;
import org.flexiblepower.rai.ControllableResource;
import org.flexiblepower.rai.ResourceController;
import org.flexiblepower.rai.ResourceMessageSubmitter;
import org.flexiblepower.rai.ResourceType;
import org.flexiblepower.rai.comm.Allocation;
import org.flexiblepower.rai.comm.AllocationStatusUpdate;
import org.flexiblepower.rai.comm.ControlSpaceRegistration;
import org.flexiblepower.rai.comm.ControlSpaceUpdate;
import org.flexiblepower.rai.comm.ResourceMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControlCommunication {

    private final static Logger log = LoggerFactory.getLogger(ControlCommunication.class);

    private final ResourceMessageSubmitter controllerConnection = new ResourceMessageSubmitter() {

        @Override
        public void submitResourceMessage(ResourceMessage resourceMessage) {
            if (resourceMessage instanceof Allocation) {
                ControlCommunication.this.handleAllocation((Allocation) resourceMessage);
            } else {
                throw new IllegalArgumentException("Received unexpected type of resourceMessage form ResourceController: " + resourceMessage.getClass()
                                                                                                                                            .getName());
            }
        }
    };

    private final ResourceMessageSubmitter controllableResourceConnection = new ResourceMessageSubmitter() {

        @Override
        public void submitResourceMessage(ResourceMessage resourceMessage) {
            if (resourceMessage instanceof ControlSpaceRegistration) {
                ControlCommunication.this.handleControlSpaceRegistration((ControlSpaceRegistration) resourceMessage);
            } else if (resourceMessage instanceof ControlSpaceUpdate) {
                ControlCommunication.this.handleControlSpaceUpdate((ControlSpaceUpdate) resourceMessage);
            } else if (resourceMessage instanceof AllocationStatusUpdate) {
                ControlCommunication.this.handleAllocationStatusUpdate((AllocationStatusUpdate) resourceMessage);
            } else {
                throw new IllegalArgumentException("Received unexpected type of resourceMessage form ControllableResource: " + resourceMessage.getClass()
                                                                                                                                              .getName());
            }
        }
    };

    private final CommunicationValidator<? super Allocation, ? super ControlSpaceRegistration, ? super ControlSpaceUpdate> communicationValidator;
    private final ResourceType<?, ?, ?> resourceType;
    private final ControllerManager controllerManager;
    private final ControllableResource<? super Allocation> controllableResource;
    private final ResourceController<? super ControlSpaceRegistration, ? super ControlSpaceUpdate> resourceController;
    private ControlSpaceRegistration controlSpaceRegistration = null;

    public ControlCommunication(ControllerManager controllerManager, ControllableResource controllableResource) throws InstantiationException,
                                                                                                               IllegalAccessException {
        if (controllerManager == null || controllableResource == null) {
            throw new NullPointerException("ControllerManager or ControllableResource cannot be null when creating a ControlCommunication");
        }
        resourceType = controllableResource.getResourceType();
        communicationValidator = (CommunicationValidator) resourceType.getCommunicationValidatorClass().newInstance();
        this.controllerManager = controllerManager;
        this.controllableResource = controllableResource;
        resourceController = (ResourceController<ControlSpaceRegistration, ControlSpaceUpdate>) this.controllerManager.registerResource(this.controllableResource,
                                                                                                                                        resourceType);
        controllableResource.initialize(controllableResourceConnection);
    }

    public void disconnect() {
        controllerManager.unregisterResource(controllableResource);
        controllableResource.disconnect();
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public ResourceMessageSubmitter getControllerConnection() {
        return controllerConnection;
    }

    public ResourceMessageSubmitter getControllableResourceConnection() {
        return controllableResourceConnection;
    }

    protected void handleControlSpaceRegistration(ControlSpaceRegistration controlSpaceRegistration) {
        if (this.controlSpaceRegistration == null) {
            communicationValidator.validateControlSpaceRegistration(controlSpaceRegistration);
            this.controlSpaceRegistration = controlSpaceRegistration;
            resourceController.initialize(controlSpaceRegistration, controllerConnection);
        } else {
            throw new IllegalStateException("ControllableResource can only submit a ControlSpaceRegistration once");
        }
    }

    protected void handleControlSpaceUpdate(ControlSpaceUpdate resourceMessage) {
        if (controlSpaceRegistration == null) {
            throw new IllegalStateException("ControllableResource can only submit a ControlSpaceUpdate after a ControlSpaceRegistration is sent");
        } else {
            resourceController.handleResourceUpdate(resourceMessage);
        }
    }

    protected void handleAllocation(Allocation allocation) {
        if (controlSpaceRegistration == null) {
            throw new IllegalStateException("ResourceController can only submit an Allocation after a ControlSpaceRegistration is sent");
        } else {
            controllableResource.handleAllocation(allocation);
        }
    }

    protected void handleAllocationStatusUpdate(AllocationStatusUpdate allocationStatusUpdate) {
        if (controlSpaceRegistration == null) {
            throw new IllegalStateException("ControllableResource can only submit an AllocationStatusUpdate after a ControlSpaceRegistration is sent");
        } else {
            resourceController.handleAllocationStatusUpdate(allocationStatusUpdate);
        }
    }

}
