package cn.hawy.quick.agent.modular.business.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:这是一个通用的方法，利用了JAVA的反射机制，
 * 		可以将放置在JAVA集合中并且符号一定条件的数据以EXCEL 的形式输出到指定IO设备上
  * @param <T> 应用泛型，代表任意一个符合javabean风格的类
  * 注意这里为了简单起见，boolean型的属性xxx的get器方式为getXxx(),而不是isXxx()
  * byte[]表jpg格式的图片数据
 * copyright @Company dw, @Time 2017年8月8日 下午1:53:12, @author Wy, @Version 1.0
 */
public class ExportExcelUtil<T> {

	private static final String Comment_CONTENT = "daoheyunke"; // 作者
	private static final String Comment_AUTHOR = "daoheyunke"; // 作者

	private static short titleFillForegroundColor = HSSFColor.WHITE.index;
	private static short titleFillPattern = HSSFCellStyle.SOLID_FOREGROUND;
	private static short titleBorderTop = HSSFCellStyle.BORDER_THIN;
	private static short titleBorderRight = HSSFCellStyle.BORDER_THIN;
	private static short titleBorderBottom = HSSFCellStyle.BORDER_THIN;
	private static short titleBorderLeft = HSSFCellStyle.BORDER_THIN;
	private static short titleAlignment = HSSFCellStyle.ALIGN_CENTER;
	private static short titleVerticalAlignment = HSSFCellStyle.VERTICAL_BOTTOM;
	private static short titleFontColor = HSSFColor.BLACK.index;
	private static short titleFontHeightInPoints = (short) 14;
	private static short titleFontBoldweight = HSSFFont.BOLDWEIGHT_BOLD;

	private static short headFillForegroundColor = HSSFColor.WHITE.index;
	private static short headFillPattern = HSSFCellStyle.SOLID_FOREGROUND;
	private static short headBorderTop = HSSFCellStyle.BORDER_THIN;
	private static short headBorderRight = HSSFCellStyle.BORDER_THIN;
	private static short headBorderBottom = HSSFCellStyle.BORDER_THIN;
	private static short headBorderLeft = HSSFCellStyle.BORDER_THIN;
	private static short headAlignment = HSSFCellStyle.ALIGN_CENTER;
	private static short headVerticalAlignment = HSSFCellStyle.VERTICAL_BOTTOM;
	private static short headFontColor = HSSFColor.BLACK.index;
	private static short headFontHeightInPoints = (short) 12;
	private static short headFontBoldweight = HSSFFont.BOLDWEIGHT_BOLD;

	private static short dataFillForegroundColor = HSSFColor.WHITE.index;
	private static short dataFillPattern = HSSFCellStyle.SOLID_FOREGROUND;
	private static short dataBorderTop = HSSFCellStyle.BORDER_THIN;
	private static short dataBorderRight = HSSFCellStyle.BORDER_THIN;
	private static short dataBorderBottom = HSSFCellStyle.BORDER_THIN;
	private static short dataBorderLeft = HSSFCellStyle.BORDER_THIN;
	private static short dataAlignment = HSSFCellStyle.ALIGN_CENTER;
	private static short dataVerticalAlignment = HSSFCellStyle.VERTICAL_CENTER;
	private static short dataFontColor = HSSFColor.BLACK.index;
	private static short dataFontHeightInPoints = (short) 10;
	private static short dataFontBoldweight = HSSFFont.BOLDWEIGHT_NORMAL;

	/**
	 * Method Description:导出Excel文件（单页签）
	 * 默认：	表格开始行的索引：0
	 * 			输出的日期格式："yyy-MM-dd"
	 * @param sheetName 表格页签名
	 * @param titleName 表格标题名（没有传null）
	 * @param colWidths 表格列宽数组
	 * @param colNames 表格列名数组
	 * @param dataVals 表格数据集合，集合中一定要放置符合javabean风格的类的对象。
	 * 			此方法支持的javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
	 * @param outps 与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	 * @return 总的excel行数
	 * @Time 2017年9月19日 下午5:02:10  @author Wy
	 */
	public int exportExcel(String sheetName, String titleName, int[] colWidths, String[] colNames, Collection<T> dataVals, OutputStream outps) {
		return exportExcel(sheetName, titleName, 0, colWidths, colNames, dataVals, outps, "yyyy-MM-dd");
	}

