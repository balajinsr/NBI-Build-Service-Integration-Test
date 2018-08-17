package com.ca.nbiapps.build.model;

import javax.annotation.Generated;

/**
 * @author Balaji N
 *
 */
public class StepResults {
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
	
	public enum BuildTestStats {
		BUILD_ADJUST_TASKID_STATUS(getBuildStepResult()), BUILD_GIT_TASK(getBuildStepResult()), BUILD_PULL_REQUEST(getBuildStepResult()), BUILD_RESULT_FETCH(getBuildStepResult()), BUILD_STATUS_CHECK(getBuildStepResult()), BUILD_PACKAGE_DOWNLOAD(getBuildStepResult()), BUILD_PACKAGE_ASSERT(getBuildStepResult()), BUILD_DBENTRIES_ASSERT(getBuildStepResult()),
		CON_PACKAGE_TASKIDS_STATUS(getDeployPackStepResults()),CON_PACKAGE(getDeployPackStepResults()), CON_PACKAGE_DOWNLOAD(getDeployPackStepResults()), CON_PACKAGE_ASSERT(getDeployPackStepResults()), CON_MANIFEST_ASSERT(getDeployPackStepResults());

		private BuildTestStats(StepResults... stepResults) {
			for (StepResults step : stepResults) {
				step.setStepName(this.name());
			}
			this.stepResults = stepResults;
		}
		StepResults[] stepResults;
		
		public StepResults[] getStepResults() {
			return stepResults;
		}

		public void setStepResults(StepResults[] stepResults) {
			this.stepResults = stepResults;
		}

		private static StepResults getStepResult(String cycleName) {
			return new Builder().withCycleName(cycleName).withStepStatus("Skipped").withReason("").build();
		}

		private static StepResults getBuildStepResult() {
			return new Builder().withCycleName("Preview").withStepStatus("Skipped").withReason("").build();
		}
		
		private static StepResults[] getDeployPackStepResults() {
			StepResults[] steps = { getStepResult("Preview"), getStepResult("ValFac"), getStepResult("Production") };
			return steps;
		}
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
