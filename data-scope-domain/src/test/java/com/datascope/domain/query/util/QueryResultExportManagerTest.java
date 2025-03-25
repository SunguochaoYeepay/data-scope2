package com.datascope.domain.query.util;

import com.datascope.domain.query.model.ColumnDefinition;
import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.util.QueryResultExportManager.ExportTaskInfo;
import com.datascope.domain.query.util.QueryResultExportManager.ExportTaskStatus;
import com.datascope.domain.query.util.QueryResultExporter.CompressionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class QueryResultExportManagerTest {

    @TempDir
    Path tempDir;
    private QueryResultExportManager exportManager;
    private QueryResult queryResult;

    @BeforeEach
    void setUp() {
        // 获取导出管理器实例
        exportManager = QueryResultExportManager.getInstance();

        // 创建测试数据
        List<ColumnDefinition> columns = new ArrayList<>();

        ColumnDefinition idColumn = new ColumnDefinition();
        idColumn.setName("id");
        idColumn.setLabel("ID");
        idColumn.setDataType("INTEGER");
        columns.add(idColumn);

        ColumnDefinition nameColumn = new ColumnDefinition();
        nameColumn.setName("name");
        nameColumn.setLabel("Name");
        nameColumn.setDataType("VARCHAR");
        columns.add(nameColumn);

        ColumnDefinition ageColumn = new ColumnDefinition();
        ageColumn.setName("age");
        ageColumn.setLabel("Age");
        ageColumn.setDataType("INTEGER");
        columns.add(ageColumn);

        // 创建行数据
        List<Map<String, Object>> rows = new ArrayList<>();

        Map<String, Object> row1 = new HashMap<>();
        row1.put("id", 1);
        row1.put("name", "John Doe");
        row1.put("age", 30);
        rows.add(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("id", 2);
        row2.put("name", "Jane Smith");
        row2.put("age", 25);
        rows.add(row2);

        Map<String, Object> row3 = new HashMap<>();
        row3.put("id", 3);
        row3.put("name", "Bob Johnson");
        row3.put("age", 40);
        rows.add(row3);

        // 创建查询结果
        queryResult = new QueryResult();
        queryResult.setColumns(columns);
        queryResult.setRows(rows);
        queryResult.setTotalRows(rows.size());
        queryResult.setExecutionTime(100);
    }

    @AfterEach
    void tearDown() {
        // 不要在测试中关闭单例实例，因为其他测试可能会使用它
        // exportManager.shutdown();
    }

    @Test
    void testSubmitCsvExportTask() throws InterruptedException, IOException {
        // 准备
        Path csvFile = tempDir.resolve("test_export_async.csv");
        String filePath = csvFile.toString();

        // 执行
        String taskId = exportManager.submitCsvExportTask(queryResult, filePath, ",", StandardCharsets.UTF_8);

        // 验证任务ID不为空
        assertNotNull(taskId);

        // 获取任务信息
        ExportTaskInfo taskInfo = exportManager.getTaskInfo(taskId);

        // 验证任务信息
        assertNotNull(taskInfo);
        assertEquals(taskId, taskInfo.getTaskId());
        assertEquals("test_export_async.csv", taskInfo.getFileName());
        assertEquals("CSV", taskInfo.getFormat());

        // 等待任务完成（最多等待5秒）
        int maxWaitTimeSeconds = 5;
        for (int i = 0; i < maxWaitTimeSeconds; i++) {
            if (taskInfo.getStatus() == ExportTaskStatus.COMPLETED) {
                break;
            }
            TimeUnit.SECONDS.sleep(1);
        }

        // 验证任务状态
        assertEquals(ExportTaskStatus.COMPLETED, taskInfo.getStatus());
        assertEquals(100, taskInfo.getProgress());
        assertNotNull(taskInfo.getFilePath());

        // 验证文件是否存在
        assertTrue(Files.exists(Path.of(taskInfo.getFilePath())));

        // 验证文件内容
        List<String> lines = Files.readAllLines(Path.of(taskInfo.getFilePath()));
        assertEquals(4, lines.size()); // 标题行 + 3行数据
        assertEquals("ID,Name,Age", lines.get(0));

        // 清理任务
        exportManager.cleanupTask(taskId);
        assertNull(exportManager.getTaskInfo(taskId));
    }

    @Test
    void testSubmitJsonExportTask() throws InterruptedException, IOException {
        // 准备
        Path jsonFile = tempDir.resolve("test_export_async.json");
        String filePath = jsonFile.toString();

        // 执行
        String taskId = exportManager.submitJsonExportTask(queryResult, filePath, StandardCharsets.UTF_8, true);

        // 验证任务ID不为空
        assertNotNull(taskId);

        // 获取任务信息
        ExportTaskInfo taskInfo = exportManager.getTaskInfo(taskId);

        // 验证任务信息
        assertNotNull(taskInfo);
        assertEquals(taskId, taskInfo.getTaskId());
        assertEquals("test_export_async.json", taskInfo.getFileName());
        assertEquals("JSON", taskInfo.getFormat());

        // 等待任务完成（最多等待5秒）
        int maxWaitTimeSeconds = 5;
        for (int i = 0; i < maxWaitTimeSeconds; i++) {
            if (taskInfo.getStatus() == ExportTaskStatus.COMPLETED) {
                break;
            }
            TimeUnit.SECONDS.sleep(1);
        }

        // 验证任务状态
        assertEquals(ExportTaskStatus.COMPLETED, taskInfo.getStatus());
        assertEquals(100, taskInfo.getProgress());
        assertNotNull(taskInfo.getFilePath());

        // 验证文件是否存在
        assertTrue(Files.exists(Path.of(taskInfo.getFilePath())));

        // 验证文件内容
        String content = Files.readString(Path.of(taskInfo.getFilePath()));
        assertTrue(content.contains("\"metadata\""));
        assertTrue(content.contains("\"totalRows\": 3"));

        // 清理任务
        exportManager.cleanupTask(taskId);
        assertNull(exportManager.getTaskInfo(taskId));
    }

    @Test
    void testSubmitCsvExportTaskWithCompression() throws InterruptedException, IOException {
        // 准备
        Path csvFile = tempDir.resolve("test_export_async_compressed.csv");
        String filePath = csvFile.toString();

        // 执行
        String taskId = exportManager.submitCsvExportTask(queryResult, filePath, ",", StandardCharsets.UTF_8, CompressionType.GZIP);

        // 验证任务ID不为空
        assertNotNull(taskId);

        // 获取任务信息
        ExportTaskInfo taskInfo = exportManager.getTaskInfo(taskId);

        // 验证任务信息
        assertNotNull(taskInfo);

        // 等待任务完成（最多等待5秒）
        int maxWaitTimeSeconds = 5;
        for (int i = 0; i < maxWaitTimeSeconds; i++) {
            if (taskInfo.getStatus() == ExportTaskStatus.COMPLETED) {
                break;
            }
            TimeUnit.SECONDS.sleep(1);
        }

        // 验证任务状态
        assertEquals(ExportTaskStatus.COMPLETED, taskInfo.getStatus());
        assertEquals(100, taskInfo.getProgress());
        assertNotNull(taskInfo.getFilePath());

        // 验证文件是否存在
        assertTrue(Files.exists(Path.of(taskInfo.getFilePath())));
        assertTrue(taskInfo.getFilePath().endsWith(".gz"));

        // 清理任务
        exportManager.cleanupTask(taskId);
        assertNull(exportManager.getTaskInfo(taskId));
    }

    @Test
    void testCancelTask() throws InterruptedException {
        // 准备 - 创建一个大数据集以确保任务不会立即完成
        List<Map<String, Object>> largeDataset = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", i);
            row.put("name", "Name " + i);
            row.put("age", 20 + (i % 50));
            largeDataset.add(row);
        }
        queryResult.setRows(largeDataset);
        queryResult.setTotalRows(largeDataset.size());

        Path csvFile = tempDir.resolve("test_export_cancel.csv");
        String filePath = csvFile.toString();

        // 执行
        String taskId = exportManager.submitCsvExportTask(queryResult, filePath, ",", StandardCharsets.UTF_8);

        // 验证任务ID不为空
        assertNotNull(taskId);

        // 获取任务信息
        ExportTaskInfo taskInfo = exportManager.getTaskInfo(taskId);

        // 验证任务信息
        assertNotNull(taskInfo);

        // 等待任务开始运行
        TimeUnit.MILLISECONDS.sleep(500);

        // 取消任务
        boolean cancelled = exportManager.cancelTask(taskId);
        assertTrue(cancelled);

        // 等待任务状态更新
        TimeUnit.SECONDS.sleep(1);

        // 验证任务状态
        assertEquals(ExportTaskStatus.CANCELLED, taskInfo.getStatus());
        assertTrue(taskInfo.isCancelled());

        // 清理任务
        exportManager.cleanupTask(taskId);
        assertNull(exportManager.getTaskInfo(taskId));
    }
}
