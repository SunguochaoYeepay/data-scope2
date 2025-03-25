package com.datascope.domain.query.util;

import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.util.QueryResultExporter.CompressionType;
import com.datascope.domain.query.util.QueryResultExporter.ExportProgressListener;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 查询结果导出管理器，提供异步导出功能
 */
public class QueryResultExportManager {

    // 单例实例
    private static QueryResultExportManager instance;
    // 导出任务线程池
    private final ExecutorService executorService;
    // 导出任务信息映射
    private final Map<String, ExportTaskInfo> taskInfoMap;
    // 导出任务Future映射
    private final Map<String, Future<?>> taskFutureMap;
    // 导出历史记录管理器
    private final QueryResultExportHistory exportHistory;

    /**
     * 私有构造函数
     */
    private QueryResultExportManager() {
        // 创建固定大小的线程池，可以根据需要调整线程数
        this.executorService = Executors.newFixedThreadPool(5);
        this.taskInfoMap = new ConcurrentHashMap<>();
        this.taskFutureMap = new ConcurrentHashMap<>();
        this.exportHistory = QueryResultExportHistory.getInstance();
    }

    /**
     * 获取单例实例
     *
     * @return 导出管理器实例
     */
    public static synchronized QueryResultExportManager getInstance() {
        if (instance == null) {
            instance = new QueryResultExportManager();
        }
        return instance;
    }

    /**
     * 提交CSV导出任务
     *
     * @param result    查询结果
     * @param filePath  导出文件路径
     * @param delimiter 分隔符
     * @param charset   字符编码
     * @return 任务ID
     */
    public String submitCsvExportTask(QueryResult result, String filePath, String delimiter, Charset charset) {
        return submitCsvExportTask(result, filePath, delimiter, charset, CompressionType.NONE);
    }

    /**
     * 提交CSV导出任务，带压缩
     *
     * @param result          查询结果
     * @param filePath        导出文件路径
     * @param delimiter       分隔符
     * @param charset         字符编码
     * @param compressionType 压缩类型
     * @return 任务ID
     */
    public String submitCsvExportTask(QueryResult result, String filePath, String delimiter, Charset charset,
                                      CompressionType compressionType) {
        return submitCsvExportTask(result, filePath, delimiter, charset, compressionType, null);
    }

    /**
     * 提交CSV导出任务，带压缩和用户ID
     *
     * @param result          查询结果
     * @param filePath        导出文件路径
     * @param delimiter       分隔符
     * @param charset         字符编码
     * @param compressionType 压缩类型
     * @param userId          用户ID，用于记录历史
     * @return 任务ID
     */
    public String submitCsvExportTask(QueryResult result, String filePath, String delimiter, Charset charset,
                                      CompressionType compressionType, String userId) {
        // 生成任务ID
        String taskId = generateTaskId();

        // 获取文件名
        String fileName = getFileNameFromPath(filePath);

        // 创建任务信息
        ExportTaskInfo taskInfo = new ExportTaskInfo(taskId, fileName, "CSV");
        taskInfoMap.put(taskId, taskInfo);

        // 创建进度监听器
        ExportProgressListener listener = createProgressListener(taskInfo);

        // 提交任务
        Future<?> future = executorService.submit(() -> {
            try {
                // 更新任务状态
                taskInfo.setStatus(ExportTaskStatus.RUNNING);
                taskInfo.setMessage("正在导出CSV文件...");

                // 执行导出
                String exportedFilePath = QueryResultExporter.exportToCsv(
                    result, filePath, delimiter, charset, compressionType, listener, taskInfo.getCancelFlag());

                // 检查是否取消
                if (taskInfo.isCancelled()) {
                    taskInfo.setStatus(ExportTaskStatus.CANCELLED);
                    taskInfo.setMessage("导出已取消");
                    return;
                }

                // 更新任务状态
                taskInfo.setStatus(ExportTaskStatus.COMPLETED);
                taskInfo.setFilePath(exportedFilePath);
                taskInfo.setMessage("导出完成");
                taskInfo.setProgress(100);

                // 如果提供了用户ID，记录导出历史
                if (userId != null && !userId.isEmpty()) {
                    // 创建导出参数映射
                    Map<String, String> exportParams = new HashMap<>();
                    exportParams.put("delimiter", delimiter);
                    exportParams.put("charset", charset.name());
                    exportParams.put("compressionType", compressionType.name());

                    // 添加到导出历史
                    exportHistory.addExportHistory(taskId, userId, fileName, "CSV",
                        exportedFilePath, exportParams);
                }
            } catch (IOException e) {
                // 更新任务状态为失败
                taskInfo.setStatus(ExportTaskStatus.FAILED);
                taskInfo.setErrorMessage(e.getMessage());
                taskInfo.setMessage("导出失败: " + e.getMessage());
            }
        });

        // 保存任务Future
        taskFutureMap.put(taskId, future);

        return taskId;
    }

