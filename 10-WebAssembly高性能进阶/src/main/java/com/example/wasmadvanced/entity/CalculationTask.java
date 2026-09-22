package com.example.wasmadvanced.entity;

/**
 * 计算任务实体 - 进阶版
 * 支持多种计算类型和模块组合
 */
public class CalculationTask {
    private String type;
    private String moduleTarget;
    private int input;
    private boolean useWasm;
    private boolean useWorker;
    private int iterations;

    public CalculationTask() {
        this.useWasm = true;
        this.useWorker = false;
        this.iterations = 1;
        this.moduleTarget = "compute";
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getModuleTarget() { return moduleTarget; }
    public void setModuleTarget(String moduleTarget) { this.moduleTarget = moduleTarget; }
    public int getInput() { return input; }
    public void setInput(int input) { this.input = input; }
    public boolean isUseWasm() { return useWasm; }
    public void setUseWasm(boolean useWasm) { this.useWasm = useWasm; }
    public boolean isUseWorker() { return useWorker; }
    public void setUseWorker(boolean useWorker) { this.useWorker = useWorker; }
    public int getIterations() { return iterations; }
    public void setIterations(int iterations) { this.iterations = iterations; }
}
