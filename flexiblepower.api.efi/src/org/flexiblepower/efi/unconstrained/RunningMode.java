package org.flexiblepower.efi.unconstrained;

import java.util.ArrayList;
import java.util.List;

import javax.measure.Measurable;

public class RunningMode {
	private final int id;

	private final String name;

	// Commodity consumed or produced
	// TODO
	private List<Measurable<?>> commoditiesPerSecond;
	
	private Double runningCostsPerSecond;

	private List<Transition> possibleTransition;

	public RunningMode(int id, String name, List<Measurable<?>> commodities, Double runningCostsPerSecond) {
		this.id = id;
		this.name = name;
		this.commoditiesPerSecond = commodities;
		this.runningCostsPerSecond = runningCostsPerSecond;
	}
}
