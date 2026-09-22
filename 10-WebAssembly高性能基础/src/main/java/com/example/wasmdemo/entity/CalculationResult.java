package com.example.wasmdemo.entity;

/**
 * 计算结果实体
 */
public class CalculationResult {
    private String type;
    private int input;
    private long result;
    private long duration;
    private String mode;
    private boolean wasmSupported;

    public CalculationResult() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getInput() { return input; }
    public void setInput(int input) { this.input = input; }
    public long getResult() { return result; }
    public void setResult(long result) { this.result = result; }
    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public boolean isWasmSupported() { return wasmSupported; }
    public void setWasmSupported(boolean wasmSupported) { this.wasmSupported = wasmSupported; }
}
