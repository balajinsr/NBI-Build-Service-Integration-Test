package com.ca.nbiapps.build.model;

import javax.annotation.Generated;

/**
 * @author Balaji N
 *
 */
public class StepResults implements Cloneable {
	
	private String cycleName;
	private String stepName;
	private long stepDuration;
	private String stepStatus = "Skipped";
	private String reason;

	@Generated("SparkTools")
	private StepResults(Builder builder) {
		this.cycleName = builder.cycleName;
		this.stepName = builder.stepName;
		this.stepDuration = builder.stepDuration;
		this.stepStatus = builder.stepStatus;
		this.reason = builder.reason;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public long getStepDuration() {
		return stepDuration;
	}

	public void setStepDuration(long stepDuration) {
		this.stepDuration = stepDuration;
	}

	public String getStepStatus() {
		return stepStatus;
	}

	public void setStepStatus(String stepStatus) {
		this.stepStatus = stepStatus;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	@Override
	public String toString() {
		return "StepResults [cycleName=" + cycleName + ", stepName=" + stepName + ", stepDuration=" + stepDuration + ", stepStatus=" + stepStatus + ", reason=" + reason + "]";
	}
	

	public String getCycleName() {
		return cycleName;
	}

	public void setCycleName(String cycleName) {
		this.cycleName = cycleName;
	}

	/**
	 * Creates builder to build {@link StepResults}.
	 * @return created builder
	 */
	@Generated("SparkTools")
	public static Builder builder() {
		return new Builder();
	}



	/**
	 * Builder to build {@link StepResults}.
	 */
	@Generated("SparkTools")
	public static final class Builder {
		private String cycleName;
		private String stepName;
		private long stepDuration;
		private String stepStatus;
		private String reason;

		private Builder() {
		}

		public Builder withCycleName(String cycleName) {
			this.cycleName = cycleName;
			return this;
		}

		public Builder withStepName(String stepName) {
			this.stepName = stepName;
			return this;
		}

		public Builder withStepDuration(long stepDuration) {
			this.stepDuration = stepDuration;
			return this;
		}

		public Builder withStepStatus(String stepStatus) {
			this.stepStatus = stepStatus;
			return this;
		}

		public Builder withReason(String reason) {
			this.reason = reason;
			return this;
		}

		public StepResults build() {
			return new StepResults(this);
		}
	}
}
