package com.datascope.domain.query.util;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 查询结果导出历史记录管理器
 */
public class QueryResultExportHistory {

    // 单例实例
    private static QueryResultExportHistory instance;
    // 历史记录映射 (historyId -> 历史记录)
    private final Map<String, ExportHistoryEntry> historyMap;
    // 用户历史记录映射 (userId -> 历史记录列表)
    private final Map<String, List<String>> userHistoryMap;

    /**
     * 私有构造函数
     */
    private QueryResultExportHistory() {
        this.historyMap = new ConcurrentHashMap<>();
        this.userHistoryMap = new ConcurrentHashMap<>();
    }

    /**
     * 获取单例实例
     *
     * @return 导出历史记录管理器实例
     */
    public static synchronized QueryResultExportHistory getInstance() {
        if (instance == null) {
            instance = new QueryResultExportHistory();
        }
        return instance;
    }

    /**
     * 添加导出历史记录
     *
     * @param taskId           任务ID
     * @param userId           用户ID
     * @param fileName         文件名
     * @param format           格式
     * @param filePath         文件路径
     * @param exportParameters 导出参数
     * @return 历史记录ID
     */
    public String addExportHistory(String taskId, String userId, String fileName, String format,
                                   String filePath, Map<String, String> exportParameters) {
        // 生成历史记录ID
        String historyId = UUID.randomUUID().toString();

        // 创建历史记录条目
        ExportHistoryEntry entry = new ExportHistoryEntry(
            historyId, taskId, userId, fileName, format, filePath, exportParameters);

        // 保存历史记录
        historyMap.put(historyId, entry);

        // 添加到用户历史记录
        userHistoryMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(historyId);

        return historyId;
    }

    /**
     * 获取历史记录条目
     *
     * @param historyId 历史记录ID
     * @return 历史记录条目，如果不存在则返回null
     */
    public ExportHistoryEntry getHistoryEntry(String historyId) {
        return historyMap.get(historyId);
    }

    /**
     * 获取用户的导出历史记录
     *
     * @param userId 用户ID
     * @return 导出历史记录列表
     */
    public List<ExportHistoryEntry> getUserExportHistory(String userId) {
        List<String> historyIds = userHistoryMap.getOrDefault(userId, Collections.emptyList());
        return historyIds.stream()
            .map(historyMap::get)
            .filter(entry -> entry != null)
            .collect(Collectors.toList());
    }

    /**
     * 获取用户的最近导出历史记录
     *
     * @param userId 用户ID
     * @param limit  最大记录数
     * @return 导出历史记录列表
     */
    public List<ExportHistoryEntry> getRecentExportHistory(String userId, int limit) {
        return getUserExportHistory(userId).stream()
            .sorted(Comparator.comparing(ExportHistoryEntry::getLastUsedTime).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * 获取用户的常用导出历史记录
     *
     * @param userId 用户ID
     * @param limit  最大记录数
     * @return 导出历史记录列表
     */
    public List<ExportHistoryEntry> getFrequentExportHistory(String userId, int limit) {
        return getUserExportHistory(userId).stream()
            .sorted(Comparator.comparing(ExportHistoryEntry::getUsageCount).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    /**
     * 重用导出历史记录
     *
     * @param historyId 历史记录ID
     * @return 历史记录条目，如果不存在则返回null
     */
    public ExportHistoryEntry reuseExportHistory(String historyId) {
        ExportHistoryEntry entry = historyMap.get(historyId);
        if (entry != null) {
            entry.updateUsage();
        }
        return entry;
    }

    /**
     * 删除导出历史记录
     *
     * @param historyId 历史记录ID
     * @return 是否成功删除
     */
    public boolean removeExportHistory(String historyId) {
        ExportHistoryEntry entry = historyMap.remove(historyId);
        if (entry != null) {
            List<String> userHistory = userHistoryMap.get(entry.getUserId());
            if (userHistory != null) {
                userHistory.remove(historyId);
            }
            return true;
        }
        return false;
    }

    /**
     * 清理用户的所有导出历史记录
     *
     * @param userId 用户ID
     * @return 清理的记录数
     */
    public int clearUserExportHistory(String userId) {
        List<String> historyIds = userHistoryMap.remove(userId);
        if (historyIds == null) {
            return 0;
        }

        int count = 0;
        for (String historyId : historyIds) {
            if (historyMap.remove(historyId) != null) {
                count++;
            }
        }

        return count;
    }

    /**
     * 导出历史记录条目
     */
    public static class ExportHistoryEntry {
        private final String historyId;
        private final String taskId;
        private final String userId;
        private final String fileName;
        private final String format;
        private final String filePath;
        private final LocalDateTime exportTime;
        private final Map<String, String> exportParameters;
        private LocalDateTime lastUsedTime;
        private int usageCount;

        public ExportHistoryEntry(String historyId, String taskId, String userId, String fileName, String format,
                                  String filePath, Map<String, String> exportParameters) {
            this.historyId = historyId;
            this.taskId = taskId;
            this.userId = userId;
            this.fileName = fileName;
            this.format = format;
            this.filePath = filePath;
            this.exportTime = LocalDateTime.now();
            this.lastUsedTime = this.exportTime;
            this.usageCount = 1;
            this.exportParameters = new HashMap<>(exportParameters);
        }

        public String getHistoryId() {
            return historyId;
        }

        public String getTaskId() {
            return taskId;
        }

        public String getUserId() {
            return userId;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFormat() {
            return format;
        }

        public String getFilePath() {
            return filePath;
        }

        public LocalDateTime getExportTime() {
            return exportTime;
        }

        public LocalDateTime getLastUsedTime() {
            return lastUsedTime;
        }

        public int getUsageCount() {
            return usageCount;
        }

        public Map<String, String> getExportParameters() {
            return Collections.unmodifiableMap(exportParameters);
        }

        /**
         * 更新使用记录
         */
        public void updateUsage() {
            this.lastUsedTime = LocalDateTime.now();
            this.usageCount++;
        }
    }
}
