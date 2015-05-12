package org.flexiblepower.simulation.ui;

import java.util.Date;
import java.util.Locale;

import org.flexiblepower.context.FlexiblePowerContext;
import org.flexiblepower.simulation.api.Simulation;
import org.flexiblepower.ui.Widget;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

@Component
public class SimulatedTimeWidget implements Widget {
    public static class Parameters {
        private long startTime;
        private long stopTime;
        private double speedFactor;

        public long getStopTime() {
            return stopTime;
        }

        public double getSpeedFactor() {
            return speedFactor;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStopTime(long stopTime) {
            this.stopTime = stopTime;
        }

        public void setSpeedFactor(double speedFactor) {
            this.speedFactor = speedFactor;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }
    }

    public static class Update {
        private final Date time;
        private final Simulation.State state;

        public Simulation.State getState() {
            return state;
        }

        public Update(Date date, Simulation.State state) {
            time = date;
            this.state = state;
        }

        public Date getTime() {
            return time;
        }
    }

    private Simulation simulation;

    @Reference
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    private FlexiblePowerContext context;

    @Reference
    public void setContext(FlexiblePowerContext context) {
        this.context = context;
    }

    @Override
    public String getTitle(Locale locale) {
        return "Simulation Clock";
    }

    public Update startSimulation(Parameters params) {
        if (params.stopTime == 0) {
            simulation.startSimulation(new Date(params.getStartTime()), params.getSpeedFactor());
        } else {
            simulation.startSimulation(new Date(params.getStartTime()),
                                       new Date(params.getStopTime()),
                                       params.getSpeedFactor());
        }
        return update();
    }

    public Update stopSimulation() {
        simulation.stopSimulation();
        return update();
    }

    public Update pauseSimulation() {
        simulation.pause();
        return update();
    }

    public Update unpauseSimulation() {
        simulation.unpause();
        return update();
    }

    public Update changeSpeedFactor(Parameters params) {
        simulation.changeSpeedFactor(params.getSpeedFactor());
        return update();
    }

    public Update update() {
        return new Update(context.currentTime(), simulation.getSimulationClockState());
    }
}
