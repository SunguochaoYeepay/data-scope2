package com.datascope.domain.query.util;

import com.datascope.domain.query.util.QueryResultExportHistory.ExportHistoryEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class QueryResultExportHistoryTest {

    private static final String TEST_USER_ID = "test_user_123";
    private QueryResultExportHistory exportHistory;

    @BeforeEach
    void setUp() {
        exportHistory = QueryResultExportHistory.getInstance();
        // 清理测试用户的历史记录
        exportHistory.clearUserExportHistory(TEST_USER_ID);
    }

    @Test
    void testAddAndGetExportHistory() {
        // 准备
        String taskId = "task_123";
        String fileName = "test_export.csv";
        String format = "CSV";
        String filePath = "/tmp/test_export.csv";
        Map<String, String> exportParams = new HashMap<>();
        exportParams.put("delimiter", ",");
        exportParams.put("charset", "UTF-8");

        // 执行
        String historyId = exportHistory.addExportHistory(taskId, TEST_USER_ID, fileName, format, filePath, exportParams);

        // 验证
        assertNotNull(historyId);

        // 获取历史记录
        ExportHistoryEntry entry = exportHistory.getHistoryEntry(historyId);

        // 验证历史记录
        assertNotNull(entry);
        assertEquals(historyId, entry.getHistoryId());
        assertEquals(taskId, entry.getTaskId());
        assertEquals(TEST_USER_ID, entry.getUserId());
        assertEquals(fileName, entry.getFileName());
        assertEquals(format, entry.getFormat());
        assertEquals(filePath, entry.getFilePath());
        assertEquals(1, entry.getUsageCount());
        assertNotNull(entry.getExportTime());
        assertNotNull(entry.getLastUsedTime());
        assertEquals(entry.getExportTime(), entry.getLastUsedTime());

        // 验证导出参数
        Map<String, String> params = entry.getExportParameters();
        assertEquals(",", params.get("delimiter"));
        assertEquals("UTF-8", params.get("charset"));
    }

    @Test
    void testGetUserExportHistory() {
        // 准备 - 添加多个历史记录
        addTestHistoryEntry("task_1", "file1.csv", "CSV", "/tmp/file1.csv");
        addTestHistoryEntry("task_2", "file2.json", "JSON", "/tmp/file2.json");
        addTestHistoryEntry("task_3", "file3.xlsx", "Excel", "/tmp/file3.xlsx");

        // 执行
        List<ExportHistoryEntry> history = exportHistory.getUserExportHistory(TEST_USER_ID);

        // 验证
        assertNotNull(history);
        assertEquals(3, history.size());

        // 验证包含所有添加的记录
        boolean hasFile1 = false, hasFile2 = false, hasFile3 = false;
        for (ExportHistoryEntry entry : history) {
            if (entry.getFileName().equals("file1.csv")) hasFile1 = true;
            if (entry.getFileName().equals("file2.json")) hasFile2 = true;
            if (entry.getFileName().equals("file3.xlsx")) hasFile3 = true;
        }

        assertTrue(hasFile1 && hasFile2 && hasFile3);
    }

    @Test
    void testGetRecentExportHistory() {
        // 准备 - 添加多个历史记录
        String histId1 = addTestHistoryEntry("task_1", "file1.csv", "CSV", "/tmp/file1.csv");
        String histId2 = addTestHistoryEntry("task_2", "file2.json", "JSON", "/tmp/file2.json");
        String histId3 = addTestHistoryEntry("task_3", "file3.xlsx", "Excel", "/tmp/file3.xlsx");

        // 模拟使用历史记录2，使其成为最近使用的
        exportHistory.reuseExportHistory(histId2);

        // 执行
        List<ExportHistoryEntry> recentHistory = exportHistory.getRecentExportHistory(TEST_USER_ID, 2);

        // 验证
        assertNotNull(recentHistory);
        assertEquals(2, recentHistory.size());

        // 验证排序 - 最近使用的应该在前面
        assertEquals("file2.json", recentHistory.get(0).getFileName());
    }

    @Test
    void testGetFrequentExportHistory() {
        // 准备 - 添加多个历史记录
        String histId1 = addTestHistoryEntry("task_1", "file1.csv", "CSV", "/tmp/file1.csv");
        String histId2 = addTestHistoryEntry("task_2", "file2.json", "JSON", "/tmp/file2.json");
        String histId3 = addTestHistoryEntry("task_3", "file3.xlsx", "Excel", "/tmp/file3.xlsx");

        // 模拟多次使用历史记录1，使其成为最常用的
        exportHistory.reuseExportHistory(histId1);
        exportHistory.reuseExportHistory(histId1);
        exportHistory.reuseExportHistory(histId1);

        // 模拟使用历史记录2
        exportHistory.reuseExportHistory(histId2);

        // 执行
        List<ExportHistoryEntry> frequentHistory = exportHistory.getFrequentExportHistory(TEST_USER_ID, 2);

        // 验证
        assertNotNull(frequentHistory);
        assertEquals(2, frequentHistory.size());

        // 验证排序 - 使用次数最多的应该在前面
        assertEquals("file1.csv", frequentHistory.get(0).getFileName());
        assertEquals(4, frequentHistory.get(0).getUsageCount());
        assertEquals("file2.json", frequentHistory.get(1).getFileName());
        assertEquals(2, frequentHistory.get(1).getUsageCount());
    }

    @Test
    void testReuseExportHistory() {
        // 准备
        String historyId = addTestHistoryEntry("task_1", "file1.csv", "CSV", "/tmp/file1.csv");

        // 执行
        ExportHistoryEntry entry = exportHistory.reuseExportHistory(historyId);

        // 验证
        assertNotNull(entry);
        assertEquals(2, entry.getUsageCount());
        assertNotEquals(entry.getExportTime(), entry.getLastUsedTime());
    }

    @Test
    void testRemoveExportHistory() {
        // 准备
        String historyId = addTestHistoryEntry("task_1", "file1.csv", "CSV", "/tmp/file1.csv");

        // 验证添加成功
        assertNotNull(exportHistory.getHistoryEntry(historyId));

        // 执行
        boolean removed = exportHistory.removeExportHistory(historyId);

        // 验证
        assertTrue(removed);
        assertNull(exportHistory.getHistoryEntry(historyId));

        // 验证用户历史记录中也被删除
        List<ExportHistoryEntry> history = exportHistory.getUserExportHistory(TEST_USER_ID);
        boolean found = false;
        for (ExportHistoryEntry entry : history) {
            if (entry.getHistoryId().equals(historyId)) {
                found = true;
                break;
            }
        }
        assertFalse(found);
    }

    @Test
    void testClearUserExportHistory() {
        // 准备 - 添加多个历史记录
        addTestHistoryEntry("task_1", "file1.csv", "CSV", "/tmp/file1.csv");
        addTestHistoryEntry("task_2", "file2.json", "JSON", "/tmp/file2.json");
        addTestHistoryEntry("task_3", "file3.xlsx", "Excel", "/tmp/file3.xlsx");

        // 验证添加成功
        assertEquals(3, exportHistory.getUserExportHistory(TEST_USER_ID).size());

        // 执行
        int count = exportHistory.clearUserExportHistory(TEST_USER_ID);

        // 验证
        assertEquals(3, count);
        assertEquals(0, exportHistory.getUserExportHistory(TEST_USER_ID).size());
    }

    /**
     * 添加测试历史记录
     */
    private String addTestHistoryEntry(String taskId, String fileName, String format, String filePath) {
        Map<String, String> exportParams = new HashMap<>();
        exportParams.put("test_param", "test_value");
        return exportHistory.addExportHistory(taskId, TEST_USER_ID, fileName, format, filePath, exportParams);
    }
}
