package com.datafabric.common.dto;


import com.fasterxml.jackson.annotation.JsonRawValue;

public class TransformMetadataDTO {
    private Integer stepOrder;
    private String ruleType;
    @JsonRawValue
    private String ruleConfig;

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleConfig() {
        return ruleConfig;
    }

    public void setRuleConfig(String ruleConfig) {
        this.ruleConfig = ruleConfig;
    }
}
