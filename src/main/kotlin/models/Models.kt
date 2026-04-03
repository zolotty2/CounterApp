package models

data class VendingMachine(
    val serialCode: String,
    val inventoryCode: String,
    val location: String,
    val brand: String,
    val model: String,
    val status: String,
    val type: String,
    val totalEarnings: Double,
    val installationDate: String,
    val dateOfCreation: String? = null,
    val nextMaintenanceDate: String? = null,
    val timeInUse: Int = 0
)

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val minAmount: Double,
    val totalStock: Double
)

data class Transaction(
    val id: Int,
    val productName: String,
    val location: String,
    val method: String,
    val amount: Double,
    val date: String
)

data class User(
    val id: Int,
    val fullName: String,
    val email: String,
    val role: String
)