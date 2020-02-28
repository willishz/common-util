package com.willishz.util;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * excel工具类
 */
public class ExcelUtil {

    private static Log logger = LogFactory.getLog(ExcelUtil.class);

    public static final String EXCEL_2003 = "xls";
    public static final String EXCEL_2007 = "xlsx";

    /**
     * 读取excel文档内容
     *
     * @param excel excel文件
     * @param elementName 实体类名标识
     * @param configPath 配置文件路径
     * @param clasz 实体类class
     * @param rowNum 从哪行开始读(跳过标题)
     * @param uploadFileName 上传文件名(用于取得后缀然后计算excel版本)
     * @return
     */
    public static <T> List<T> readExcelToVo(File excel, String elementName, String configPath, Class<T> clasz, int rowNum,
            String uploadFileName) {
        String excelVersion = uploadFileName.substring(uploadFileName.lastIndexOf(".") + 1);
        List<ConfigElement> configElementList = configElementFromXml(configPath, elementName, clasz);
        if (EXCEL_2003.equals(excelVersion)) {
            return readExcel2003(excel, configElementList, clasz, rowNum);
        } else if (EXCEL_2007.equals(excelVersion)) {
            return readExcel2007(excel, configElementList, clasz, rowNum);
        } else {
            return null;
        }
    }

