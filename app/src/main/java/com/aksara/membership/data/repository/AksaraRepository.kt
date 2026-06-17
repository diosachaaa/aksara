package com.aksara.membership.data.repository

import com.aksara.membership.data.dao.MemberDao
import com.aksara.membership.data.dao.ProductDao
import com.aksara.membership.data.dao.RedemptionDao
import com.aksara.membership.data.dao.RewardDao
import com.aksara.membership.data.dao.TransactionDao
import com.aksara.membership.data.entity.Member
import com.aksara.membership.data.entity.Product
import com.aksara.membership.data.entity.Redemption
import com.aksara.membership.data.entity.Reward
import com.aksara.membership.data.entity.Transaction
import com.aksara.membership.util.CodeGenerator
import com.aksara.membership.util.PointCalculator
import kotlinx.coroutines.flow.Flow

class AksaraRepository(
    private val memberDao: MemberDao,
    private val transactionDao: TransactionDao,
    private val rewardDao: RewardDao,
    private val redemptionDao: RedemptionDao,
    private val productDao: ProductDao
) {

    // ---------- MEMBER ----------
    suspend fun register(name: String, email: String, phone: String): Long {
        val newId = memberDao.insert(Member(name = name, email = email, phone = phone))
        val memberNumber = "AKS%05d".format(newId)
        memberDao.getMemberById(newId)?.let { memberDao.update(it.copy(memberNumber = memberNumber)) }
        return newId
    }

    suspend fun isEmailRegistered(email: String): Boolean = memberDao.countByEmail(email) > 0
    suspend fun login(email: String): Member? = memberDao.getMemberByEmail(email)
    fun observeMember(id: Long): Flow<Member?> = memberDao.observeMember(id)
    suspend fun updateMember(member: Member) = memberDao.update(member)

    suspend fun updateProfile(memberId: Long, name: String, email: String, phone: String, photoPath: String?) {
        memberDao.getMemberById(memberId)?.let {
            memberDao.update(it.copy(name = name, email = email, phone = phone, photoPath = photoPath))
        }
    }

    // ---------- PRODUCT / KATALOG ----------
    fun observeProducts(): Flow<List<Product>> = productDao.observeAll()

    /**
     * Pastikan katalog terisi. Dipanggil saat aplikasi mulai. Jika tabel produk
     * kosong (mis. database lama dari versi sebelumnya, atau seeding callback gagal),
     * isi ulang dari [com.aksara.membership.data.CatalogSeed]. Aman dipanggil berkali-kali.
     */
    suspend fun ensureCatalogSeeded() {
        val current = productDao.count()
        android.util.Log.i("AksaraSeed", "ensureCatalogSeeded dipanggil, jumlah produk saat ini = $current")
        if (current == 0) {
            productDao.insertAll(com.aksara.membership.data.CatalogSeed.products)
            android.util.Log.i("AksaraSeed", "Katalog diisi: ${com.aksara.membership.data.CatalogSeed.products.size} produk dimasukkan")
        }
    }

    /**
     * Pastikan daftar reward terisi. Dipanggil saat aplikasi mulai. Jika tabel
     * rewards kosong (database lama / seeding callback gagal), isi ulang dari
     * [com.aksara.membership.data.RewardSeed]. Aman dipanggil berkali-kali.
     */
    suspend fun ensureRewardsSeeded() {
        val current = rewardDao.count()
        android.util.Log.i("AksaraSeed", "ensureRewardsSeeded dipanggil, jumlah reward saat ini = $current")
        if (current == 0) {
            rewardDao.insertAll(com.aksara.membership.data.RewardSeed.rewards)
            android.util.Log.i("AksaraSeed", "Reward diisi: ${com.aksara.membership.data.RewardSeed.rewards.size} reward dimasukkan")
        }
    }

    // ---------- TRANSACTION ----------
    fun observeTransactions(memberId: Long): Flow<List<Transaction>> =
        transactionDao.observeTransactions(memberId)

    /**
     * Checkout keranjang: dibuat satu transaksi per kategori, poin diakumulasi
     * dari total seluruh belanja (Rp10.000 = 1 poin). Mengembalikan total poin.
     */
    suspend fun checkout(memberId: Long, lines: List<Pair<Product, Int>>): Int {
        if (lines.isEmpty()) return 0
        var totalPoints = 0
        lines.groupBy { it.first.category }.forEach { (category, items) ->
            val subtotal = items.sumOf { it.first.price * it.second }
            val points = PointCalculator.calculate(subtotal)
            transactionDao.insert(
                Transaction(memberId = memberId, amount = subtotal, pointsEarned = points, category = category)
            )
            totalPoints += points
        }
        memberDao.getMemberById(memberId)?.let { member ->
            memberDao.update(member.copy(totalPoints = member.totalPoints + totalPoints))
        }
        return totalPoints
    }

    // ---------- REWARD ----------
    fun observeRewards(): Flow<List<Reward>> = rewardDao.observeRewards()

    suspend fun redeem(memberId: Long, reward: Reward): String? {
        val member = memberDao.getMemberById(memberId) ?: return null
        if (member.totalPoints < reward.pointCost) return null
        memberDao.update(member.copy(totalPoints = member.totalPoints - reward.pointCost))
        val code = CodeGenerator.voucherCode()
        redemptionDao.insert(
            Redemption(memberId = memberId, rewardName = reward.name, pointCost = reward.pointCost, voucherCode = code)
        )
        return code
    }

    fun observeRedemptions(memberId: Long): Flow<List<Redemption>> =
        redemptionDao.observeRedemptions(memberId)
}
