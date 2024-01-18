import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.awt.FileDialog
import java.awt.Frame
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@Composable
@Preview
fun App() {
    var xmlPath = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val transLateDataList = mutableListOf<TransLateData>()

    MaterialTheme {
        Column {
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    FilePicker("选择 res 目录", scope, xmlPath)
                    Text("选中的目录: ${xmlPath.value}", modifier = Modifier.padding(start = 12.dp))
                }
            }

            Button(onClick = {
                val path = "C:\\Users\\chenlijin\\my\\SystemSetting\\app\\src\\main\\res"
                Language.entries.forEach {
                    extractedTranslate(xmlPath.value, transLateDataList, it)
                }
                val csvData = mutableListOf(
                    listOf("stringId", "中文", "英文", "西班牙语", "印度", "泰国", "越南"),
                )
                csvData.addAll(transLateDataList.map { data ->
                    listOf(
                        data.stringId,
                        data.zhValue,
                        data.enValue,
                        data.esValue,
                        data.inValue,
                        data.thValue,
                        data.viValue
                    )
                })

                csvWriter().writeAll(csvData, "translateData.csv")
            }, modifier = Modifier.padding(16.dp)) {
                Text("开始解析")
            }
        }
    }
}

private fun extractedTranslate(basePath: String, transLateDataList: MutableList<TransLateData>, language: Language) {
    val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("$basePath${language.path}")
    val root = document.documentElement
    val children = root.childNodes
    for (i in 0 until children.length) {
        if (children.item(i).childNodes.length > 0) {
            val node = children.item(i).childNodes.item(0)
            val key = children.item(i).attributes.getNamedItem("name").textContent
            val value = node.textContent
            if (transLateDataList.any { it.stringId == key }) {
                val entity = transLateDataList.first { it.stringId == key }
                when (language) {
                    Language.ZH -> entity.zhValue = value
                    Language.EN -> entity.enValue = value
                    Language.ES -> entity.esValue = value
                    Language.IN -> entity.inValue = value
                    Language.TH -> entity.thValue = value
                    Language.VI -> entity.viValue = value
                }
            } else {
                when (language) {
                    Language.ZH -> transLateDataList.add(TransLateData(key, zhValue = value))
                    Language.EN -> transLateDataList.add(TransLateData(key, enValue = value))
                    Language.ES -> transLateDataList.add(TransLateData(key, esValue = value))
                    Language.IN -> transLateDataList.add(TransLateData(key, inValue = value))
                    Language.TH -> transLateDataList.add(TransLateData(key, thValue = value))
                    Language.VI -> transLateDataList.add(TransLateData(key, viValue = value))
                }

            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}

@Composable
@Preview
fun FilePicker(text: String, scope: CoroutineScope, meiTuanFilePath: MutableState<String>) {
    var fileDialog: FileDialog? = null
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            scope.launch {
                fileDialog = FileDialog(Frame(), text, FileDialog.LOAD)
                fileDialog?.isVisible = true
                fileDialog?.let {
                    meiTuanFilePath.value = File(it.directory).parent
                }
            }
        }) {
            Text("选择文件")
        }
    }
}
