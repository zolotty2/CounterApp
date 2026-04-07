package database

import java.sql.Connection
import java.sql.DriverManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import models.*

class Database {
    private val url = "jdbc:postgresql://localhost:5432/vending_db"
    private val user = "postgres"
    private val password = "1111"

    private fun getConnection(): Connection {
        Class.forName("org.postgresql.Driver")
        return DriverManager.getConnection(url, user, password)
    }

    suspend fun authenticateUser(email: String, password: String): User? = withContext(Dispatchers.IO) {
        val connection = getConnection()
        try {
            val stmt = connection.prepareStatement(
                "SELECT id, full_name, email, role FROM users WHERE email = ? AND password = ?"
            )
            stmt.setString(1, email)
            stmt.setString(2, password)
            val rs = stmt.executeQuery()

            if (rs.next()) {
                return@withContext User(
                    id = rs.getInt("id"),
                    fullName = rs.getString("full_name"),
                    email = rs.getString("email"),
                    role = rs.getString("role")
                )
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            connection.close()
        }
    }

    suspend fun getVendingMachines(): List<VendingMachine> = withContext(Dispatchers.IO) {
        val connection = getConnection()
        try {
            val stmt = connection.createStatement()
            val rs = stmt.executeQuery("""
                SELECT vm.serial_code, vm.inventory_code, vm.intall_location,
                       b.brand_name, m.model_name, s.status_name, vt.vend_type,
                       vm.total_earnings, TO_CHAR(vm.date_of_installation, 'DD.MM.YYYY') as install_date,
                       TO_CHAR(vm.date_of_creation, 'DD.MM.YYYY') as creation_date,
                       vm.time_in_use
                FROM vending_machines vm
                JOIN models m ON vm.id_model = m.id
                JOIN brands b ON m.id_brand = b.id
                JOIN statuses s ON vm.id_status = s.id
                JOIN vending_types vt ON vm.id_type = vt.id
                LIMIT 100
            """)

            val machines = mutableListOf<VendingMachine>()
            while (rs.next()) {
                machines.add(VendingMachine(
                    serialCode = rs.getString("serial_code"),
                    inventoryCode = rs.getString("inventory_code"),
                    location = rs.getString("intall_location"),
                    brand = rs.getString("brand_name"),
                    model = rs.getString("model_name"),
                    status = rs.getString("status_name"),
                    type = rs.getString("vend_type"),
                    totalEarnings = rs.getDouble("total_earnings"),
                    installationDate = rs.getString("install_date"),
                    dateOfCreation = rs.getString("creation_date"),
                    timeInUse = rs.getInt("time_in_use")
                ))
            }
            machines
        } finally {
            connection.close()
        }
    }

    suspend fun getProducts(): List<Product> = withContext(Dispatchers.IO) {
        val connection = getConnection()
        try {
            val stmt = connection.createStatement()
            val rs = stmt.executeQuery("""
                SELECT id, product_name, description, price, min_amount, 0 as total_stock
                FROM products LIMIT 100
            """)

            val products = mutableListOf<Product>()
            while (rs.next()) {
                products.add(Product(
                    id = rs.getInt("id"),
                    name = rs.getString("product_name"),
                    description = rs.getString("description"),
                    price = rs.getDouble("price"),
                    minAmount = rs.getDouble("min_amount"),
                    totalStock = rs.getDouble("total_stock")
                ))
            }
            products
        } finally {
            connection.close()
        }
    }

    suspend fun getTransactions(): List<Transaction> = withContext(Dispatchers.IO) {
        val connection = getConnection()
        try {
            val stmt = connection.createStatement()
            val rs = stmt.executeQuery("""
                SELECT t.id, p.product_name, vm.intall_location, tm.method_name, t.amount,
                       TO_CHAR(t.date_of_transaction, 'DD.MM.YYYY HH24:MI:SS') as trans_date
                FROM transactions t
                JOIN vending_machine_products vmp ON t.id_vend_product = vmp.id
                JOIN products p ON vmp.id_product = p.id
                JOIN vending_machines vm ON vmp.id_vending_machine = vm.serial_code
                JOIN trans_methods tm ON t.id_trans_method = tm.id
                LIMIT 100
            """)

            val transactions = mutableListOf<Transaction>()
            while (rs.next()) {
                transactions.add(Transaction(
                    id = rs.getInt("id"),
                    productName = rs.getString("product_name"),
                    location = rs.getString("intall_location"),
                    method = rs.getString("method_name"),
                    amount = rs.getDouble("amount"),
                    date = rs.getString("trans_date")
                ))
            }
            transactions
        } finally {
            connection.close()
        }
    }

    suspend fun getMonitorVendingMachines(): List<VendingMachineMonitorItem> = withContext(Dispatchers.IO) {
        val connection = getConnection()
        try {
            val stmt = connection.prepareStatement("""
                SELECT 
                    vm.serial_code,
                    vm.intall_location,
                    b.brand_name,
                    m.model_name,
                    s.status_name,
                    vt.vend_type,
                    vm.total_earnings,
                    COALESCE(MAX(t.date_of_transaction), vm.date_of_installation) as last_transaction,
                    COALESCE(MAX(mt.date_of_maintanance), vm.date_of_installation) as last_maintenance,
                    (SELECT COUNT(*) FROM vending_machine_products vmp WHERE vmp.id_vending_machine = vm.serial_code) as total_slots,
                    (SELECT SUM(amount_in_stock) FROM vending_machine_products vmp WHERE vmp.id_vending_machine = vm.serial_code) as total_stock
                FROM vending_machines vm
                JOIN models m ON vm.id_model = m.id
                JOIN brands b ON m.id_brand = b.id
                JOIN statuses s ON vm.id_status = s.id
                JOIN vending_types vt ON vm.id_type = vt.id
                LEFT JOIN transactions t ON t.id_vend_product IN (
                    SELECT id FROM vending_machine_products WHERE id_vending_machine = vm.serial_code
                )
                LEFT JOIN maintanances mt ON mt.id_vend_machine = vm.serial_code
                GROUP BY vm.serial_code, b.brand_name, m.model_name, s.status_name, vt.vend_type, vm.intall_location, vm.total_earnings, vm.date_of_installation
                ORDER BY vm.serial_code
            """)
            val rs = stmt.executeQuery()
            val items = mutableListOf<VendingMachineMonitorItem>()
            var index = 1
            while (rs.next()) {
                val serial = rs.getString("serial_code")
                val location = rs.getString("intall_location")
                val brand = rs.getString("brand_name")
                val model = rs.getString("model_name")
                val status = rs.getString("status_name")
                val type = rs.getString("vend_type")
                val earnings = rs.getDouble("total_earnings")
                val lastTrans = rs.getTimestamp("last_transaction")
                val lastMaint = rs.getDate("last_maintenance")
                val totalSlots = rs.getInt("total_slots")
                val totalStock = rs.getDouble("total_stock")

                val connectionStatus = if (lastTrans != null && System.currentTimeMillis() - lastTrans.time < 30*60*1000) "Online" else "Offline"
                val loadPercent = if (totalSlots > 0) (totalStock / totalSlots * 100).toInt() else 0
                val loadInfo = when {
                    loadPercent > 80 -> "высокая ($loadPercent%)"
                    loadPercent > 30 -> "средняя ($loadPercent%)"
                    else -> "низкая ($loadPercent%)"
                }
                val cash = String.format("%.0f", earnings) + " р."
                val lastEventTime = when {
                    lastTrans != null -> getRelativeTime(lastTrans.time)
                    else -> "нет событий"
                }
                val equipmentInfo = if (lastMaint != null) getRelativeTime(lastMaint.time) else "не обслуживался"
                val additionalInfo = "$totalStock / $totalSlots"

                items.add(VendingMachineMonitorItem(
                    id = index++,
                    serialCode = serial,
                    location = location,
                    brandModel = "$brand $model ($type)",
                    connectionStatus = connectionStatus,
                    loadInfo = loadInfo,
                    cashAmount = cash,
                    lastEvent = lastEventTime,
                    equipmentInfo = equipmentInfo,
                    additionalInfo = additionalInfo,
                    detailRows = listOf("${brand} ${model}", "последняя транзакция: ${lastTrans?.toString() ?: "нет"}")
                ))
            }
            items
        } finally {
            connection.close()
        }
    }

    private fun getRelativeTime(timestamp: Long): String {
        val diff = (System.currentTimeMillis() - timestamp) / 1000
        return when {
            diff < 60 -> "только что"
            diff < 3600 -> "${diff / 60} мин. назад"
            diff < 86400 -> "${diff / 3600} ч. назад"
            else -> "${diff / 86400} дн. назад"
        }
    }
}