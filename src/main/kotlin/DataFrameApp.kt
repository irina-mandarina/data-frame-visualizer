import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.table.DefaultTableModel
import javax.swing.table.TableRowSorter

data class DataFrameData(
    val columns: Array<String>,
    val rows: List<Array<Any>>
)



class DataFrameApp : JFrame("DataFrame Viewer") {
    private val tableModel = DefaultTableModel()
    private val table = JTable(tableModel)
    private val rowSorter = TableRowSorter(tableModel)
    private val loader = DataLoader()

    init {
        defaultCloseOperation = EXIT_ON_CLOSE
        size = Dimension(1000, 800)
        setupUI()
        applyStyles()
    }

    private fun setupUI() {
        layout = BorderLayout(10, 10)
        table.rowSorter = rowSorter
        table.autoResizeMode = JTable.AUTO_RESIZE_OFF

        val scrollPane = JScrollPane(table)
        add(createControlPanel(), BorderLayout.NORTH)
        add(scrollPane, BorderLayout.CENTER)
    }

    private fun createControlPanel(): JPanel {
        return JPanel(FlowLayout(FlowLayout.LEFT, 10, 5)).apply {
            border = EmptyBorder(5, 10, 5, 10)
            background = Color(240, 240, 240)

            add(JButton("Load File").apply {
                addActionListener { loadData() }
            })

            // controls for searching by value
            add(JLabel("Search:"))
            add(JTextField(20).apply {
                addKeyListener(object : java.awt.event.KeyAdapter() {
                    override fun keyReleased(e: java.awt.event.KeyEvent?) {
                        filterTable(text)
                    }
                })
            })

            // controls for sorting by column on each column header
            table.tableHeader.addMouseListener(object : java.awt.event.MouseAdapter() {
                override fun mouseClicked(e: java.awt.event.MouseEvent) {
                    val column = table.columnModel.getColumnIndexAtX(e.x)
                    // toggle sorting direction or remove sorting
                    val sortKeys = rowSorter.sortKeys.toMutableList()
                    val existingKey = sortKeys.find { it.column == column }
                    if (existingKey != null) {
                        sortKeys.remove(existingKey)
                        when (existingKey.sortOrder) {
                            SortOrder.ASCENDING -> sortKeys.add(RowSorter.SortKey(column, SortOrder.DESCENDING))
                            SortOrder.DESCENDING -> { /* Do nothing, key is already removed */ }
                            else -> sortKeys.add(RowSorter.SortKey(column, SortOrder.ASCENDING))
                        }
                    } else {
                        sortKeys.add(RowSorter.SortKey(column, SortOrder.ASCENDING))
                    }
                    rowSorter.sortKeys = sortKeys
                }
            })

        }
    }

    private fun applyStyles() {
        table.apply {
            gridColor = Color(220, 220, 220)
            setShowGrid(true)
            tableHeader.background = Color(230, 230, 230)
            tableHeader.foreground = Color(60, 60, 60)
            font = Font("Arial", Font.PLAIN, 12)
            rowHeight = 25
        }
    }

    private fun filterTable(text: String) {
        rowSorter.rowFilter = if (text.isEmpty()) {
            null
        } else {
            RowFilter.regexFilter("(?i)$text")
        }
    }

    // multiple files can be chosen. if the headers do not match, visualizing the first file only.
    private fun loadData() {
        val fileChooser = JFileChooser().apply {
            isMultiSelectionEnabled = true
            fileFilter = FileNameExtensionFilter(
                "Supported Files", "csv", "json", "xlsx", "db"
            )
        }

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                val files = fileChooser.selectedFiles
                if (files.isEmpty()) return

                val dataFrames = files.map { file ->
                    when (file.extension.lowercase()) {
                        "csv" -> loader.loadCSV(file)
                        "json" -> loader.loadJSON(file)
                        "xlsx" -> loader.loadExcel(file)
                        "db" -> loader.loadDatabase(file)
                        else -> throw IllegalArgumentException("Unsupported file format")
                    }
                }

                val headers = dataFrames.first().columns
                if (dataFrames.all { it.columns.contentEquals(headers) }) {
                    val combinedRows = dataFrames.flatMap { it.rows }
                    updateTable(DataFrameData(headers, combinedRows))
                } else {
                    showError("Headers do not match across files. Displaying the first file only.")
                    updateTable(dataFrames.first())
                }
            } catch (e: Exception) {
                showError("Error loading files: ${e.message}")
                println(e)
            }
        }
    }

    private fun updateTable(data: DataFrameData) {
        tableModel.setDataVector(data.rows.toTypedArray(), data.columns)
        table.columnModel.columns.asSequence().forEach { column ->
            column.preferredWidth = 150
        }
    }

    private fun showError(message: String) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE)
    }
}