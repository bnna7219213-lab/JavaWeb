package com.example.wasmdemo.entity;

/**
 * 计算任务实体 - 用于前端发起的计算请求
 */
public class CalculationTask {
    private String type;
    private int input;
    private boolean useWasm;

    public CalculationTask() {}

    public CalculationTask(String type, int input, boolean useWasm) {
        this.type = type;
        this.input = input;
        this.useWasm = useWasm;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public int getInput() { return input; }
    public void setInput(int input) { this.input = input; }
    public boolean isUseWasm() { return useWasm; }
    public void setUseWasm(boolean useWasm) { this.useWasm = useWasm; }
}
