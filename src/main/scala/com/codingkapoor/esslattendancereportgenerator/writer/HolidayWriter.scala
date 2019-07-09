package com.codingkapoor.esslattendancereportgenerator.writer

import java.time.YearMonth

import com.codingkapoor.esslattendancereportgenerator.model.{AttendancePerEmployee, Holiday}
import org.apache.poi.ss.usermodel.{BorderStyle, FillPatternType, HorizontalAlignment, VerticalAlignment}
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.{XSSFColor, XSSFWorkbook}
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder

trait HolidayWriter {
  val month: Int
  val year: Int

  def mergedRegionAlreadyExists(firstRowIndex: Int, lastRowIndex: Int, firstColumnIndex: Int, lastColumnIndex: Int)(implicit workbook: XSSFWorkbook): Boolean

  def writeHolidays(implicit workbook: XSSFWorkbook, attendances: Seq[AttendancePerEmployee], holidays: Seq[Holiday]): Unit = {
    val yearMonth = YearMonth.of(year, month)
    val _month = yearMonth.getMonth.toString

    val sheet = workbook.getSheet(_month)

    val font = workbook.createFont
    font.setFontHeightInPoints(10.toShort)

    val cellStyle = workbook.createCellStyle
    cellStyle.setFont(font)
    cellStyle.setAlignment(HorizontalAlignment.CENTER)
    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER)
    cellStyle.setRotation(90.toShort)
    cellStyle.setShrinkToFit(true)
    cellStyle.setBorderLeft(BorderStyle.THIN)
    cellStyle.setBorderColor(XSSFCellBorder.BorderSide.LEFT, new XSSFColor(java.awt.Color.GRAY))
    cellStyle.setBorderTop(BorderStyle.THIN)
    cellStyle.setBorderColor(XSSFCellBorder.BorderSide.TOP, new XSSFColor(java.awt.Color.GRAY))
    cellStyle.setBorderRight(BorderStyle.THIN)
    cellStyle.setBorderColor(XSSFCellBorder.BorderSide.RIGHT, new XSSFColor(java.awt.Color.GRAY))
    cellStyle.setBorderBottom(BorderStyle.THIN)
    cellStyle.setBorderColor(XSSFCellBorder.BorderSide.BOTTOM, new XSSFColor(java.awt.Color.GRAY))
    cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(238, 204, 251)))
    cellStyle.setFillPattern(FillPatternType.DIAMONDS)

    for (holiday <- holidays) {
      val day = holiday.date.getDayOfMonth
      val dayIndex = 5 + day - 1

      val firstRowIndex = 3
      val lastRowIndex = firstRowIndex + attendances.size - 1
      val firstColIndex = dayIndex
      val lastColIndex = dayIndex

      if (!mergedRegionAlreadyExists(firstRowIndex, lastRowIndex, firstColIndex, lastColIndex)) {

        for (i <- firstRowIndex to lastRowIndex) {
          val row = sheet.getRow(i)
          for (j <- firstColIndex to lastColIndex) {
            val col = row.createCell(j)
            col.setCellStyle(cellStyle)
            if (i == firstRowIndex && j == firstColIndex) {
              col.setCellValue(holiday.occasion)
            }
          }
        }

        sheet.addMergedRegion(new CellRangeAddress(firstRowIndex, lastRowIndex, firstColIndex, lastColIndex))
      }
    }
  }
}