    /**
     * 提交JSON导出任务
     *
     * @param result      查询结果
     * @param filePath    导出文件路径
     * @param charset     字符编码
     * @param prettyPrint 是否格式化输出
     * @return 任务ID
     */
    public String submitJsonExportTask(QueryResult result, String filePath, Charset charset, boolean prettyPrint) {
        return submitJsonExportTask(result, filePath, charset, prettyPrint, CompressionType.NONE);
    }

    /**
     * 提交JSON导出任务，带压缩
     *
     * @param result          查询结果
     * @param filePath        导出文件路径
     * @param charset         字符编码
     * @param prettyPrint     是否格式化输出
     * @param compressionType 压缩类型
     * @return 任务ID
     */
    public String submitJsonExportTask(QueryResult result, String filePath, Charset charset, boolean prettyPrint,
                                       CompressionType compressionType) {
        return submitJsonExportTask(result, filePath, charset, prettyPrint, compressionType, null);
    }

    /**
     * 提交JSON导出任务，带压缩和用户ID
     *
     * @param result          查询结果
     * @param filePath        导出文件路径
     * @param charset         字符编码
     * @param prettyPrint     是否格式化输出
     * @param compressionType 压缩类型
     * @param userId          用户ID，用于记录历史
     * @return 任务ID
     */
    public String submitJsonExportTask(QueryResult result, String filePath, Charset charset, boolean prettyPrint,
                                       CompressionType compressionType, String userId) {
        // 生成任务ID
        String taskId = generateTaskId();

        // 获取文件名
        String fileName = getFileNameFromPath(filePath);

        // 创建任务信息
        ExportTaskInfo taskInfo = new ExportTaskInfo(taskId, fileName, "JSON");
        taskInfoMap.put(taskId, taskInfo);

        // 创建进度监听器
        ExportProgressListener listener = createProgressListener(taskInfo);

        // 提交任务
        Future<?> future = executorService.submit(() -> {
            try {
                // 更新任务状态
                taskInfo.setStatus(ExportTaskStatus.RUNNING);
                taskInfo.setMessage("正在导出JSON文件...");

                // 执行导出
                String exportedFilePath = QueryResultExporter.exportToJson(
                    result, filePath, charset, prettyPrint, compressionType, listener, taskInfo.getCancelFlag());

                // 检查是否取消
                if (taskInfo.isCancelled()) {
                    taskInfo.setStatus(ExportTaskStatus.CANCELLED);
                    taskInfo.setMessage("导出已取消");
                    return;
                }

                // 更新任务状态
                taskInfo.setStatus(ExportTaskStatus.COMPLETED);
                taskInfo.setFilePath(exportedFilePath);
                taskInfo.setMessage("导出完成");
                taskInfo.setProgress(100);

                // 如果提供了用户ID，记录导出历史
                if (userId != null && !userId.isEmpty()) {
                    // 创建导出参数映射
                    Map<String, String> exportParams = new HashMap<>();
                    exportParams.put("charset", charset.name());
                    exportParams.put("prettyPrint", String.valueOf(prettyPrint));
                    exportParams.put("compressionType", compressionType.name());

                    // 添加到导出历史
                    exportHistory.addExportHistory(taskId, userId, fileName, "JSON",
                        exportedFilePath, exportParams);
                }
            } catch (IOException e) {
                // 更新任务状态为失败
                taskInfo.setStatus(ExportTaskStatus.FAILED);
                taskInfo.setErrorMessage(e.getMessage());
                taskInfo.setMessage("导出失败: " + e.getMessage());
            }
        });

        // 保存任务Future
        taskFutureMap.put(taskId, future);

        return taskId;
    }

