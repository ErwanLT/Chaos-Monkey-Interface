package fr.eletutour.chaosmonkey.models;

import java.util.ArrayList;
import java.util.List;

/**
 * AssaultPropertiesUpdate
 */
public class AssaultPropertiesUpdate {

    private Integer level;

    private Boolean deterministic;

    private Integer latencyRangeStart;

    private Integer latencyRangeEnd;

    private Boolean latencyActive;

    private Boolean exceptionsActive;

    private AssaultException exception;

    private Boolean killApplicationActive;

    private String killApplicationCronExpression;

    private Boolean memoryActive;

    private Integer memoryMillisecondsHoldFilledMemory;

    private Integer memoryMillisecondsWaitNextIncrease;

    private Double memoryFillIncrementFraction;

    private Double memoryFillTargetFraction;

    private String memoryCronExpression;

    private Boolean cpuActive;

    private Integer cpuMillisecondsHoldLoad;

    private Double cpuLoadTargetFraction;

    private String cpuCronExpression;

    @Deprecated
    private String runtimeAssaultCronExpression;

    private List<String> watchedCustomServices = new ArrayList<>();

    public AssaultPropertiesUpdate() {
    }

    public AssaultPropertiesUpdate(Integer level, Boolean deterministic, Integer latencyRangeStart, Integer latencyRangeEnd, Boolean latencyActive, Boolean exceptionsActive, AssaultException exception, Boolean killApplicationActive, String killApplicationCronExpression, Boolean memoryActive, Integer memoryMillisecondsHoldFilledMemory, Integer memoryMillisecondsWaitNextIncrease, Double memoryFillIncrementFraction, Double memoryFillTargetFraction, String memoryCronExpression, Boolean cpuActive, Integer cpuMillisecondsHoldLoad, Double cpuLoadTargetFraction, String cpuCronExpression, String runtimeAssaultCronExpression, List<String> watchedCustomServices) {
        this.level = level;
        this.deterministic = deterministic;
        this.latencyRangeStart = latencyRangeStart;
        this.latencyRangeEnd = latencyRangeEnd;
        this.latencyActive = latencyActive;
        this.exceptionsActive = exceptionsActive;
        this.exception = exception;
        this.killApplicationActive = killApplicationActive;
        this.killApplicationCronExpression = killApplicationCronExpression;
        this.memoryActive = memoryActive;
        this.memoryMillisecondsHoldFilledMemory = memoryMillisecondsHoldFilledMemory;
        this.memoryMillisecondsWaitNextIncrease = memoryMillisecondsWaitNextIncrease;
        this.memoryFillIncrementFraction = memoryFillIncrementFraction;
        this.memoryFillTargetFraction = memoryFillTargetFraction;
        this.memoryCronExpression = memoryCronExpression;
        this.cpuActive = cpuActive;
        this.cpuMillisecondsHoldLoad = cpuMillisecondsHoldLoad;
        this.cpuLoadTargetFraction = cpuLoadTargetFraction;
        this.cpuCronExpression = cpuCronExpression;
        this.runtimeAssaultCronExpression = runtimeAssaultCronExpression;
        this.watchedCustomServices = watchedCustomServices;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean getDeterministic() {
        return deterministic;
    }

    public void setDeterministic(Boolean deterministic) {
        this.deterministic = deterministic;
    }

    public Integer getLatencyRangeStart() {
        return latencyRangeStart;
    }

    public void setLatencyRangeStart(Integer latencyRangeStart) {
        this.latencyRangeStart = latencyRangeStart;
    }

    public Integer getLatencyRangeEnd() {
        return latencyRangeEnd;
    }

    public void setLatencyRangeEnd(Integer latencyRangeEnd) {
        this.latencyRangeEnd = latencyRangeEnd;
    }

    public Boolean getLatencyActive() {
        return latencyActive;
    }

    public void setLatencyActive(Boolean latencyActive) {
        this.latencyActive = latencyActive;
    }

    public Boolean getExceptionsActive() {
        return exceptionsActive;
    }

    public void setExceptionsActive(Boolean exceptionsActive) {
        this.exceptionsActive = exceptionsActive;
    }

    public AssaultException getException() {
        return exception;
    }

    public void setException(AssaultException exception) {
        this.exception = exception;
    }

    public Boolean getKillApplicationActive() {
        return killApplicationActive;
    }

    public void setKillApplicationActive(Boolean killApplicationActive) {
        this.killApplicationActive = killApplicationActive;
    }

    public String getKillApplicationCronExpression() {
        return killApplicationCronExpression;
    }

    public void setKillApplicationCronExpression(String killApplicationCronExpression) {
        this.killApplicationCronExpression = killApplicationCronExpression;
    }

    public Boolean getMemoryActive() {
        return memoryActive;
    }

    public void setMemoryActive(Boolean memoryActive) {
        this.memoryActive = memoryActive;
    }

    public Integer getMemoryMillisecondsHoldFilledMemory() {
        return memoryMillisecondsHoldFilledMemory;
    }

    public void setMemoryMillisecondsHoldFilledMemory(Integer memoryMillisecondsHoldFilledMemory) {
        this.memoryMillisecondsHoldFilledMemory = memoryMillisecondsHoldFilledMemory;
    }

    public Integer getMemoryMillisecondsWaitNextIncrease() {
        return memoryMillisecondsWaitNextIncrease;
    }

    public void setMemoryMillisecondsWaitNextIncrease(Integer memoryMillisecondsWaitNextIncrease) {
        this.memoryMillisecondsWaitNextIncrease = memoryMillisecondsWaitNextIncrease;
    }

    public Double getMemoryFillIncrementFraction() {
        return memoryFillIncrementFraction;
    }

    public void setMemoryFillIncrementFraction(Double memoryFillIncrementFraction) {
        this.memoryFillIncrementFraction = memoryFillIncrementFraction;
    }

    public Double getMemoryFillTargetFraction() {
        return memoryFillTargetFraction;
    }

    public void setMemoryFillTargetFraction(Double memoryFillTargetFraction) {
        this.memoryFillTargetFraction = memoryFillTargetFraction;
    }

    public String getMemoryCronExpression() {
        return memoryCronExpression;
    }

    public void setMemoryCronExpression(String memoryCronExpression) {
        this.memoryCronExpression = memoryCronExpression;
    }

    public Boolean getCpuActive() {
        return cpuActive;
    }

    public void setCpuActive(Boolean cpuActive) {
        this.cpuActive = cpuActive;
    }

    public Integer getCpuMillisecondsHoldLoad() {
        return cpuMillisecondsHoldLoad;
    }

    public void setCpuMillisecondsHoldLoad(Integer cpuMillisecondsHoldLoad) {
        this.cpuMillisecondsHoldLoad = cpuMillisecondsHoldLoad;
    }

    public Double getCpuLoadTargetFraction() {
        return cpuLoadTargetFraction;
    }

    public void setCpuLoadTargetFraction(Double cpuLoadTargetFraction) {
        this.cpuLoadTargetFraction = cpuLoadTargetFraction;
    }

    public String getCpuCronExpression() {
        return cpuCronExpression;
    }

    public void setCpuCronExpression(String cpuCronExpression) {
        this.cpuCronExpression = cpuCronExpression;
    }

    public String getRuntimeAssaultCronExpression() {
        return runtimeAssaultCronExpression;
    }

    public void setRuntimeAssaultCronExpression(String runtimeAssaultCronExpression) {
        this.runtimeAssaultCronExpression = runtimeAssaultCronExpression;
    }

    public List<String> getWatchedCustomServices() {
        return watchedCustomServices;
    }

    public void setWatchedCustomServices(List<String> watchedCustomServices) {
        this.watchedCustomServices = watchedCustomServices;
    }

    @Override
    public String toString() {
        return "AssaultPropertiesUpdate{" +
                "level=" + level +
                ", deterministic=" + deterministic +
                ", latencyRangeStart=" + latencyRangeStart +
                ", latencyRangeEnd=" + latencyRangeEnd +
                ", latencyActive=" + latencyActive +
                ", exceptionsActive=" + exceptionsActive +
                ", exception=" + exception +
                ", killApplicationActive=" + killApplicationActive +
                ", killApplicationCronExpression='" + killApplicationCronExpression + '\'' +
                ", memoryActive=" + memoryActive +
                ", memoryMillisecondsHoldFilledMemory=" + memoryMillisecondsHoldFilledMemory +
                ", memoryMillisecondsWaitNextIncrease=" + memoryMillisecondsWaitNextIncrease +
                ", memoryFillIncrementFraction=" + memoryFillIncrementFraction +
                ", memoryFillTargetFraction=" + memoryFillTargetFraction +
                ", memoryCronExpression='" + memoryCronExpression + '\'' +
                ", cpuActive=" + cpuActive +
                ", cpuMillisecondsHoldLoad=" + cpuMillisecondsHoldLoad +
                ", cpuLoadTargetFraction=" + cpuLoadTargetFraction +
                ", cpuCronExpression='" + cpuCronExpression + '\'' +
                ", runtimeAssaultCronExpression='" + runtimeAssaultCronExpression + '\'' +
                ", watchedCustomServices=" + watchedCustomServices +
                '}';
    }
}

