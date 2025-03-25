package com.datascope.domain.query.util;

import com.datascope.domain.query.model.ColumnDefinition;
import com.datascope.domain.query.model.QueryResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 查询结果导出工具类
 */
public class QueryResultExporter {

    private static final Logger log = LoggerFactory.getLogger(QueryResultExporter.class);

    /**
     * 导出查询结果到CSV文件
     *
     * @param result   查询结果
     * @param filePath 文件路径
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToCsv(QueryResult result, String filePath) throws IOException {
        return exportToCsv(result, filePath, ",", StandardCharsets.UTF_8, CompressionType.NONE, null);
    }

    /**
     * 导出查询结果到CSV文件
     *
     * @param result    查询结果
     * @param filePath  文件路径
     * @param delimiter 分隔符
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToCsv(QueryResult result, String filePath, String delimiter) throws IOException {
        return exportToCsv(result, filePath, delimiter, StandardCharsets.UTF_8, CompressionType.NONE, null);
    }

    /**
     * 导出查询结果到CSV文件
     *
     * @param result    查询结果
     * @param filePath  文件路径
     * @param delimiter 分隔符
     * @param charset   字符集
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToCsv(QueryResult result, String filePath, String delimiter, Charset charset) throws IOException {
        return exportToCsv(result, filePath, delimiter, charset, CompressionType.NONE, null);
    }

    /**
     * 导出查询结果到CSV文件
     *
     * @param result     查询结果
     * @param filePath   文件路径
     * @param delimiter  分隔符
     * @param charset    字符集
     * @param listener   进度监听器
     * @param cancelFlag 取消标志
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToCsv(QueryResult result, String filePath, String delimiter, Charset charset,
                                     ExportProgressListener listener, AtomicBoolean cancelFlag) throws IOException {
        return exportToCsv(result, filePath, delimiter, charset, CompressionType.NONE, listener, cancelFlag);
    }

    /**
     * 导出查询结果到CSV文件
     *
     * @param result          查询结果
     * @param filePath        文件路径
     * @param delimiter       分隔符
     * @param charset         字符集
     * @param compressionType 压缩类型
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToCsv(QueryResult result, String filePath, String delimiter, Charset charset,
                                     CompressionType compressionType) throws IOException {
        return exportToCsv(result, filePath, delimiter, charset, compressionType, null);
    }

    /**
     * 导出查询结果到CSV文件
     *
     * @param result          查询结果
     * @param filePath        文件路径
     * @param delimiter       分隔符
     * @param charset         字符集
     * @param compressionType 压缩类型
     * @param listener        进度监听器
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToCsv(QueryResult result, String filePath, String delimiter, Charset charset,
                                     CompressionType compressionType, ExportProgressListener listener) throws IOException {
        return exportToCsv(result, filePath, delimiter, charset, compressionType, listener, null);
    }

    /**
     * 导出查询结果到CSV文件
     *
     * @param result          查询结果
     * @param filePath        文件路径
     * @param delimiter       分隔符
     * @param charset         字符集
     * @param compressionType 压缩类型
     * @param listener        进度监听器
     * @param cancelFlag      取消标志
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToCsv(QueryResult result, String filePath, String delimiter, Charset charset,
                                     CompressionType compressionType, ExportProgressListener listener,
                                     AtomicBoolean cancelFlag) throws IOException {
        if (result == null) {
            throw new IllegalArgumentException("Query result cannot be null");
        }
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        // 确保目录存在
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 根据压缩类型调整文件路径
        String finalFilePath = filePath;
        if (compressionType == CompressionType.GZIP && !filePath.endsWith(".gz")) {
            finalFilePath = filePath + ".gz";
        } else if (compressionType == CompressionType.ZIP && !filePath.endsWith(".zip")) {
            finalFilePath = filePath + ".zip";
        }

        // 获取列定义和数据行
        List<ColumnDefinition> columns = result.getColumns();
        List<Map<String, Object>> rows = result.getRows();
        int totalRows = rows.size();

        // 创建CSV格式
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
            .setDelimiter(delimiter)
            .setRecordSeparator("\n")
            .build();

        // 准备输出流
        OutputStream outputStream = null;
        try {
            // 根据压缩类型创建不同的输出流
            switch (compressionType) {
                case GZIP:
                    outputStream = new GZIPOutputStream(new FileOutputStream(finalFilePath));
                    break;
                case ZIP:
                    ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(finalFilePath));
                    String entryName = new File(filePath).getName();
                    zipOut.putNextEntry(new ZipEntry(entryName));
                    outputStream = zipOut;
                    break;
                case NONE:
                default:
                    outputStream = new FileOutputStream(finalFilePath);
                    break;
            }

            // 创建CSV打印器
            try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, charset);
                 CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat)) {

                // 写入表头
                for (ColumnDefinition column : columns) {
                    csvPrinter.print(column.getName());
                }
                csvPrinter.println();

                // 写入数据行
                int rowCount = 0;
                for (Map<String, Object> row : rows) {
                    // 检查是否取消
                    if (cancelFlag != null && cancelFlag.get()) {
                        if (listener != null) {
                            listener.onCancelled();
                        }
                        return null;
                    }

                    for (ColumnDefinition column : columns) {
                        Object value = row.get(column.getField());
                        csvPrinter.print(value);
                    }
                    csvPrinter.println();

                    // 更新进度
                    rowCount++;
                    if (listener != null && rowCount % 1000 == 0) {
                        int progress = (int) ((double) rowCount / totalRows * 100);
                        listener.onProgressUpdate(progress, "Exported " + rowCount + " of " + totalRows + " rows");
                    }
                }

                // 完成导出
                if (listener != null) {
                    listener.onProgressUpdate(100, "Export completed: " + totalRows + " rows");
                    listener.onComplete(finalFilePath);
                }
            }

            // 如果是ZIP格式，需要关闭ZIP条目
            if (compressionType == CompressionType.ZIP) {
                ((ZipOutputStream) outputStream).closeEntry();
            }

            log.info("CSV export completed: {}", finalFilePath);
            return finalFilePath;
        } catch (IOException e) {
            if (listener != null) {
                listener.onError("Export failed: " + e.getMessage());
            }
            log.error("Failed to export CSV: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("Failed to close output stream: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 导出查询结果到JSON文件
     *
     * @param result   查询结果
     * @param filePath 文件路径
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToJson(QueryResult result, String filePath) throws IOException {
        return exportToJson(result, filePath, StandardCharsets.UTF_8, true, CompressionType.NONE, null);
    }

    /**
     * 导出查询结果到JSON文件
     *
     * @param result      查询结果
     * @param filePath    文件路径
     * @param charset     字符集
     * @param prettyPrint 是否格式化输出
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToJson(QueryResult result, String filePath, Charset charset, boolean prettyPrint) throws IOException {
        return exportToJson(result, filePath, charset, prettyPrint, CompressionType.NONE, null);
    }

    /**
     * 导出查询结果到JSON文件
     *
     * @param result      查询结果
     * @param filePath    文件路径
     * @param charset     字符集
     * @param prettyPrint 是否格式化输出
     * @param listener    进度监听器
     * @param cancelFlag  取消标志
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToJson(QueryResult result, String filePath, Charset charset, boolean prettyPrint,
                                      ExportProgressListener listener, AtomicBoolean cancelFlag) throws IOException {
        return exportToJson(result, filePath, charset, prettyPrint, CompressionType.NONE, listener, cancelFlag);
    }

    /**
     * 导出查询结果到JSON文件
     *
     * @param result          查询结果
     * @param filePath        文件路径
     * @param charset         字符集
     * @param prettyPrint     是否格式化输出
     * @param compressionType 压缩类型
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToJson(QueryResult result, String filePath, Charset charset, boolean prettyPrint,
                                      CompressionType compressionType) throws IOException {
        return exportToJson(result, filePath, charset, prettyPrint, compressionType, null);
    }

    /**
     * 导出查询结果到JSON文件
     *
     * @param result          查询结果
     * @param filePath        文件路径
     * @param charset         字符集
     * @param prettyPrint     是否格式化输出
     * @param compressionType 压缩类型
     * @param listener        进度监听器
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToJson(QueryResult result, String filePath, Charset charset, boolean prettyPrint,
                                      CompressionType compressionType, ExportProgressListener listener) throws IOException {
        return exportToJson(result, filePath, charset, prettyPrint, compressionType, listener, null);
    }

    /**
     * 导出查询结果到JSON文件
     *
     * @param result          查询结果
     * @param filePath        文件路径
     * @param charset         字符集
     * @param prettyPrint     是否格式化输出
     * @param compressionType 压缩类型
     * @param listener        进度监听器
     * @param cancelFlag      取消标志
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToJson(QueryResult result, String filePath, Charset charset, boolean prettyPrint,
                                      CompressionType compressionType, ExportProgressListener listener,
                                      AtomicBoolean cancelFlag) throws IOException {
        if (result == null) {
            throw new IllegalArgumentException("Query result cannot be null");
        }
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        // 确保目录存在
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 根据压缩类型调整文件路径
        String finalFilePath = filePath;
        if (compressionType == CompressionType.GZIP && !filePath.endsWith(".gz")) {
            finalFilePath = filePath + ".gz";
        } else if (compressionType == CompressionType.ZIP && !filePath.endsWith(".zip")) {
            finalFilePath = filePath + ".zip";
        }

        // 创建JSON对象映射器
        ObjectMapper objectMapper = new ObjectMapper();
        if (prettyPrint) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        // 准备输出流
        OutputStream outputStream = null;
        try {
            // 根据压缩类型创建不同的输出流
            switch (compressionType) {
                case GZIP:
                    outputStream = new GZIPOutputStream(new FileOutputStream(finalFilePath));
                    break;
                case ZIP:
                    ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(finalFilePath));
                    String entryName = new File(filePath).getName();
                    zipOut.putNextEntry(new ZipEntry(entryName));
                    outputStream = zipOut;
                    break;
                case NONE:
                default:
                    outputStream = new FileOutputStream(finalFilePath);
                    break;
            }

            // 检查是否取消
            if (cancelFlag != null && cancelFlag.get()) {
                if (listener != null) {
                    listener.onCancelled();
                }
                return null;
            }

            // 写入JSON数据
            try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, charset)) {
                objectMapper.writeValue(writer, result.getRows());
            }

            // 如果是ZIP格式，需要关闭ZIP条目
            if (compressionType == CompressionType.ZIP) {
                ((ZipOutputStream) outputStream).closeEntry();
            }

            // 完成导出
            if (listener != null) {
                listener.onProgressUpdate(100, "Export completed");
                listener.onComplete(finalFilePath);
            }

            log.info("JSON export completed: {}", finalFilePath);
            return finalFilePath;
        } catch (IOException e) {
            if (listener != null) {
                listener.onError("Export failed: " + e.getMessage());
            }
            log.error("Failed to export JSON: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    log.error("Failed to close output stream: {}", e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 导出查询结果到Excel文件
     *
     * @param result   查询结果
     * @param filePath 文件路径
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToExcel(QueryResult result, String filePath) throws IOException {
        return exportToExcel(result, filePath, null);
    }

    /**
     * 导出查询结果到Excel文件
     *
     * @param result   查询结果
     * @param filePath 文件路径
     * @param listener 进度监听器
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToExcel(QueryResult result, String filePath, ExportProgressListener listener) throws IOException {
        return exportToExcel(result, filePath, listener, null);
    }

    /**
     * 导出查询结果到Excel文件
     *
     * @param result     查询结果
     * @param filePath   文件路径
     * @param listener   进度监听器
     * @param cancelFlag 取消标志
     * @return 导出的文件路径
     * @throws IOException IO异常
     */
    public static String exportToExcel(QueryResult result, String filePath, ExportProgressListener listener,
                                       AtomicBoolean cancelFlag) throws IOException {
        if (result == null) {
            throw new IllegalArgumentException("Query result cannot be null");
        }
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        // 确保目录存在
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 获取列定义和数据行
        List<ColumnDefinition> columns = result.getColumns();
        List<Map<String, Object>> rows = result.getRows();
        int totalRows = rows.size();

        // 创建工作簿和工作表
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Query Result");

            // 创建表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // 创建表头行
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i).getName());
                cell.setCellStyle(headerStyle);
            }

            // 写入数据行
            int rowCount = 0;
            for (int i = 0; i < rows.size(); i++) {
                // 检查是否取消
                if (cancelFlag != null && cancelFlag.get()) {
                    if (listener != null) {
                        listener.onCancelled();
                    }
                    return null;
                }

                Row row = sheet.createRow(i + 1);
                Map<String, Object> dataRow = rows.get(i);

                for (int j = 0; j < columns.size(); j++) {
                    Cell cell = row.createCell(j);
                    Object value = dataRow.get(columns.get(j).getField());
                    setCellValue(cell, value);
                }

                // 更新进度
                rowCount++;
                if (listener != null && rowCount % 1000 == 0) {
                    int progress = (int) ((double) rowCount / totalRows * 100);
                    listener.onProgressUpdate(progress, "Exported " + rowCount + " of " + totalRows + " rows");
                }
            }

            // 自动调整列宽
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // 写入文件
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }

            // 完成导出
            if (listener != null) {
                listener.onProgressUpdate(100, "Export completed: " + totalRows + " rows");
                listener.onComplete(filePath);
            }

            log.info("Excel export completed: {}", filePath);
            return filePath;
        } catch (IOException e) {
            if (listener != null) {
                listener.onError("Export failed: " + e.getMessage());
            }
            log.error("Failed to export Excel: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 设置单元格值
     *
     * @param cell  单元格
     * @param value 值
     */
    private static void setCellValue(Cell cell, Object value) {
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number) {
            if (value instanceof Integer || value instanceof Long) {
                cell.setCellValue(((Number) value).longValue());
            } else {
                cell.setCellValue(((Number) value).doubleValue());
            }
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    /**
     * 压缩类型
     */
    public enum CompressionType {
        NONE,
        GZIP,
        ZIP
    }

    /**
     * 导出进度监听器
     */
    public interface ExportProgressListener {
        /**
         * 进度更新
         *
         * @param progress 进度百分比 (0-100)
         * @param message  进度消息
         */
        void onProgressUpdate(int progress, String message);

        /**
         * 导出完成
         *
         * @param filePath 导出文件路径
         */
        void onComplete(String filePath);

        /**
         * 导出错误
         *
         * @param error 错误信息
         */
        void onError(String error);

        /**
         * 导出取消
         */
        void onCancelled();
    }
}
