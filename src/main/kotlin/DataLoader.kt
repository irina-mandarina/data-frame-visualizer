import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.File
import java.sql.Connection
import java.sql.DriverManager

class DataLoader {
    fun loadCSV(file: File): DataFrameData {
        val parser = CSVParser.parse(file, Charsets.UTF_8, CSVFormat.DEFAULT.withHeader())
        val headers = parser.headerMap.keys.toTypedArray()
        val rows = parser.map { record -> headers.map { record[it] as Any }.toTypedArray() }
        return DataFrameData(headers, rows)
    }

    fun loadJSON(file: File): DataFrameData {
        val objectMapper = jacksonObjectMapper()
        val data: List<Map<String, Any>> = objectMapper.readValue(file)
        val headers = data.firstOrNull()?.keys?.toTypedArray() ?: arrayOf()
        val rows = data.map { row -> headers.map { row[it] ?: "" }.toTypedArray() }
        return DataFrameData(headers, rows)
    }

    fun loadExcel(file: File): DataFrameData {
        val workbook = WorkbookFactory.create(file)
        val sheet = workbook.getSheetAt(0)
        val headers = sheet.getRow(0).map { it.toString() }.toTypedArray()
        val rows = (1..sheet.lastRowNum).map { rowNum ->
            sheet.getRow(rowNum).map { it as Any }.toTypedArray()
        }
        return DataFrameData(headers, rows)
    }

    fun loadDatabase(file: File): DataFrameData {
        val connection: Connection = DriverManager.getConnection("jdbc:sqlite:${file.absolutePath}")
        connection.use { conn ->
            val statement = conn.createStatement()
            val resultSet = statement.executeQuery("SELECT * FROM table_name")
            val metaData = resultSet.metaData
            val headers = Array(metaData.columnCount) { metaData.getColumnName(it + 1) }
            val rows = mutableListOf<Array<Any>>()

            while (resultSet.next()) {
                rows.add(Array(headers.size) { resultSet.getObject(it + 1) })
            }
            return DataFrameData(headers, rows)
        }
    }
}