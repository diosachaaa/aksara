package com.aksara.membership.data.repository

import com.aksara.membership.data.dao.MemberDao
import com.aksara.membership.data.dao.RewardDao
import com.aksara.membership.data.dao.TransactionDao
import com.aksara.membership.data.entity.Member
import com.aksara.membership.data.entity.Reward
import com.aksara.membership.data.entity.Transaction
import com.aksara.membership.util.PointCalculator
import kotlinx.coroutines.flow.Flow

/**
 * Satu-satunya sumber kebenaran (single source of truth) untuk data.
 * ViewModel hanya berbicara dengan repository ini, tidak langsung ke DAO.
 */
class AksaraRepository(
    private val memberDao: MemberDao,
    private val transactionDao: TransactionDao,
    private val rewardDao: RewardDao
) {

    // ---------- MEMBER ----------

    /** Registrasi member baru, otomatis membuat nomor member. Mengembalikan id member. */
    suspend fun register(name: String, email: String, phone: String): Long {
        val newId = memberDao.insert(
            Member(name = name, email = email, phone = phone)
        )
        // Format nomor member, contoh: MBR00123
        val memberNumber = "AKS%05d".format(newId)
        memberDao.getMemberById(newId)?.let {
            memberDao.update(it.copy(memberNumber = memberNumber))
        }
        return newId
    }

    suspend fun isEmailRegistered(email: String): Boolean =
        memberDao.countByEmail(email) > 0

    suspend fun login(email: String): Member? = memberDao.getMemberByEmail(email)

    fun observeMember(id: Long): Flow<Member?> = memberDao.observeMember(id)

    suspend fun updateMember(member: Member) = memberDao.update(member)

    // ---------- TRANSACTION ----------

    /**
     * Menambah transaksi sekaligus menghitung & mengakumulasi poin.
     * Rp10.000 = 1 poin (lihat [PointCalculator]).
     */
    suspend fun addTransaction(
        memberId: Long,
        bookTitle: String,
        category: String,
        amount: Long
    ): Int {
        val points = PointCalculator.calculate(amount)
        transactionDao.insert(
            Transaction(
                memberId = memberId,
                bookTitle = bookTitle,
                category = category,
                amount = amount,
                pointsEarned = points
            )
        )
        memberDao.getMemberById(memberId)?.let { member ->
            memberDao.update(member.copy(totalPoints = member.totalPoints + points))
        }
        return points
    }

    fun observeTransactions(memberId: Long): Flow<List<Transaction>> =
        transactionDao.observeTransactions(memberId)

    // ---------- REWARD ----------

    fun observeRewards(): Flow<List<Reward>> = rewardDao.observeRewards()

    /**
     * Menukar poin dengan reward.
     * @return true jika berhasil (poin mencukupi), false jika poin kurang.
     */
    suspend fun redeem(memberId: Long, reward: Reward): Boolean {
        val member = memberDao.getMemberById(memberId) ?: return false
        if (member.totalPoints < reward.pointCost) return false
        memberDao.update(member.copy(totalPoints = member.totalPoints - reward.pointCost))
        return true
    }
}
