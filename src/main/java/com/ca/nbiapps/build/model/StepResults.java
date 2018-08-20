package com.ca.nbiapps.build.model;

import java.util.ArrayList;
import java.util.List;

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

	public StepResults() {
	}
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
		BUILD_ADJUST_TASKID_STATUS(getBuildStepList("Preview")), BUILD_GIT_TASK(getBuildStepList("Preview")), BUILD_PULL_REQUEST(getBuildStepList("Preview")), BUILD_RESULT_FETCH(getBuildStepList("Preview")), BUILD_STATUS_CHECK(getBuildStepList("Preview")), BUILD_PACKAGE_DOWNLOAD(getBuildStepList("Preview")), BUILD_PACKAGE_ASSERT(getBuildStepList("Preview")), BUILD_DBENTRIES_ASSERT(getBuildStepList("Preview")),
		CON_PACKAGE_TASKIDS_STATUS(getDeployPackStepResults()),CON_PACKAGE(getDeployPackStepResults()), CON_PACKAGE_DOWNLOAD(getDeployPackStepResults()), CON_PACKAGE_ASSERT(getDeployPackStepResults()), CON_MANIFEST_ASSERT(getDeployPackStepResults());

		private BuildTestStats(List<StepResults> stepResults) {
			for (StepResults step : stepResults) {
				step.setStepName(this.name());
			}
			this.stepResults = stepResults;
		}
		
		List<StepResults> stepResults;
		
		public List<StepResults> getStepResults() {
			return stepResults;
		}

		public void setStepResults(List<StepResults> stepResults) {
			this.stepResults = stepResults;
		}

		public static List<StepResults> getBuildStepList(String cycleName) {
			List<StepResults> results = new ArrayList<>();
			results.add(new Builder().withCycleName(cycleName).withStepStatus("Skipped").withReason("").build());
			return results;
		}
		
		public static StepResults getStepResults(String cycleName) {
			return new Builder().withCycleName(cycleName).withStepStatus("Skipped").withReason("").build();
		}
		
		public static List<StepResults> getDeployPackStepResults() {
			List<StepResults> results = new ArrayList<>();
			results.add(getStepResults("Preview"));
			results.add(getStepResults("ValFac"));
			results.add(getStepResults("Production"));
			return results;
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