    /**
     * 提交Excel导出任务
     *
     * @param result   查询结果
     * @param filePath 导出文件路径
     * @return 任务ID
     */
    public String submitExcelExportTask(QueryResult result, String filePath) {
        return submitExcelExportTask(result, filePath, null);
    }

    /**
     * 提交Excel导出任务，带用户ID
     *
     * @param result   查询结果
     * @param filePath 导出文件路径
     * @param userId   用户ID，用于记录历史
     * @return 任务ID
     */
    public String submitExcelExportTask(QueryResult result, String filePath, String userId) {
        // 生成任务ID
        String taskId = generateTaskId();

        // 获取文件名
        String fileName = getFileNameFromPath(filePath);

        // 创建任务信息
        ExportTaskInfo taskInfo = new ExportTaskInfo(taskId, fileName, "Excel");
        taskInfoMap.put(taskId, taskInfo);

        // 创建进度监听器
        ExportProgressListener listener = createProgressListener(taskInfo);

        // 提交任务
        Future<?> future = executorService.submit(() -> {
            try {
                // 更新任务状态
                taskInfo.setStatus(ExportTaskStatus.RUNNING);
                taskInfo.setMessage("正在导出Excel文件...");

                // 执行导出
                String exportedFilePath = QueryResultExporter.exportToExcel(
                    result, filePath, listener, taskInfo.getCancelFlag());

                // 检查是否取消
                if (taskInfo.isCancelled()) {
                    taskInfo.setStatus(ExportTaskStatus.CANCELLED);
                    taskInfo.setMessage("导出已取消");
                    return;
                }

                // 更新任务状态
                taskInfo.setStatus(ExportTaskStatus.COMPLETED);
                taskInfo.setFilePath(exportedFilePath);
                taskInfo.setMessage("导出完成");
                taskInfo.setProgress(100);

                // 如果提供了用户ID，记录导出历史
                if (userId != null && !userId.isEmpty()) {
                    // 创建导出参数映射
                    Map<String, String> exportParams = new HashMap<>();

                    // 添加到导出历史
                    exportHistory.addExportHistory(taskId, userId, fileName, "Excel",
                        exportedFilePath, exportParams);
                }
            } catch (IOException e) {
                // 更新任务状态为失败
                taskInfo.setStatus(ExportTaskStatus.FAILED);
                taskInfo.setErrorMessage(e.getMessage());
                taskInfo.setMessage("导出失败: " + e.getMessage());
            }
        });

        // 保存任务Future
        taskFutureMap.put(taskId, future);

        return taskId;
    }

    /**
     * 获取任务信息
     *
     * @param taskId 任务ID
     * @return 任务信息，如果不存在则返回null
     */
    public ExportTaskInfo getTaskInfo(String taskId) {
        return taskInfoMap.get(taskId);
    }

    /**
     * 取消任务
     *
     * @param taskId 任务ID
     * @return 是否成功取消
     */
    public boolean cancelTask(String taskId) {
        ExportTaskInfo taskInfo = taskInfoMap.get(taskId);
        if (taskInfo == null) {
            return false;
        }

        // 设置取消标志
        taskInfo.cancel();

        // 尝试取消Future
        Future<?> future = taskFutureMap.get(taskId);
        if (future != null) {
            return future.cancel(true);
        }

        return true;
    }

    /**
     * 清理已完成的任务
     *
     * @param taskId 任务ID
     */
    public void cleanupTask(String taskId) {
        taskInfoMap.remove(taskId);
        taskFutureMap.remove(taskId);
    }

    /**
     * 关闭导出管理器
     */
    public void shutdown() {
        executorService.shutdown();
    }

    /**
     * 获取用户的导出历史记录
     *
     * @param userId 用户ID
     * @return 导出历史记录列表
     */
    public List<QueryResultExportHistory.ExportHistoryEntry> getUserExportHistory(String userId) {
        return exportHistory.getUserExportHistory(userId);
    }

    /**
     * 获取用户的最近导出历史记录
     *
     * @param userId 用户ID
     * @param limit  最大记录数
     * @return 导出历史记录列表
     */
    public List<QueryResultExportHistory.ExportHistoryEntry> getRecentExportHistory(String userId, int limit) {
        return exportHistory.getRecentExportHistory(userId, limit);
    }

