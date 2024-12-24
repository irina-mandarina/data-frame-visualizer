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

            add(JLabel("Search:"))
            add(JTextField(20).apply {
                addKeyListener(object : java.awt.event.KeyAdapter() {
                    override fun keyReleased(e: java.awt.event.KeyEvent?) {
                        filterTable(text)
                    }
                })
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

    private fun loadData() {
        val fileChooser = JFileChooser().apply {
            fileFilter = FileNameExtensionFilter(
                "Supported Files", "csv", "json", "xlsx", "db"
            )
        }

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                val file = fileChooser.selectedFile
                val data = when (file.extension.lowercase()) {
                    "csv" -> loader.loadCSV(file)
                    "json" -> loader.loadJSON(file)
                    "xlsx" -> loader.loadExcel(file)
                    "db" -> loader.loadDatabase(file)
                    else -> throw IllegalArgumentException("Unsupported file format")
                }
                updateTable(data)
            } catch (e: Exception) {
                showError("Error loading file: ${e.message}")
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