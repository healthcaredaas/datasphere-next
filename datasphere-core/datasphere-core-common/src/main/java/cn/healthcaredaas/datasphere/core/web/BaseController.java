package cn.healthcaredaas.datasphere.core.web;

import cn.healthcaredaas.datasphere.core.common.RestResult;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * Controller基类
 * 提供统一响应方法的封装
 *
 * @author chenpan
 */
public class BaseController {

    /**
     * 返回成功响应
     */
    protected <T> RestResult<T> success() {
        return RestResult.success();
    }

    /**
     * 返回成功响应（带数据）
     */
    protected <T> RestResult<T> success(T data) {
        return RestResult.success(data);
    }

    /**
     * 返回成功响应（带消息和数据）
     */
    protected <T> RestResult<T> success(String message, T data) {
        return RestResult.success(message, data);
    }

    /**
     * 返回分页数据
     */
    protected <T> RestResult<PageResult<T>> success(IPage<T> page) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setCurrent(page.getCurrent());
        pageResult.setSize(page.getSize());
        pageResult.setTotal(page.getTotal());
        pageResult.setPages(page.getPages());
        pageResult.setRecords(page.getRecords());
        return RestResult.success(pageResult);
    }

    /**
     * 返回失败响应
     */
    protected <T> RestResult<T> error() {
        return RestResult.error();
    }

    /**
     * 返回失败响应（带消息）
     */
    protected <T> RestResult<T> error(String message) {
        return RestResult.error(message);
    }

    /**
     * 返回失败响应（带错误码和消息）
     */
    protected <T> RestResult<T> error(Integer code, String message) {
        return RestResult.error(code, message);
    }

    /**
     * 根据布尔值返回成功或失败
     */
    protected <T> RestResult<T> status(boolean success) {
        return RestResult.status(success);
    }

    /**
     * 分页结果封装
     */
    public static class PageResult<T> {
        private Long current;
        private Long size;
        private Long total;
        private Long pages;
        private List<T> records;

        public Long getCurrent() {
            return current;
        }

        public void setCurrent(Long current) {
            this.current = current;
        }

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        public Long getTotal() {
            return total;
        }

        public void setTotal(Long total) {
            this.total = total;
        }

        public Long getPages() {
            return pages;
        }

        public void setPages(Long pages) {
            this.pages = pages;
        }

        public List<T> getRecords() {
            return records;
        }

        public void setRecords(List<T> records) {
            this.records = records;
        }
    }
}
