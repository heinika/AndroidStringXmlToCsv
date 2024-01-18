# AndroidStringXmlToCsv
这是一个将 android xml 转化为 CSV 的程序。
生成结果如下：
![结果图](./picture/result.png)

关键代码：
```kotlin
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
```