	/**
	 * Method Description:导出Excel文件（单页签）
	 * 默认：	表格开始行的索引：0
	 * @param sheetName 表格页签名
	 * @param titleName 表格标题名（没有传null）
	 * @param colWidths 表格列宽数组
	 * @param colNames 表格列名数组
	 * @param dataVals 表格数据集合，集合中一定要放置符合javabean风格的类的对象。
	 * 			此方法支持的javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
	 * @param outps 与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	 * @param dateFormat 如果有Date格式的数据，设定输出格式。默认为"yyy-MM-dd"
	 * @return 总的excel行数
	 * @Time 2017年9月19日 下午5:02:10  @author Wy
	 */
	public int exportExcel(String sheetName, String titleName, int[] colWidths, String[] colNames, Collection<T> dataVals, OutputStream outps, String dateFormat) {
		return exportExcel(sheetName, titleName, 0, colWidths, colNames, dataVals, outps, dateFormat);
	}

	/**
	 * Method Description:导出Excel文件（单页签）
	 * @param sheetName 表格页签名
	 * @param titleName 表格标题名（没有传null）
	 * @param rowIndex 表格开始行的索引
	 * @param colWidths 表格列宽数组
	 * @param colNames 表格列名数组
	 * @param dataVals 表格数据集合，集合中一定要放置符合javabean风格的类的对象。
	 * 			此方法支持的javabean属性的数据类型有基本数据类型及String,Date,byte[](图片数据)
	 * @param outps 与输出设备关联的流对象，可以将EXCEL文档导出到本地文件或者网络中
	 * @param dateFormat 如果有Date格式的数据，设定输出格式。默认为"yyy-MM-dd"
	 * @return 总的excel行数
	 * @Time 2017年9月19日 下午5:02:10  @author Wy
	 */
	@SuppressWarnings("unchecked")
	public int exportExcel(String sheetName, String titleName, int rowIndex, int[] colWidths,
						   String[] colNames, Collection<T> dataVals, OutputStream outps, String dateFormat) {
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 生成一个表格
		HSSFSheet sheet = workbook.createSheet(sheetName);
		// 设置表格默认列宽度为15个字节
		sheet.setDefaultColumnWidth((short) 20);
		sheet.setDefaultRowHeightInPoints(20);
		// 分别设置列宽
		if (colWidths != null) {
			for (int i = 0; i < colWidths.length; i++) {
				short w = (short) colWidths[i];
				sheet.setColumnWidth(i, w * 256);
			}
		}
		// 生成并设置一个样式，生成字体，把字体应用到样式
		HSSFCellStyle headStyle = setCellStyle(workbook, headFillForegroundColor, headFillPattern,
				headBorderTop, headBorderRight, headBorderBottom, headBorderLeft, headAlignment, headVerticalAlignment,
				headFontColor, headFontHeightInPoints, headFontBoldweight);
		// 生成并设置一个样式，生成字体，把字体应用到样式
		HSSFCellStyle dataStyle = setCellStyle(workbook, dataFillForegroundColor, dataFillPattern,
				dataBorderTop, dataBorderRight, dataBorderBottom, dataBorderLeft, dataAlignment, dataVerticalAlignment,
				dataFontColor, dataFontHeightInPoints, dataFontBoldweight);
		// 声明一个画图的顶级管理器
		HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		// 定义注释的大小和位置, 详见文档
//		HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
//		// 设置注释内容
//		comment.setString(new HSSFRichTextString(Comment_CONTENT));
//		// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
//		comment.setAuthor(Comment_AUTHOR);
		// Table Name
		if (titleName != null) {
			// firstRow lastRow firstCol lastCol
			int lastCol = colNames.length - 1;
			CellRangeAddress cra = new CellRangeAddress(0, 0, 0, lastCol);
			sheet.addMergedRegion(cra);
			HSSFRow titleRow = sheet.createRow(rowIndex++);
			HSSFCell firstCell = titleRow.createCell(0);
			HSSFCellStyle titleStyle = setCellStyle(workbook, titleFillForegroundColor, titleFillPattern,
					titleBorderTop, titleBorderRight, titleBorderBottom, titleBorderLeft, titleAlignment, titleVerticalAlignment,
					titleFontColor, titleFontHeightInPoints, titleFontBoldweight);
			HSSFCell lastCell = titleRow.createCell(lastCol);
			firstCell.setCellStyle(titleStyle);
			lastCell.setCellStyle(titleStyle);
			firstCell.setCellValue(titleName);
		}
		// Column Name
		HSSFRow colRow = sheet.createRow(rowIndex);
		for (short i = 0; i < colNames.length; i++) {
			HSSFCell colCell = colRow.createCell(i);
			colCell.setCellStyle(headStyle);
			HSSFRichTextString colName = new HSSFRichTextString(colNames[i]);
			colCell.setCellValue(colName);
		}
		// Data Collection
		Iterator<T> it = dataVals.iterator();
		while (it.hasNext()) {
			rowIndex++;
			colRow = sheet.createRow(rowIndex);
			// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
			T t = (T) it.next();
			Field[] fields = t.getClass().getDeclaredFields();
			for (short i = 0; i < fields.length; i++) {
				HSSFCell cell = colRow.createCell(i);
				cell.setCellStyle(dataStyle);
				Field field = fields[i];
				String fieldName = field.getName();
				String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
				try {
					Class tCls = t.getClass();
					Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
					Object value = getMethod.invoke(t, new Object[] {});
					// 判断值的类型后进行强制类型转换
					String textValue = null;
					/*
					if (value instanceof Integer) {
						int intValue = (Integer) value;
						cell.setCellValue(intValue);
					} else if (value instanceof Float) {
						float fValue = (Float) value;
						textValue = new HSSFRichTextString(String.valueOf(fValue));
						cell.setCellValue(textValue);
					} else if (value instanceof Double) {
						double dValue = (Double) value;
						textValue = new HSSFRichTextString(String.valueOf(dValue));
						cell.setCellValue(textValue);
					} else if (value instanceof Long) {
						long longValue = (Long) value;
						cell.setCellValue(longValue);
					} else if (value instanceof Boolean) {
						boolean bValue = (Boolean) value;
						textValue = bValue ? "男" : "女";
					} else
					 */
					boolean hasImg = false;
					if (hasImg) {
						if (value instanceof Date) {
							Date date = (Date) value;
							textValue = new SimpleDateFormat(dateFormat).format(date);
						} else if (value instanceof byte[]) {
							// 有图片时，设置行高为60px;
							colRow.setHeightInPoints(60);
							// 设置图片所在列宽度为80px,注意这里单位的一个换算
							sheet.setColumnWidth(i, (short) (35.7 * 80));
							// sheet.autoSizeColumn(i);
							byte[] bsValue = (byte[]) value;
							HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 6, rowIndex, (short) 6, rowIndex);
							anchor.setAnchorType(2);
							patriarch.createPicture(anchor, workbook.addPicture(bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
						} else{
							//其它数据类型都当作字符串简单处理
							textValue = value.toString();
						}
					} else {
						if (value instanceof Date) {
							Date date = (Date) value;
							textValue = new SimpleDateFormat(dateFormat).format(date);
						} else {
							//其它数据类型都当作字符串简单处理
							textValue = value.toString();
						}
					}
					//如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
					if(textValue != null){
						Pattern p = Pattern.compile("^//d+(//.//d+)?$");
						Matcher matcher = p.matcher(textValue);
						if(matcher.matches()){
							//是数字当作double处理
							cell.setCellValue(Double.parseDouble(textValue));
						}else{
							HSSFRichTextString richString = new HSSFRichTextString(textValue);
							/** 字体样式
							 HSSFFont cellFont = workbook.createFont();
							 cellFont.setColor(dataFontColor);
							 cellFont.setFontHeightInPoints(dataFontHeightInPoints);
							 cellFont.setBoldweight(dataFontBoldweight);
							 richString.applyFont(cellFont);
							 * */
							cell.setCellValue(richString);
						}
					}
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					//清理资源
				}
			}

		}
		try {
			workbook.write(outps);
			outps.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rowIndex;
	}

	/**
	 * Method Description:导出Excel文件（多页签）
	 * 默认：	每个页签数据行数：20000
	 * 			每个页签的开始行的索引：0
	 * 			输出的时间格式："yyyy-MM-dd"
	 * @param sheetName 页签名字（多页签会以尾注区分）
	 * @param titleName 标题名称
	 * @param colWidths 每个页签列宽数组
	 * @param colNames 每个页签列名数组
	 * @param dataVals 所有数据
	 * @param outps 输出流
	 * @return 总的excel行数
	 * @Time 2017年9月19日 下午4:49:42  @author Wy
	 */
	public int expExcel(String sheetName, String titleName, int[] colWidths, String[] colNames, List<T> dataVals, OutputStream outps) {
		return expExcel(sheetName, 20000, titleName, 0, colWidths, colNames, dataVals, outps, "yyyy-MM-dd");
	}

	/**
	 * Method Description:导出Excel文件（多页签）
	 * 默认：	每个页签的开始行的索引：0
	 * 			输出的时间格式：
	 * @param sheetName 页签名字（多页签会以尾注区分）
	 * @param sheetDataCont 每个页签数据行数
	 * @param titleName 标题名称
	 * @param colWidths 每个页签列宽数组
	 * @param colNames 每个页签列名数组
	 * @param dataVals 所有数据
	 * @param outps 输出流
	 * @return 总的excel行数
	 * @Time 2017年9月19日 下午4:49:42  @author Wy
	 */
	public int expExcel(String sheetName, int sheetDataCont, String titleName, int[] colWidths, String[] colNames, List<T> dataVals, OutputStream outps) {
		return expExcel(sheetName, sheetDataCont, titleName, 0, colWidths, colNames, dataVals, outps, "yyyy-MM-dd");
	}

	/**
	 * Method Description:导出Excel文件（多页签）
	 * 默认：	每个页签的开始行的索引：0
	 * @param sheetName 页签名字（多页签会以尾注区分）
	 * @param sheetDataCont 每个页签数据行数
	 * @param titleName 标题名称
	 * @param colWidths 每个页签列宽数组
	 * @param colNames 每个页签列名数组
	 * @param dataVals 所有数据
	 * @param outps 输出流
	 * @param dateFormat 输出的时间格式
	 * @return 总的excel行数
	 * @Time 2017年9月19日 下午4:49:42  @author Wy
	 */
	public int expExcel(String sheetName, int sheetDataCont, String titleName, int[] colWidths, String[] colNames, List<T> dataVals, OutputStream outps, String dateFormat) {
		return expExcel(sheetName, sheetDataCont, titleName, 0, colWidths, colNames, dataVals, outps, dateFormat);
	}

	/**
	 * Method Description:导出Excel文件（多页签）
	 * @param sheetName 页签名字（多页签会以尾注区分）
	 * @param sheetDataCont 每个页签数据行数
	 * @param titleName 标题名称
	 * @param rowBegin 每个页签的开始行的索引
	 * @param colWidths 每个页签列宽数组
	 * @param colNames 每个页签列名数组
	 * @param dataVals 所有数据
	 * @param outps 输出流
	 * @param dateFormat 输出的时间格式
	 * @return 总的excel行数
	 * @Time 2017年9月19日 下午4:49:42  @author Wy
	 */
	public int expExcel(String sheetName, int sheetDataCont, String titleName, int rowBegin,
						int[] colWidths, String[] colNames, List<T> dataVals, OutputStream outps, String dateFormat) {
		int rowCount = 0; // 总行数
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		int sheetIndexs = dataVals.size()/sheetDataCont;
		for (int i=0; i<sheetIndexs+1; i++) {
			int rowIndex = rowBegin;
			// 生成一个表格
			HSSFSheet sheet = workbook.createSheet(sheetName +"-"+ i);
			// 设置表格默认列宽度为15个字节
			sheet.setDefaultColumnWidth((short) 20);
			sheet.setDefaultRowHeightInPoints(20);
			// 分别设置列宽
			if (colWidths != null) {
				for (int cw = 0; cw < colWidths.length; cw++) {
					short w = (short) colWidths[cw];
					sheet.setColumnWidth(cw, w * 256);
				}
			}
			// 生成并设置一个样式，生成字体，把字体应用到样式
			HSSFCellStyle headStyle = setCellStyle(workbook, headFillForegroundColor, headFillPattern,
					headBorderTop, headBorderRight, headBorderBottom, headBorderLeft, headAlignment, headVerticalAlignment,
					headFontColor, headFontHeightInPoints, headFontBoldweight);
			// 生成并设置一个样式，生成字体，把字体应用到样式
			HSSFCellStyle dataStyle = setCellStyle(workbook, dataFillForegroundColor, dataFillPattern,
					dataBorderTop, dataBorderRight, dataBorderBottom, dataBorderLeft, dataAlignment, dataVerticalAlignment,
					dataFontColor, dataFontHeightInPoints, dataFontBoldweight);
			// 声明一个画图的顶级管理器
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			// 定义注释的大小和位置, 详见文档
//			HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
//			// 设置注释内容
//			comment.setString(new HSSFRichTextString(Comment_CONTENT));
//			// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
//			comment.setAuthor(Comment_AUTHOR);
			HSSFRow row = null;
			// Table Name
			if (titleName != null) {
				// firstRow lastRow firstCol lastCol
				int lastCol = colNames.length - 1;
				CellRangeAddress cra = new CellRangeAddress(0, 0, 0, lastCol);
				sheet.addMergedRegion(cra);
				row = sheet.createRow(rowIndex);
				HSSFCell firstCell = row.createCell(0);
				HSSFCellStyle titleStyle = setCellStyle(workbook, titleFillForegroundColor, titleFillPattern,
						titleBorderTop, titleBorderRight, titleBorderBottom, titleBorderLeft, titleAlignment, titleVerticalAlignment,
						titleFontColor, titleFontHeightInPoints, titleFontBoldweight);
				HSSFCell lastCell = row.createCell(lastCol);
				firstCell.setCellStyle(titleStyle);
				lastCell.setCellStyle(titleStyle);
				firstCell.setCellValue(titleName);
			}
			// Column Name
			if (colNames !=null && colNames.length > 0) {
				rowIndex++;
				row = sheet.createRow(rowIndex);
				for (short cn = 0; cn < colNames.length; cn++) {
					HSSFCell colCell = row.createCell(cn);
					colCell.setCellStyle(headStyle);
					HSSFRichTextString colName = new HSSFRichTextString(colNames[cn]);
					colCell.setCellValue(colName);
				}
			}
			// Data Collection
			int dataCount = (i + 1)*sheetDataCont;
			if (i==sheetIndexs) dataCount = dataVals.size();
			for (int j=i*sheetDataCont; j<dataCount; j++) {
				rowIndex++;
				row = sheet.createRow(rowIndex);
				// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
				T t =  (T) dataVals.get(j);
				Field[] fields = t.getClass().getDeclaredFields();
				for (short f = 0; f < fields.length; f++) {
					HSSFCell cell = row.createCell(f);
					cell.setCellStyle(dataStyle);
					Field field = fields[f];
					String fieldName = field.getName();
					String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					try {
						Class tCls = t.getClass();
						Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
						Object value = getMethod.invoke(t, new Object[] {});
						if(value==null){
							value="";
						}
						// 判断值的类型后进行强制类型转换
						String textValue = null;
						/*
					if (value instanceof Integer) {
						int intValue = (Integer) value;
						cell.setCellValue(intValue);
					} else if (value instanceof Float) {
						float fValue = (Float) value;
						textValue = new HSSFRichTextString(String.valueOf(fValue));
						cell.setCellValue(textValue);
					} else if (value instanceof Double) {
						double dValue = (Double) value;
						textValue = new HSSFRichTextString(String.valueOf(dValue));
						cell.setCellValue(textValue);
					} else if (value instanceof Long) {
						long longValue = (Long) value;
						cell.setCellValue(longValue);
					} else if (value instanceof Boolean) {
						boolean bValue = (Boolean) value;
						textValue = bValue ? "男" : "女";
					} else
						 */
						boolean hasImg = false;
						if (hasImg) {
							if (value instanceof Date) {
								Date date = (Date) value;
								textValue = new SimpleDateFormat(dateFormat).format(date);
							} else if (value instanceof byte[]) {
								// 有图片时，设置行高为60px;
								row.setHeightInPoints(60);
								// 设置图片所在列宽度为80px,注意这里单位的一个换算
								sheet.setColumnWidth(i, (short) (35.7 * 80));
								// sheet.autoSizeColumn(i);
								byte[] bsValue = (byte[]) value;
								HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 6, rowIndex, (short) 6, rowIndex);
								anchor.setAnchorType(2);
								patriarch.createPicture(anchor, workbook.addPicture(bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
							} else{
								//其它数据类型都当作字符串简单处理
								textValue = value.toString();
							}
						} else {
							if (value instanceof Date) {
								Date date = (Date) value;
								textValue = new SimpleDateFormat(dateFormat).format(date);
							} else {
								//其它数据类型都当作字符串简单处理
								textValue = value.toString();
							}
						}
						//如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
						if(textValue != null){
							Pattern p = Pattern.compile("^//d+(//.//d+)?$");
							Matcher matcher = p.matcher(textValue);
							if(matcher.matches()){
								//是数字当作double处理
								cell.setCellValue(Double.parseDouble(textValue));
							}else{
								HSSFRichTextString richString = new HSSFRichTextString(textValue);
								/** 字体样式
								 HSSFFont cellFont = workbook.createFont();
								 cellFont.setColor(dataFontColor);
								 cellFont.setFontHeightInPoints(dataFontHeightInPoints);
								 cellFont.setBoldweight(dataFontBoldweight);
								 richString.applyFont(cellFont);
								 * */
								cell.setCellValue(richString);
							}
						}
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						//清理资源
					}
				}

			}
			rowCount += rowIndex;
		}
		try {
			workbook.write(outps);
			outps.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rowCount;
	}

	/**
	 * Method Description:设置单元格属性
	 * @param workbook excel工作簿
	 * @param fillForegroundColor 前景颜色
	 * @param fillPattern 图案
	 * @param borderTop 上边框样式
	 * @param borderRight 右边框样式
	 * @param borderBottom 下边框样式
	 * @param borderLeft 左边框样式
	 * @param alignment 单元格水平位置
	 * @param verticalAlignment 单元格垂直位置
	 * @param fontColor 字体颜色
	 * @param fontHeightInPoints 字体字号
	 * @param fontBoldweight 字体加粗
	 * @return
	 * @Time 2017年8月9日 上午10:06:04  @author Wy
	 */
	public HSSFCellStyle setCellStyle(HSSFWorkbook workbook, short fillForegroundColor, short fillPattern,
									  short borderTop, short borderRight, short borderBottom, short borderLeft, short alignment, short verticalAlignment,
									  short fontColor, short fontHeightInPoints, short fontBoldweight) {
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setFillForegroundColor(fillForegroundColor);
		cellStyle.setFillPattern(fillPattern);
		cellStyle.setBorderTop(borderTop);
		cellStyle.setBorderRight(borderRight);
		cellStyle.setBorderBottom(borderBottom);
		cellStyle.setBorderLeft(borderLeft);
		cellStyle.setAlignment(alignment);
		cellStyle.setVerticalAlignment(verticalAlignment);
		cellStyle.setWrapText(true);     //自动换行
		HSSFFont cellFont = workbook.createFont();
		cellFont.setColor(fontColor);
		cellFont.setFontHeightInPoints(fontHeightInPoints);
		cellFont.setBoldweight(fontBoldweight);
		cellStyle.setFont(cellFont);
		return cellStyle;
	}


	/**
	 *  贷记卡客户申请信息表		特殊格式定制方法
	 * Method Description:导出Excel文件（多页签）
	 * @param sheetName 页签名字（多页签会以尾注区分）
	 * @param sheetDataCont 每个页签数据行数
	 * @param titleName 标题名称
	 * @param rowBegin 每个页签的开始行的索引
	 * @param colWidths 每个页签列宽数组
	 * @param colNames 每个页签列名数组
	 * @param dataVals 所有数据
	 * @param outps 输出流
	 * @param dateFormat 输出的时间格式
	 * @return 总的excel行数
	 * @Time   @author GWX
	 */
	public int expSpecialExcel(String sheetName, int sheetDataCont, String titleName, int rowBegin,
							   int[] colWidths, String[] colNames, List<T> dataVals, OutputStream outps, String dateFormat) throws MalformedURLException {
		int rowCount = 0; // 总行数
		// 声明一个工作薄
		HSSFWorkbook workbook = new HSSFWorkbook();
		int sheetIndexs = dataVals.size()/sheetDataCont;
		for (int i=0; i<sheetIndexs+1; i++) {
			int rowIndex = rowBegin;
			// 生成一个表格
			HSSFSheet sheet = workbook.createSheet(sheetName +"-"+ i);
			// 设置表格默认列宽度为15个字节
			sheet.setDefaultColumnWidth((short) 20);
			sheet.setDefaultRowHeightInPoints(20);
			// 分别设置列宽
			if (colWidths != null) {
				for (int cw = 0; cw < colWidths.length; cw++) {
					short w = (short) colWidths[cw];
					sheet.setColumnWidth(cw, w * 256);
				}
			}
			// 生成并设置一个样式，生成字体，把字体应用到样式
			HSSFCellStyle headStyle = setCellStyle(workbook, headFillForegroundColor, headFillPattern,
					headBorderTop, headBorderRight, headBorderBottom, headBorderLeft, headAlignment, headVerticalAlignment,
					headFontColor, (short)14, headFontBoldweight);
			// 生成并设置一个样式，生成字体，把字体应用到样式
			HSSFCellStyle dataStyle = setCellStyle(workbook, dataFillForegroundColor, dataFillPattern,
					dataBorderTop, dataBorderRight, dataBorderBottom, dataBorderLeft, dataAlignment, dataVerticalAlignment,
					dataFontColor, (short)12, dataFontBoldweight);
			// 声明一个画图的顶级管理器
			HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
			// 定义注释的大小和位置, 详见文档
//			HSSFComment comment = patriarch.createComment(new HSSFClientAnchor(0, 0, 0, 0, (short) 4, 2, (short) 6, 5));
//			// 设置注释内容
//			comment.setString(new HSSFRichTextString(Comment_CONTENT));
//			// 设置注释作者，当鼠标移动到单元格上是可以在状态栏中看到该内容.
//			comment.setAuthor(Comment_AUTHOR);
			HSSFRow row = null;
			// Table Name
			if (titleName != null) {
				// firstRow lastRow firstCol lastCol
				int lastCol = colNames.length - 1;
				CellRangeAddress cra = new CellRangeAddress(0, 0, 0, lastCol);
				sheet.addMergedRegion(cra);
				row = sheet.createRow(rowIndex);
				HSSFCell firstCell = row.createCell(0);
				HSSFCellStyle titleStyle = setCellStyle(workbook, titleFillForegroundColor, titleFillPattern,
						titleBorderTop, titleBorderRight, titleBorderBottom, titleBorderLeft, titleAlignment, titleVerticalAlignment,
						titleFontColor, titleFontHeightInPoints, titleFontBoldweight);
				HSSFCell lastCell = row.createCell(lastCol);
				firstCell.setCellStyle(titleStyle);
				lastCell.setCellStyle(titleStyle);
				firstCell.setCellValue(titleName);
			}
			//合并单元格
			CellRangeAddress row1 = new CellRangeAddress(1, 1, 4, 5);
			CellRangeAddress row3 = new CellRangeAddress(3, 3, 3, 5);
			CellRangeAddress row5 = new CellRangeAddress(5, 5, 3, 5);
			CellRangeAddress row61 = new CellRangeAddress(6, 6, 1, 2);
			CellRangeAddress row62 = new CellRangeAddress(6, 6, 4, 5);
			CellRangeAddress row71 = new CellRangeAddress(7, 7, 1, 2);
			CellRangeAddress row72 = new CellRangeAddress(7, 7, 3, 4);
			CellRangeAddress row8 = new CellRangeAddress(8, 8, 1, 5);
			CellRangeAddress row9 = new CellRangeAddress(9, 9, 1, 5);
			CellRangeAddress row10 = new CellRangeAddress(10, 10, 3, 5);
			CellRangeAddress row11 = new CellRangeAddress(11, 11, 1, 5);
			CellRangeAddress row12 = new CellRangeAddress(12, 12, 0, 5);
			CellRangeAddress row13to41 = new CellRangeAddress(13, 41, 0, 5);
			CellRangeAddress row42 = new CellRangeAddress(42, 42, 0, 5);
			sheet.addMergedRegion(row1);
			sheet.addMergedRegion(row3);
			sheet.addMergedRegion(row5);
			sheet.addMergedRegion(row61);
			sheet.addMergedRegion(row62);
			sheet.addMergedRegion(row71);
			sheet.addMergedRegion(row72);
			sheet.addMergedRegion(row8);
			sheet.addMergedRegion(row9);
			sheet.addMergedRegion(row10);
			sheet.addMergedRegion(row11);
			sheet.addMergedRegion(row12);
			sheet.addMergedRegion(row13to41);
			sheet.addMergedRegion(row42);
			/*// Column Name
			if (colNames !=null && colNames.length > 0) {
				rowIndex++;
				row = sheet.createRow(rowIndex);
				for (short cn = 0; cn < colNames.length; cn++) {
					HSSFCell colCell = row.createCell(cn);
					colCell.setCellStyle(headStyle);
					HSSFRichTextString colName = new HSSFRichTextString(colNames[cn]);
					colCell.setCellValue(colName);
				}
			}*/
			// Data Collection
			int dataCount = (i + 1)*sheetDataCont;
			if (i==sheetIndexs) dataCount = dataVals.size();
			for (int j=i*sheetDataCont; j<dataCount; j++) {
				rowIndex++;
				row = sheet.createRow(rowIndex);
				row.setHeightInPoints(20);
				if(rowIndex==6){
					row.setHeightInPoints(60);
				}
				if(rowIndex==8){
					row.setHeightInPoints(50);
				}
				// 利用反射，根据javabean属性的先后顺序，动态调用getXxx()方法得到属性值
				T t =  (T) dataVals.get(j);
				Field[] fields = t.getClass().getDeclaredFields();
				for (short f = 0; f < fields.length; f++) {
					HSSFCell cell = row.createCell(f);
					cell.setCellStyle(dataStyle);
					Field field = fields[f];
					String fieldName = field.getName();
					String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
					try {
						Class tCls = t.getClass();
						Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
						Object value = getMethod.invoke(t, new Object[] {});
						if(value==null){
							value="";
						}
						// 判断值的类型后进行强制类型转换
						String textValue = null;
						/*
					if (value instanceof Integer) {
						int intValue = (Integer) value;
						cell.setCellValue(intValue);
					} else if (value instanceof Float) {
						float fValue = (Float) value;
						textValue = new HSSFRichTextString(String.valueOf(fValue));
						cell.setCellValue(textValue);
					} else if (value instanceof Double) {
						double dValue = (Double) value;
						textValue = new HSSFRichTextString(String.valueOf(dValue));
						cell.setCellValue(textValue);
					} else if (value instanceof Long) {
						long longValue = (Long) value;
						cell.setCellValue(longValue);
					} else if (value instanceof Boolean) {
						boolean bValue = (Boolean) value;
						textValue = bValue ? "男" : "女";
					} else 
						 */
						boolean hasImg = false;
						if (hasImg) {
							if (value instanceof Date) {
								Date date = (Date) value;
								textValue = new SimpleDateFormat(dateFormat).format(date);
							} else if (value instanceof byte[]) {
								// 有图片时，设置行高为60px;
								row.setHeightInPoints(60);
								// 设置图片所在列宽度为80px,注意这里单位的一个换算
								sheet.setColumnWidth(i, (short) (35.7 * 80));
								// sheet.autoSizeColumn(i);
								byte[] bsValue = (byte[]) value;
								HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 6, rowIndex, (short) 6, rowIndex);
								anchor.setAnchorType(2);
								patriarch.createPicture(anchor, workbook.addPicture(bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));
							} else{
								//其它数据类型都当作字符串简单处理
								textValue = value.toString();
							}
						} else {
							if (value instanceof Date) {
								Date date = (Date) value;
								textValue = new SimpleDateFormat(dateFormat).format(date);
							} else {
								//其它数据类型都当作字符串简单处理
								textValue = value.toString();
							}
						}
						
						/*if(j==12&&f==0) {
							URL url;
							try {
							 url = new URL("http://106.14.64.21:8380//image/2017-11-02/15096376587221509636892049002.jpg");
					         HttpURLConnection connection = (HttpURLConnection)url.openConnection();
					         DataInputStream in = new DataInputStream(connection.getInputStream());
					      //   int length = in.available();     available 返回的字节数也不一定就是对方实际发送数据的长度，因为，如果数据长度过大的话，在实际的网络发送过程中，会对数据进行分段，分多次发送，而 available  只返回本次的可用字节数，这就是我开始讲的第二点“网络传输的不连续性 
					         int length=connection.getContentLength();  //获得网络文件的总大小
					         // create buffer
					         byte[] buf = new byte[length];
					         
					         // read the full data into the buffer
					         in.readFully(buf);
							
							// 有图片时，设置行高为60px;
							//row.setHeightInPoints(120);
							// 设置图片所在列宽度为80px,注意这里单位的一个换算
							//sheet.setColumnWidth(i, (short) (35.7 * 80));
							// sheet.autoSizeColumn(i);
							HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 250, 220, (short) 0, rowIndex-2, (short) 5, rowIndex+8);
							anchor.setAnchorType(2);
							patriarch.createPicture(anchor, workbook.addPicture(buf, HSSFWorkbook.PICTURE_TYPE_JPEG));
							textValue=null;
							in.close();
					        connection.disconnect();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}*/ //图片处理 关闭


						//如果不是图片数据，就利用正则表达式判断textValue是否全部由数字组成
						if(textValue != null){
							Pattern p = Pattern.compile("^//d+(//.//d+)?$");
							Matcher matcher = p.matcher(textValue);
							if(matcher.matches()){
								//是数字当作double处理
								cell.setCellValue(Double.parseDouble(textValue));
							}else{
								HSSFRichTextString richString = new HSSFRichTextString(textValue);
								/** 字体样式
								 HSSFFont cellFont = workbook.createFont();
								 cellFont.setColor(dataFontColor);
								 cellFont.setFontHeightInPoints(dataFontHeightInPoints);
								 cellFont.setBoldweight(dataFontBoldweight);
								 richString.applyFont(cellFont);
								 * */
								cell.setCellValue(richString);
							}
						}


					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						//清理资源
					}
				}

			}
			rowCount += rowIndex;
		}

		try {
			workbook.write(outps);
			outps.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rowCount;
	}


}
