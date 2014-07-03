package org.flexiblepower.efi.buffer;

import java.util.ArrayList;
import java.util.List;

import javax.measure.Measurable;

public class RunningMode {
	private final int id;
	
	private final String name;
	
	private List<RunningModeLine> runningMode;
	
	private List<Transition> possibleTransition;
	
	public RunningMode(int id, String name){
		this.id = id;
		this.name = name;
		this.runningMode = new ArrayList<RunningMode.RunningModeLine>();
	}
	
	public void addLine(RunningModeLine line){
		runningMode.add(line);
	}
	
	public static class RunningModeLine{
		
		// Defines the range of the line
		private double lowerBound;
		private double upperBound;
		
		// Charge speed x / seconds
		private double xs;
		
		// Commodity consumed or produced
		// TODO
		private List<Measurable<?>> commoditiesPerSecond;
		
		// Optional: Running costs for this runningmode expressed per second
		private Double runningCostsPerSecond;
		
		public RunningModeLine(double rangeLow, double rangeUp, double xs,
				List<Measurable<?>> commodities,
				Double runningCostsPerSecond) {
			this.upperBound = rangeLow;
			this.lowerBound = rangeUp;
			this.xs = xs;
			this.commoditiesPerSecond = commodities;
			this.runningCostsPerSecond = runningCostsPerSecond;
		} 
	}
}