    /**
     * 获取用户的常用导出历史记录
     *
     * @param userId 用户ID
     * @param limit  最大记录数
     * @return 导出历史记录列表
     */
    public List<QueryResultExportHistory.ExportHistoryEntry> getFrequentExportHistory(String userId, int limit) {
        return exportHistory.getFrequentExportHistory(userId, limit);
    }

    /**
     * 重用导出历史记录
     *
     * @param historyId 历史记录ID
     * @return 任务ID，如果历史记录不存在则返回null
     */
    public String reuseExportHistory(String historyId) {
        QueryResultExportHistory.ExportHistoryEntry entry = exportHistory.reuseExportHistory(historyId);
        if (entry == null) {
            return null;
        }

        // 创建任务信息
        String taskId = generateTaskId();
        ExportTaskInfo taskInfo = new ExportTaskInfo(taskId, entry.getFileName(), entry.getFormat());
        taskInfo.setStatus(ExportTaskStatus.COMPLETED);
        taskInfo.setFilePath(entry.getFilePath());
        taskInfo.setMessage("从历史记录重用");
        taskInfo.setProgress(100);

        // 保存任务信息
        taskInfoMap.put(taskId, taskInfo);

        return taskId;
    }

    /**
     * 删除导出历史记录
     *
     * @param historyId 历史记录ID
     * @return 是否成功删除
     */
    public boolean removeExportHistory(String historyId) {
        return exportHistory.removeExportHistory(historyId);
    }

    /**
     * 清理用户的所有导出历史记录
     *
     * @param userId 用户ID
     * @return 清理的记录数
     */
    public int clearUserExportHistory(String userId) {
        return exportHistory.clearUserExportHistory(userId);
    }

    /**
     * 生成任务ID
     *
     * @return 任务ID
     */
    private String generateTaskId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 从文件路径中获取文件名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    private String getFileNameFromPath(String filePath) {
        int lastSeparatorIndex = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        if (lastSeparatorIndex >= 0) {
            return filePath.substring(lastSeparatorIndex + 1);
        }
        return filePath;
    }

    /**
     * 创建进度监听器
     *
     * @param taskInfo 任务信息
     * @return 进度监听器
     */
    private ExportProgressListener createProgressListener(ExportTaskInfo taskInfo) {
        return new ExportProgressListener() {
            @Override
            public void onProgressUpdate(int progress, String message) {
                taskInfo.setProgress(progress);
                taskInfo.setMessage(message);
            }

            @Override
            public void onComplete(String filePath) {
                taskInfo.setStatus(ExportTaskStatus.COMPLETED);
                taskInfo.setFilePath(filePath);
                taskInfo.setProgress(100);
                taskInfo.setMessage("导出完成");
            }

            @Override
            public void onError(String error) {
                taskInfo.setStatus(ExportTaskStatus.FAILED);
                taskInfo.setErrorMessage(error);
                taskInfo.setMessage("导出失败: " + error);
            }

            @Override
            public void onCancelled() {
                taskInfo.setStatus(ExportTaskStatus.CANCELLED);
                taskInfo.setMessage("导出已取消");
            }
        };
    }

    /**
     * 导出任务状态
     */
    public enum ExportTaskStatus {
        PENDING,    // 等待中
        RUNNING,    // 运行中
        COMPLETED,  // 已完成
        FAILED,     // 失败
        CANCELLED   // 已取消
    }

    /**
     * 导出任务信息
     */
    public static class ExportTaskInfo {
        private final String taskId;
        private final String fileName;
        private final String format;
        private final AtomicBoolean cancelFlag;
        private ExportTaskStatus status;
        private int progress;
        private String message;
        private String filePath;
        private String errorMessage;

        public ExportTaskInfo(String taskId, String fileName, String format) {
            this.taskId = taskId;
            this.fileName = fileName;
            this.format = format;
            this.status = ExportTaskStatus.PENDING;
            this.progress = 0;
            this.message = "等待导出...";
            this.cancelFlag = new AtomicBoolean(false);
        }

        public String getTaskId() {
            return taskId;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFormat() {
            return format;
        }

        public ExportTaskStatus getStatus() {
            return status;
        }

        public void setStatus(ExportTaskStatus status) {
            this.status = status;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public AtomicBoolean getCancelFlag() {
            return cancelFlag;
        }

        public void cancel() {
            this.cancelFlag.set(true);
        }

        public boolean isCancelled() {
            return this.cancelFlag.get();
        }
    }
}
