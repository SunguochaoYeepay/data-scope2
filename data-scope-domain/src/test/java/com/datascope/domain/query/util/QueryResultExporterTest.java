package com.datascope.domain.query.util;

import com.datascope.domain.query.model.ColumnDefinition;
import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.util.QueryResultExporter.CompressionType;
import com.datascope.domain.query.util.QueryResultExporter.ExportProgressListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class QueryResultExporterTest {

    @TempDir
    Path tempDir;
    private QueryResult queryResult;

    @BeforeEach
    void setUp() {
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

    @Test
    void exportToCsv() throws IOException {
        // 准备
        Path csvFile = tempDir.resolve("test_export.csv");
        String filePath = csvFile.toString();

        // 执行
        String result = QueryResultExporter.exportToCsv(queryResult, filePath);

        // 验证
        assertEquals(filePath, result);
        assertTrue(Files.exists(csvFile));

        // 验证文件内容
        List<String> lines = Files.readAllLines(csvFile);
        assertEquals(4, lines.size()); // 标题行 + 3行数据
        assertEquals("ID,Name,Age", lines.get(0));
        assertTrue(lines.get(1).contains("1") && lines.get(1).contains("John Doe") && lines.get(1).contains("30"));
        assertTrue(lines.get(2).contains("2") && lines.get(2).contains("Jane Smith") && lines.get(2).contains("25"));
        assertTrue(lines.get(3).contains("3") && lines.get(3).contains("Bob Johnson") && lines.get(3).contains("40"));
    }

    @Test
    void exportToCsvWithCustomDelimiter() throws IOException {
        // 准备
        Path csvFile = tempDir.resolve("test_export_tab.csv");
        String filePath = csvFile.toString();
        String delimiter = "\t"; // 使用制表符作为分隔符

        // 执行
        String result = QueryResultExporter.exportToCsv(queryResult, filePath, delimiter);

        // 验证
        assertEquals(filePath, result);
        assertTrue(Files.exists(csvFile));

        // 验证文件内容
        List<String> lines = Files.readAllLines(csvFile);
        assertEquals(4, lines.size()); // 标题行 + 3行数据
        assertEquals("ID\tName\tAge", lines.get(0));
        assertTrue(lines.get(1).contains("1") && lines.get(1).contains("John Doe") && lines.get(1).contains("30"));
        assertTrue(lines.get(2).contains("2") && lines.get(2).contains("Jane Smith") && lines.get(2).contains("25"));
        assertTrue(lines.get(3).contains("3") && lines.get(3).contains("Bob Johnson") && lines.get(3).contains("40"));
    }

    @Test
    void exportToCsvWithCustomDelimiterAndEncoding() throws IOException {
        // 准备
        Path csvFile = tempDir.resolve("test_export_utf16.csv");
        String filePath = csvFile.toString();
        String delimiter = ";"; // 使用分号作为分隔符

        // 执行
        String result = QueryResultExporter.exportToCsv(queryResult, filePath, delimiter, StandardCharsets.UTF_16);

        // 验证
        assertEquals(filePath, result);
        assertTrue(Files.exists(csvFile));

        // 验证文件内容 - 使用指定的编码读取
        try (BufferedReader reader = Files.newBufferedReader(csvFile, StandardCharsets.UTF_16)) {
            String headerLine = reader.readLine();
            assertEquals("ID;Name;Age", headerLine);

            String line1 = reader.readLine();
            assertTrue(line1.contains("1") && line1.contains("John Doe") && line1.contains("30"));

            String line2 = reader.readLine();
            assertTrue(line2.contains("2") && line2.contains("Jane Smith") && line2.contains("25"));

            String line3 = reader.readLine();
            assertTrue(line3.contains("3") && line3.contains("Bob Johnson") && line3.contains("40"));
        }
    }

    @Test
    void exportToCsvWithCompression() throws IOException {
        // 准备
        Path csvFile = tempDir.resolve("test_export.csv");
        String filePath = csvFile.toString();

        // 执行 - GZIP压缩
        String gzipResult = QueryResultExporter.exportToCsv(queryResult, filePath, ",", StandardCharsets.UTF_8,
            CompressionType.GZIP, null, null);

        // 验证
        assertEquals(filePath + ".gz", gzipResult);
        assertTrue(Files.exists(Path.of(gzipResult)));

        // 准备 - ZIP压缩
        Path zipCsvFile = tempDir.resolve("test_export_zip.csv");
        String zipFilePath = zipCsvFile.toString();

        // 执行 - ZIP压缩
        String zipResult = QueryResultExporter.exportToCsv(queryResult, zipFilePath, ",", StandardCharsets.UTF_8,
            CompressionType.ZIP, null, null);

        // 验证
        assertEquals(zipFilePath + ".zip", zipResult);
        assertTrue(Files.exists(Path.of(zipResult)));
    }

    @Test
    void exportToCsvWithProgressListener() throws IOException {
        // 准备
        Path csvFile = tempDir.resolve("test_export_progress.csv");
        String filePath = csvFile.toString();

        AtomicInteger progressUpdates = new AtomicInteger(0);
        AtomicBoolean completed = new AtomicBoolean(false);
        AtomicBoolean error = new AtomicBoolean(false);

        ExportProgressListener listener = new ExportProgressListener() {
            @Override
            public void onProgressUpdate(int progress, String message) {
                progressUpdates.incrementAndGet();
                assertTrue(progress >= 0 && progress <= 100);
                assertNotNull(message);
            }

            @Override
            public void onComplete(String path) {
                completed.set(true);
                assertEquals(filePath, path);
            }

            @Override
            public void onError(String errorMsg) {
                error.set(true);
                fail("Should not have error: " + errorMsg);
            }

            @Override
            public void onCancelled() {
                fail("Should not be cancelled");
            }
        };

        // 执行
        String result = QueryResultExporter.exportToCsv(queryResult, filePath, ",", StandardCharsets.UTF_8, listener, null);

        // 验证
        assertEquals(filePath, result);
        assertTrue(Files.exists(csvFile));
        assertTrue(progressUpdates.get() > 0, "Should have progress updates");
        assertTrue(completed.get(), "Should be completed");
        assertFalse(error.get(), "Should not have error");
    }

    @Test
    void exportToCsvWithCancellation() throws IOException {
        // 准备
        Path csvFile = tempDir.resolve("test_export_cancel.csv");
        String filePath = csvFile.toString();

        AtomicBoolean cancelled = new AtomicBoolean(false);
        AtomicBoolean cancelFlag = new AtomicBoolean(true); // 设置为true表示取消

        ExportProgressListener listener = new ExportProgressListener() {
            @Override
            public void onProgressUpdate(int progress, String message) {
                // 不需要做任何事情
            }

            @Override
            public void onComplete(String path) {
                fail("Should not complete when cancelled");
            }

            @Override
            public void onError(String errorMsg) {
                fail("Should not have error when cancelled: " + errorMsg);
            }

            @Override
            public void onCancelled() {
                cancelled.set(true);
            }
        };

        // 执行
        String result = QueryResultExporter.exportToCsv(queryResult, filePath, ",", StandardCharsets.UTF_8, listener, cancelFlag);

        // 验证
        assertNull(result, "Result should be null when cancelled");
        assertTrue(cancelled.get(), "Should be cancelled");
    }

    @Test
    void exportToJson() throws IOException {
        // 准备
        Path jsonFile = tempDir.resolve("test_export.json");
        String filePath = jsonFile.toString();

        // 执行
        String result = QueryResultExporter.exportToJson(queryResult, filePath, StandardCharsets.UTF_8, true);

        // 验证
        assertEquals(filePath, result);
        assertTrue(Files.exists(jsonFile));

        // 验证文件内容
        String content = Files.readString(jsonFile);
        assertTrue(content.contains("\"metadata\""));
        assertTrue(content.contains("\"totalRows\": 3"));
        assertTrue(content.contains("\"executionTime\": 100"));
        assertTrue(content.contains("\"data\""));
        assertTrue(content.contains("\"id\": 1"));
        assertTrue(content.contains("\"name\": \"John Doe\""));
        assertTrue(content.contains("\"age\": 30"));
    }

    @Test
    void exportToJsonWithCompression() throws IOException {
        // 准备
        Path jsonFile = tempDir.resolve("test_export.json");
        String filePath = jsonFile.toString();

        // 执行 - GZIP压缩
        String gzipResult = QueryResultExporter.exportToJson(queryResult, filePath, StandardCharsets.UTF_8, true,
            CompressionType.GZIP, null, null);

        // 验证
        assertEquals(filePath + ".gz", gzipResult);
        assertTrue(Files.exists(Path.of(gzipResult)));

        // 准备 - ZIP压缩
        Path zipJsonFile = tempDir.resolve("test_export_zip.json");
        String zipFilePath = zipJsonFile.toString();

        // 执行 - ZIP压缩
        String zipResult = QueryResultExporter.exportToJson(queryResult, zipFilePath, StandardCharsets.UTF_8, true,
            CompressionType.ZIP, null, null);

        // 验证
        assertEquals(zipFilePath + ".zip", zipResult);
        assertTrue(Files.exists(Path.of(zipResult)));
    }

    @Test
    void exportToJsonWithCustomEncoding() throws IOException {
        // 准备
        Path jsonFile = tempDir.resolve("test_export_utf16.json");
        String filePath = jsonFile.toString();

        // 执行
        String result = QueryResultExporter.exportToJson(queryResult, filePath, StandardCharsets.UTF_16, true);

        // 验证
        assertEquals(filePath, result);
        assertTrue(Files.exists(jsonFile));

        // 验证文件内容 - 使用指定的编码读取
        String content = Files.readString(jsonFile, StandardCharsets.UTF_16);
        assertTrue(content.contains("\"metadata\""));
        assertTrue(content.contains("\"totalRows\": 3"));
        assertTrue(content.contains("\"executionTime\": 100"));
        assertTrue(content.contains("\"data\""));
        assertTrue(content.contains("\"id\": 1"));
        assertTrue(content.contains("\"name\": \"John Doe\""));
        assertTrue(content.contains("\"age\": 30"));
    }

    @Test
    void exportToJsonWithoutPrettyPrint() throws IOException {
        // 准备
        Path jsonFile = tempDir.resolve("test_export_compact.json");
        String filePath = jsonFile.toString();

        // 执行
        String result = QueryResultExporter.exportToJson(queryResult, filePath, StandardCharsets.UTF_8, false);

        // 验证
        assertEquals(filePath, result);
        assertTrue(Files.exists(jsonFile));

        // 验证文件内容 - 应该没有换行符和缩进
        String content = Files.readString(jsonFile);
        assertTrue(content.contains("\"metadata\":{"));
        assertTrue(content.contains("\"totalRows\":3"));
        assertTrue(!content.contains("\n  ")); // 不应该有缩进
    }

    @Test
    void exportToJsonWithProgressListener() throws IOException {
        // 准备
        Path jsonFile = tempDir.resolve("test_export_progress.json");
        String filePath = jsonFile.toString();

        AtomicInteger progressUpdates = new AtomicInteger(0);
        AtomicBoolean completed = new AtomicBoolean(false);

        ExportProgressListener listener = new ExportProgressListener() {
            @Override
            public void onProgressUpdate(int progress, String message) {
                progressUpdates.incrementAndGet();
                assertTrue(progress >= 0 && progress <= 100);
                assertNotNull(message);
            }

            @Override
            public void onComplete(String path) {
                completed.set(true);
                assertEquals(filePath, path);
            }

            @Override
            public void onError(String errorMsg) {
                fail("Should not have error: " + errorMsg);
            }

            @Override
            public void onCancelled() {
                fail("Should not be cancelled");
            }
        };

        // 执行
        String result = QueryResultExporter.exportToJson(queryResult, filePath, StandardCharsets.UTF_8, true, listener, null);

        // 验证
        assertEquals(filePath, result);
        assertTrue(Files.exists(jsonFile));
        assertTrue(progressUpdates.get() > 0, "Should have progress updates");
        assertTrue(completed.get(), "Should be completed");
    }

    @Test
    void exportToExcel() throws IOException {
        // 准备
        Path excelFile = tempDir.resolve("test_export.xlsx");
        String filePath = excelFile.toString();

        // 执行
        String result = QueryResultExporter.exportToExcel(queryResult, filePath);

        // 验证 - 由于我们的实现实际上是导出为CSV，所以我们检查CSV文件
        Path csvFile = tempDir.resolve("test_export.csv");
        assertEquals(csvFile.toString(), result);
        assertTrue(Files.exists(csvFile));
    }

    @Test
    void exportToExcelWithProgressListener() throws IOException {
        // 准备
        Path excelFile = tempDir.resolve("test_export_progress.xlsx");
        String filePath = excelFile.toString();

        AtomicInteger progressUpdates = new AtomicInteger(0);
        AtomicBoolean completed = new AtomicBoolean(false);

        ExportProgressListener listener = new ExportProgressListener() {
            @Override
            public void onProgressUpdate(int progress, String message) {
                progressUpdates.incrementAndGet();
                assertTrue(progress >= 0 && progress <= 100);
                assertNotNull(message);
            }

            @Override
            public void onComplete(String path) {
                completed.set(true);
                assertEquals(filePath.replace(".xlsx", ".csv"), path);
            }

            @Override
            public void onError(String errorMsg) {
                fail("Should not have error: " + errorMsg);
            }

            @Override
            public void onCancelled() {
                fail("Should not be cancelled");
            }
        };

        // 执行
        String result = QueryResultExporter.exportToExcel(queryResult, filePath, listener, null);

        // 验证
        Path csvFile = tempDir.resolve("test_export_progress.csv");
        assertEquals(csvFile.toString(), result);
        assertTrue(Files.exists(csvFile));
        assertTrue(progressUpdates.get() > 0, "Should have progress updates");
        assertTrue(completed.get(), "Should be completed");
    }
}