    private static <T> List<T> readExcel2007(File file, List<ConfigElement> elements, Class<T> cl, int rowNum) {
        List<T> list = new ArrayList<T>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            // 第一张工作表
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator rows = sheet.rowIterator();
            int num = 0;
            while (rows.hasNext()) {
                num++;
                XSSFRow row = (XSSFRow) rows.next();
                if (num >= rowNum) {
                    for (int i = 0; i < elements.size(); i++) {
                        XSSFCell cell = row.getCell(i);
                        if (cell == null) {
                            ConfigElement element = elements.get(i);
                            element.setValue(null);
                            continue;
                        }
                        int ctype = cell.getCellType();
                        String value = null;
                        if (XSSFCell.CELL_TYPE_STRING == ctype) {
                            value = cell.getStringCellValue().trim();
                        } else if (XSSFCell.CELL_TYPE_NUMERIC == ctype) {
                            java.text.DecimalFormat formatter = new java.text.DecimalFormat("###.###");
                            DataFormatter df = new DataFormatter();
                            df.setDefaultNumberFormat(formatter);
                            value = df.formatCellValue(cell);
                        } else if (XSSFCell.CELL_TYPE_BOOLEAN == ctype) {
                            value = String.valueOf(cell.getBooleanCellValue());
                        } else if (XSSFCell.CELL_TYPE_FORMULA == ctype) {
                            value = "CAN NOT READ FORMULA";
                        } else {
                            value = cell.getRichStringCellValue().toString();
                        }
                        ConfigElement element = elements.get(i);
                        element.setValue(value != null ? value.trim() : value);
                    }
                    T object = configObject(cl, elements);
                    if (object != null) {
                        list.add(object);
                    }
                }
            }
            return list;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static <T> List<T> readExcel2003(File file, List<ConfigElement> elements, Class<T> cl, int rowNum) {
        List<T> list = new ArrayList<T>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(fis);
            // 第一张工作表
            HSSFSheet sheet = workbook.getSheetAt(0);
            Iterator rows = sheet.rowIterator();
            int num = 0;
            while (rows.hasNext()) {
                num++;
                HSSFRow row = (HSSFRow) rows.next();
                if (num >= rowNum) {
                    for (int i = 0; i < elements.size(); i++) {
                        HSSFCell cell = row.getCell(i);
                        if (cell == null) {
                            ConfigElement element = elements.get(i);
                            element.setValue(null);
                            continue;
                        }
                        int ctype = cell.getCellType();
                        String value = null;
                        if (HSSFCell.CELL_TYPE_STRING == ctype) {
                            value = cell.getStringCellValue();
                        } else if (HSSFCell.CELL_TYPE_NUMERIC == ctype) {
                            value = String.valueOf(cell.getNumericCellValue());
                        } else if (HSSFCell.CELL_TYPE_BOOLEAN == ctype) {
                            value = String.valueOf(cell.getBooleanCellValue());
                        } else {
                            value = cell.getRichStringCellValue().toString();
                        }
                        ConfigElement element = elements.get(i);
                        element.setValue(value != null ? value.trim() : value);
                    }
                    T object = configObject(cl, elements);
                    if (object != null) {
                        list.add(object);
                    }
                }
            }
            return list;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 保存具体数据
     * @param clasz
     * @param elements
     * @param <T>
     * @return
     */
    private static <T> T configObject(Class<T> clasz, List<ConfigElement> elements) {
        try {
            T object = clasz.newInstance();
            Method[] methods = clasz.getDeclaredMethods();
            Map<String, Method> methodsMap = new HashMap();
            for (Method _method : methods) {
                methodsMap.put(_method.getName(), _method);
            }
            Method method = null;
            for (ConfigElement element : elements) {
                String name = element.getName();
                String value = element.getValue();
                Boolean notNull = element.getNotNull();
                Integer maxLength = element.getMaxLength();
                String defaultV = element.getDefaultVal();
                if (notNull && value == null) {
                    if (defaultV == null) {
                        return null;
                    } else {
                        value = defaultV;
                    }
                }
                if (maxLength != null) {
                    if (value.length() > maxLength) {
                        return null;
                    }
                }
                String fs = name.substring(0, 1).toUpperCase();
                String ss = name.substring(1, name.length());
                String typeName = element.getType();
                Object fieldValue = null;
                if ("date".equals(typeName)) {
                    SimpleDateFormat sdf = StringUtil.sdf_date.get();
                    if (element.getDateFormat() != null) {
                        sdf = new SimpleDateFormat(element.getDateFormat());
                    }
                    if (StringUtil.isNotEmpty(value)) {
                        fieldValue = sdf.parse(value);
                    }
                } else if ("integer".equals(typeName)) {
                    if (StringUtil.isNotEmpty(value)) {
                        fieldValue = Integer.parseInt(value);
                    }
                } else if ("decimal".equals(typeName)) {
                    if (StringUtil.isNotEmpty(value)) {
                        fieldValue = new BigDecimal(value);
                    }
                } else if ("double".equals(typeName)) {
                    if (StringUtil.isNotEmpty(value)) {
                        fieldValue = Double.valueOf(value).doubleValue();
                    }
                } else if ("boolean".equals(typeName)) {
                    if (StringUtil.isNotEmpty(value)) {
                        fieldValue = Boolean.valueOf(value);
                    }
                } else if ("list".equals(typeName)) {
                    if (StringUtil.isNotEmpty(value)) {
                        // 获取原list value
                        Method methodGet = methodsMap.get("get" + fs + ss);
                        Object o = methodGet.invoke(object, new Object[]{});
                        List list = null;
                        if (o == null) {
                            list = new ArrayList();
                        } else {
                            list = (List) o;
                        }
                        list.add(String.valueOf(value));
                        fieldValue = list;
                    }
                } else {
                    fieldValue = value;
                }
                method = methodsMap.get("set" + fs + ss);
                if (StringUtil.isNotEmpty(fieldValue)) {
                    try {
                        method.invoke(object, new Object[]{fieldValue});
                    } catch (Exception e) {
                        System.out.println(method.getName() + " fieldValue:" + fieldValue);
                    }
                }
            }
            return object;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据xml模板，数据集合 生成excel inputstream流
     *
     *            xml路径
     * @param elementName
     *            根元素name
     * @param hasTitle
     *            是否存在标题.标题将显示为子元素名称
     * @param objs
     *            集合数据
     * @param clasz
     *            反射class
     * @param width
     *            单元格宽度
     * @param strings
     *            首行数据
     * @return
     */
    private static InputStream getExcelInput(String configPath, String elementName, boolean hasTitle, List objs, Class<?> clasz, int width, String... strings) {
        List<ConfigElement> ces = configElementFromXml(configPath, elementName, clasz);
        return getInput(width, ces, objs, hasTitle, strings);
    }

    /**
     * 根据，excel列需求信息，数据集合 生成excel inputstream流
     *
     * @param ces
     * @param objs
     * @param hasTitle
     * @param strings
     * @return
     */
    private static InputStream getInput(int width, List<ConfigElement> ces, List<Object> objs, boolean hasTitle, String... strings) {

        // 生成Excel表格
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet sheet = workBook.createSheet();
        sheet.setDefaultColumnWidth(width);
        // 定义样式
        HSSFCellStyle contentStyle = workBook.createCellStyle();
        HSSFFont contentFont = workBook.createFont(); // 定义字体
        contentFont.setFontName("Arial");
        contentFont.setFontHeightInPoints((short) 10);

        contentStyle.setFont(contentFont);
        contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);

        int rowId = 0;

        if (strings != null && strings.length != 0) {
            HSSFRow row = sheet.createRow(rowId++);
            int colId = 0;
            for (String str : strings) {
                HSSFCell cell = row.createCell(colId++);
                cell.setCellStyle(contentStyle);
                cell.setCellValue(str);
            }
        }

        // 标题
        if (hasTitle) {
            HSSFRow row = sheet.createRow(rowId++);
            int colId = 0;
            for (ConfigElement ce : ces) {
                HSSFCell cell = row.createCell(colId++);
                cell.setCellStyle(contentStyle);
                String value = ce.getExcelField();
                if (StringUtil.isEmpty(value)) {
                    value = ce.getName();
                }
                cell.setCellValue(value);
            }
        }

        // 数据
        for (int i = 0; i < objs.size(); i++) {
            Object obj = objs.get(i);
            HSSFRow row = sheet.createRow(rowId++);
            // 字段
            int colId = 0;
            for (ConfigElement ce : ces) {
                // 添加内容
                HSSFCell cell = row.createCell(colId++);
                cell.setCellStyle(contentStyle);
                Object value = getValueByField(obj, ce.getName());
                Boolean notnull = ce.getNotNull();
                String valueOrDefault = null;
                String defaValue = ce.getDefaultVal();
//                Boolean isExpand = ce.getIsExpand();
                if (notnull) {
                    valueOrDefault = StringUtil.toString(value);
                } else {
                    if (value == null) {
                        valueOrDefault = StringUtil.toString(defaValue);
                    } else {
                        valueOrDefault = StringUtil.toString(value);
                    }
                }
                cell.setCellValue(valueOrDefault);
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            workBook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] tempBuff = outputStream.toByteArray();

        InputStream input = new ByteArrayInputStream(tempBuff, 0, tempBuff.length);

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return input;
    }

    /**
     * 配置每个属性需求信息
     *
     * @param elementName
     * @param clasz
     * @return
     */
    private static List<ConfigElement> configElementFromXml(String configPath, String elementName, Class<?> clasz) {
        SAXReader reader = new SAXReader();
        Document configDocument = null;
        try {
            configDocument = reader.read(new File(configPath));
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        if (configDocument == null) {
            return null;
        }

        // root
        Element root = configDocument.getRootElement();
        if (root == null) {
            return null;
        }
        List<ConfigElement> list = new ArrayList();
        // element
        Iterator<Element> elements = root.elementIterator();
        while (elements.hasNext()) {
            Element element = elements.next();
            String name = element.attributeValue("name");
            String cxml = element.attributeValue("class");
            if (name == null || "".equals(name) || cxml == null || "".equals(cxml)) {
                continue;
            }
            if (!"element".equals(element.getName())) {
                continue;
            }
            // root field
            if (name.equals(elementName) && cxml.equals(clasz.getName())) {
                Iterator<Element> fields = element.elementIterator();
                // field
                while (fields.hasNext()) {
                    Element field = fields.next();
                    if ("field".equals(field.getName())) {
                        String fieldName = field.attributeValue("name");
                        String type = field.attributeValue("type");
                        boolean isExpand = field.attributeValue("isExpand") != null ? Boolean.valueOf(field.attributeValue("isExpand")) : false;
                        boolean notNull = field.attributeValue("notNull") != null ? Boolean.valueOf(field.attributeValue("notnull")) : false;
                        String value = field.attributeValue("value");
                        String defaultVal = field.attributeValue("defaultVal");
                        String excelField = field.attributeValue("excelField");
                        Integer maxLength = field.attributeValue("maxLength") != null ? Integer.valueOf(field.attributeValue("maxLength")) : null;
                        String dateFormat = field.attributeValue("dateFormat");
                        ConfigElement ce = new ConfigElement(fieldName, value, notNull, defaultVal, excelField, isExpand, type, maxLength, dateFormat);
                        list.add(ce);
                    }
                }
            } else {
                logger.error("element not found name:" + name + " elementName:" + elementName + "classAttribute:"+ cxml + "className:" + clasz.getName());
            }
        }
        return list;
    }

    private static Object getValueByField(Object object, String fieldName) {
        try {
            Class c = object.getClass();
            Field f = c.getDeclaredField(fieldName);
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String methodName = "get" + firstLetter + fieldName.substring(1);
            Method getMethod = c.getMethod(methodName, new Class[] {});
            Object value = getMethod.invoke(object, new Object[] {});
            return value;
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 导出excel文件到response下载
     *
     * @param downloadFileName 下载文件名
     * @param response         response
     * @param configPath         xml路径
     * @param elementName      根元素name
     * @param hasTitle         是否存在标题.标题将显示为子元素名称
     * @param objs             集合数据
     * @param clasz            反射class
     * @param width            单元格宽度
     * @param strings          首行标题
     * @return
     */
    public static void exportExcelFileToResponse(String downloadFileName, HttpServletResponse response, String configPath, String elementName, boolean hasTitle, List objs, Class<?> clasz, int width, String... strings) {
        InputStream inputStream = null;
        OutputStream output = null;
        response.setContentType("application/octet-stream;charset=UTF-8");
        try {
            inputStream = getExcelInput(configPath, elementName, hasTitle, objs, clasz, width, strings);
            response.setHeader("Content-Disposition", "attachment;filename=" + java.net.URLEncoder.encode(downloadFileName, "UTF-8"));
            // 客户端不缓存
            response.setHeader("Pragma", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
            output = response.getOutputStream();
            byte[] b = new byte[inputStream.available()];
            int bytesRead;
            while ((bytesRead = inputStream.read(b)) != -1) {
                output.write(b, 0, bytesRead);
            }
            output.flush();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 导出excel文件
     * @param exportConfPath 导出excel的列配置文件xml
     * @param elementName 导出excel的配置类名
     * @param cellWidth 列宽
     * @param clazz 导出类名
     * @param exportFilePath 导出文件路径
     * @param exportFileName 导出文件名
     * @param rows 数据
     * @param strings 首行数据
     * @return
     * @throws Exception
     */
    public static String createAndSaveExcel(String exportConfPath, String elementName,
                                                 Integer cellWidth, Class<?> clazz, String exportFilePath, String exportFileName, List rows, String... strings) throws Exception {
        File file = null;
        String filePath = null;
        try {
            XMLConfiguration xmlConfiguration = new XMLConfiguration(exportConfPath);
            file = xmlConfiguration.getFile();
            filePath = file.getPath();
            File dir = new File(exportFilePath);
            if (!dir.exists()) {
                //创建文件夹
                logger.info("【导出" + exportFileName + "-->导入的文件夹不存在，创建文件夹】");
                dir.mkdirs();
            }
        } catch (Exception e) {
            logger.error("【导出" + exportFileName + "-->读取配置文件时出错】", e);
            e.printStackTrace();
            throw new RuntimeException("【导出" + exportFileName + "-->读取配置文件时出错】");
        }
        try {
            InputStream input = new ExcelUtil().getExcelInputSetWidth(cellWidth, filePath, elementName, true, rows, clazz, strings);
            exportFileName = exportFileName + ".xls";
            writeFileOut(exportFilePath, exportFileName, input);
            return exportFilePath + exportFileName;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 根据xml模板，数据集合 生成excel inputstream流
     *
     * @param filePath    xml路径
     * @param elementName 根元素name
     * @param hasTitle    是否存在标题.标题将显示为子元素名称
     * @param objs        集合数据
     * @param c           反射class
     * @param strings     首行数据
     * @return
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static InputStream getExcelInputSetWidth(int width, String filePath, String elementName, boolean hasTitle,
                                                    List objs, Class<?> c, String... strings) {
        List<ConfigElement> ces = configElementFromXml(filePath, elementName, c);
        return getInputSetWidth(width, ces, objs, hasTitle, strings);
    }

    /**
     * 根据，excel列需求信息，数据集合 生成excel inputstream流
     *
     * @param ces
     * @param objs
     * @param hasTitle
     * @param strings
     * @return
     */
    static InputStream getInputSetWidth(int width, List<ConfigElement> ces, List<Object> objs, boolean hasTitle,
                                        String... strings) {

        // 生成Excel表格
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet sheet = workBook.createSheet();
        sheet.setDefaultColumnWidth(width);
        // 定义样式
        HSSFCellStyle contentStyle = workBook.createCellStyle();
        HSSFFont contentFont = workBook.createFont(); // 定义字体
        contentFont.setFontName("Arial");
        contentFont.setFontHeightInPoints((short) 10);

        contentStyle.setFont(contentFont);
        contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);

        int rowId = 0;

        if (strings != null && strings.length != 0) {
            HSSFRow row = sheet.createRow(rowId++);
            int colId = 0;
            for (String str : strings) {
                HSSFCell cell = row.createCell(colId++);
                cell.setCellStyle(contentStyle);
                cell.setCellValue(str);
            }
        }

        // 标题
        if (hasTitle) {
            HSSFRow row = sheet.createRow(rowId++);
            int colId = 0;
            for (ConfigElement ce : ces) {
                HSSFCell cell = row.createCell(colId++);
                cell.setCellStyle(contentStyle);
                String value = ce.getExcelField();
                if (value == null || "".equals(value)) {
                    value = ce.getName();
                }
                cell.setCellValue(value);
            }
        }

        // 数据
        for (int i = 0; i < objs.size(); i++) {
            Object obj = objs.get(i);
            HSSFRow row = sheet.createRow(rowId++);
            // 字段
            int colId = 0;
            for (ConfigElement ce : ces) {
                // 添加内容
                HSSFCell cell = row.createCell(colId++);
                cell.setCellStyle(contentStyle);
                Object value = getValueByField(obj, ce.getName());
                Boolean notNull = ce.getNotNull();
                String lValue = null;
                String defaValue = ce.getDefaultVal();
                Boolean isExpand = ce.getIsExpand();
                // 该字段规则需要系统生成
                if (isExpand) {
                    // 扩展字段生成规则
                    Format format = ce.getFormat();
                    int maxLength = ce.getMaxLength();
                    if (Format.number.equals(format)) {
                        String iStr = String.valueOf(i);
                        int lLength = maxLength - iStr.length();
                        StringBuffer sbuffer = new StringBuffer();
                        for (int j = 0; j < lLength; j++) {
                            sbuffer.append("0");
                        }
                        lValue = sbuffer.toString() + i;
                    } else {
                        // 随机字母
                        lValue = "" + StringUtil.buildRandomDigit(maxLength);
                    }
                } else {
                    if (!notNull) {
                        lValue = value == null ? (defaValue == null ? "" : defaValue) : value.toString();
                    } else {
                        if (value == null) {
                            lValue = defaValue == null ? "" : defaValue;
                        } else {
                            lValue = value.toString();
                        }
                    }
                }
                if ("0".equals(ce.getType())) {
                    cell.setCellValue((lValue == null || "".equals(lValue)) ? 0 : Double.parseDouble(lValue));
                    cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                } else {
                    cell.setCellValue(lValue);
                }
            }
            for (int cols = 0; cols < colId; cols++) {
                // 调整每一列宽度
                sheet.autoSizeColumn(cols);
                // 解决自动设置列宽中文失效的问题
                sheet.setColumnWidth(cols, sheet.getColumnWidth(cols) * 15 / 10);
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            workBook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] tempBuff = outputStream.toByteArray();

        InputStream input = new ByteArrayInputStream(tempBuff, 0, tempBuff.length);

        try {
            outputStream.close();
        } catch (IOException e) {
            logger.error(e);
            e.printStackTrace();
        }

        return input;
    }

    private static void writeFileOut(String generateFileAddress, String fileName, InputStream input) throws Exception{
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(new File(generateFileAddress + fileName));
            byte[] b = new byte[input.available()];
            int bytesRead;
            while ((bytesRead = input.read(b)) != -1) {
                fos.write(b, 0, bytesRead);
            }
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("创建文件出错");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("输出文件出错");
        }
    }

    /**
     * 导出excel文件(自动覆盖旧文件)
     *
     * @param targetFileName 目标文件名
     * @param configPath       xml路径
     * @param elementName    根元素name
     * @param hasTitle       是否存在标题.标题将显示为子元素名称
     * @param objs           集合数据
     * @param clasz          反射class
     * @param width          单元格宽度
     * @param strings        首行标题
     * @return
     */
    public static void exportExcelFileToFile(String targetFileName, String configPath, String elementName, boolean hasTitle, List objs, Class<?> clasz, int width, String... strings) {
        InputStream inputStream = null;
        FileOutputStream fos = null;
        try {
            inputStream = getExcelInput(configPath, elementName, hasTitle, objs, clasz, width, strings);
            fos = new FileOutputStream(targetFileName);
            int ch = 0;
            while((ch = inputStream.read()) != -1) {
                fos.write(ch);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 字段的config配置文件
     */
    static class ConfigElement {

        /**
         * 映射类字段名
         */
        private String name;
        /**
         * 映射类字段的数据类型
         * string(默认) date integer decimal double boolean list
         */
        private String type;
        /**
         * 是否必填
         */
        private Boolean notNull;
        /**
         * 为空时的默认值
         */
        private String defaultVal;
        /**
         * 字段中文名(生成excel时的标题)
         */
        private String excelField;
        /**
         * 是否需要计算得出(例如身份证号需要混淆)
         */
        private Boolean isExpand;
        /**
         * number或string
         */
        private Format format;
        private String customfield;
        /**
         * 字段最大长度(目前不用)
         */
        private Integer maxLength;
        /**
         * type为日期格式时的格式化
         */
        private String dateFormat;
        /**
         * 直接设值(覆盖excel导入的值)
         */
        private String value;

        public ConfigElement(String name, String value, Boolean notNull, String defaultVal, String excelField, Boolean isExpand, String type, Integer maxLength, String dateFormat) {
            this.name = name;
            this.value = value;
            this.notNull = notNull;
            this.defaultVal = defaultVal;
            this.excelField = excelField;
            this.isExpand = isExpand;
            this.type = type;
            this.maxLength = maxLength;
            this.dateFormat = dateFormat;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Boolean getNotNull() {
            return notNull;
        }

        public void setNotNull(Boolean notNull) {
            this.notNull = notNull;
        }

        public String getDefaultVal() {
            return defaultVal;
        }

        public void setDefaultVal(String defaultVal) {
            this.defaultVal = defaultVal;
        }

        public String getExcelField() {
            return excelField;
        }

        public void setExcelField(String excelField) {
            this.excelField = excelField;
        }

        public Boolean getIsExpand() {
            return isExpand;
        }

        public void setIsExpand(Boolean isExpand) {
            isExpand = isExpand;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getMaxLength() {
            return maxLength;
        }

        public void setMaxLength(Integer maxLength) {
            this.maxLength = maxLength;
        }

        public String getDateFormat() {
            return dateFormat;
        }

        public void setDateFormat(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        public Boolean getExpand() {
            return isExpand;
        }

        public void setExpand(Boolean expand) {
            isExpand = expand;
        }

        public Format getFormat() {
            return format;
        }

        public void setFormat(Format format) {
            this.format = format;
        }

        public String getCustomfield() {
            return customfield;
        }

        public void setCustomfield(String customfield) {
            this.customfield = customfield;
        }

        @Override
        public String toString() {
            return "ConfigElement{" +
                    "name='" + name + '\'' +
                    ", value='" + value + '\'' +
                    ", notNull=" + notNull +
                    ", defaultVal='" + defaultVal + '\'' +
                    ", excelField='" + excelField + '\'' +
                    ", isExpand=" + isExpand +
                    ", type=" + type +
                    ", maxLength=" + maxLength +
                    ", dateFormat=" + dateFormat +
                    '}';
        }

    }

    enum Format {
        number, string
    }

    public static void main(String[] args) throws Exception {
        String importConfPath = "D:\\upload\\deduct\\conf\\deduct_import.xml";
        File uploadFile = new File("D:\\upload\\deduct\\import\\代扣批次1.xlsx");
        List<Object> list = ExcelUtil.readExcelToVo(uploadFile, "deduct_import", importConfPath, Object.class, 2, "代扣批次1.xlsx");
        String exportConfPath = "D:\\upload\\deduct\\conf\\deduct_export.xml";
        List<Object> rows = new ArrayList();
        Object deductExportVO = new Object();
        rows.add(deductExportVO);
        ExcelUtil.createAndSaveExcel(exportConfPath, "deduct_export", 22, Object.class, "D:\\upload\\deduct\\export\\", "存公记录1", rows);
    }
}
