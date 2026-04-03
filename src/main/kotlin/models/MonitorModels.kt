package models

data class VendingMachineMonitorItem(
    val id: Int, // порядковый номер для отображения
    val serialCode: String,
    val location: String,
    val brandModel: String, // "Бренд Модель (доп.инфо)"
    val connectionStatus: String, // "Online"/"Offline"/"T2" и т.п.
    val loadInfo: String, // например "280.67 р." или "общая"
    val cashAmount: String, // "1642 р. (302 монет)"
    val lastEvent: String, // "11 мин. назад"
    val equipmentInfo: String, // "2 дня назад"
    val additionalInfo: String, // "112 / 247"
    val detailRows: List<String>? = null // дополнительные строки (модель, серийный номер)
)