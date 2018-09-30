package com.mo9.raptor.bean.req;

import org.springframework.data.domain.Sort;

/**
 * @author zma
 * @date 2018/9/27
 */
public class PageReq {
    /**
     * 当前页面
     */
    private Integer page = 1;
    /**
     * 每页条数
     */
    private Integer size = 20;
    /**
     * 排序方式 ASC, DESC;
     */
    private Sort.Direction direction = Sort.Direction.ASC;
    /**
     * 排序字段
     */
    private String property = "id";
    /**
     * 开始时间
     */
    private Long startTime = 0L;
    /**
     * 结束时间
     */
    private Long endTime = System.currentTimeMillis();

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Sort.Direction getDirection() {
        return direction;
    }

    public void setDirection(Sort.Direction direction) {
        this.direction = direction;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
