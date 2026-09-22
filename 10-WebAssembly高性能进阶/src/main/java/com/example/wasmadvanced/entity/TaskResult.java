package com.example.wasmadvanced.entity;

/**
 * 任务结果实体
 */
public class TaskResult {
    private String taskType;
    private String moduleUsed;
    private String executionMode;
    private double duration;
    private long resultValue;
    private long workerOverhead;
    private double speedupVsJs;

    public TaskResult() {}

    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public String getModuleUsed() { return moduleUsed; }
    public void setModuleUsed(String moduleUsed) { this.moduleUsed = moduleUsed; }
    public String getExecutionMode() { return executionMode; }
    public void setExecutionMode(String executionMode) { this.executionMode = executionMode; }
    public double getDuration() { return duration; }
    public void setDuration(double duration) { this.duration = duration; }
    public long getResultValue() { return resultValue; }
    public void setResultValue(long resultValue) { this.resultValue = resultValue; }
    public long getWorkerOverhead() { return workerOverhead; }
    public void setWorkerOverhead(long workerOverhead) { this.workerOverhead = workerOverhead; }
    public double getSpeedupVsJs() { return speedupVsJs; }
    public void setSpeedupVsJs(double speedupVsJs) { this.speedupVsJs = speedupVsJs; }
}
