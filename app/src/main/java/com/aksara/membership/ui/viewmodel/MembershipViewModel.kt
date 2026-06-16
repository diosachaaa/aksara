package com.aksara.membership.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aksara.membership.data.entity.Member
import com.aksara.membership.data.entity.Reward
import com.aksara.membership.data.entity.Transaction
import com.aksara.membership.data.repository.AksaraRepository
import com.aksara.membership.util.PointCalculator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Hasil sebuah aksi (registrasi/login/redeem) untuk ditampilkan ke UI. */
sealed interface UiEvent {
    data class Error(val message: String) : UiEvent
    data class Success(val message: String) : UiEvent
}

@OptIn(ExperimentalCoroutinesApi::class)
class MembershipViewModel(
    private val repository: AksaraRepository
) : ViewModel() {

    // ----- SESSION -----
    private val _currentMemberId = MutableStateFlow<Long?>(null)
    val currentMemberId: StateFlow<Long?> = _currentMemberId.asStateFlow()

    val isLoggedIn: Boolean get() = _currentMemberId.value != null

    // ----- DATA TURUNAN DARI SESSION -----
    val member: StateFlow<Member?> = _currentMemberId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.observeMember(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<Transaction>> = _currentMemberId
        .flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else repository.observeTransactions(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rewards: StateFlow<List<Reward>> = repository.observeRewards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ----- EVENT KE UI (snackbar/toast) -----
    private val _event = MutableStateFlow<UiEvent?>(null)
    val event: StateFlow<UiEvent?> = _event.asStateFlow()
    fun consumeEvent() { _event.value = null }

    // ----- AKSI -----
    fun register(
        name: String,
        email: String,
        phone: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            if (repository.isEmailRegistered(email)) {
                _event.value = UiEvent.Error("Email sudah terdaftar. Silakan login.")
                return@launch
            }
            val id = repository.register(name.trim(), email.trim(), phone.trim())
            _currentMemberId.value = id
            _event.value = UiEvent.Success("Registrasi berhasil! Selamat datang, ${name.trim()}.")
            onSuccess()
        }
    }

    fun login(email: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val found = repository.login(email.trim())
            if (found == null) {
                _event.value = UiEvent.Error("Email tidak ditemukan. Silakan daftar dulu.")
            } else {
                _currentMemberId.value = found.id
                onSuccess()
            }
        }
    }

    /** Pratinjau poin tanpa menyimpan (untuk layar Add Transaction). */
    fun previewPoints(amount: Long): Int = PointCalculator.calculate(amount)

    fun addTransaction(
        bookTitle: String,
        category: String,
        amount: Long,
        onDone: (Int) -> Unit
    ) {
        val id = _currentMemberId.value ?: return
        viewModelScope.launch {
            val points = repository.addTransaction(id, bookTitle.trim(), category, amount)
            onDone(points)
        }
    }

    fun redeem(reward: Reward, onResult: (Boolean) -> Unit) {
        val id = _currentMemberId.value ?: return
        viewModelScope.launch {
            val success = repository.redeem(id, reward)
            if (!success) _event.value = UiEvent.Error("Poin Anda belum mencukupi.")
            onResult(success)
        }
    }

    fun updateProfile(name: String, email: String, phone: String) {
        val current = member.value ?: return
        viewModelScope.launch {
            repository.updateMember(
                current.copy(name = name.trim(), email = email.trim(), phone = phone.trim())
            )
            _event.value = UiEvent.Success("Profil berhasil diperbarui.")
        }
    }

    fun logout() {
        _currentMemberId.value = null
    }

    // ----- FACTORY -----
    class Factory(private val repository: AksaraRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MembershipViewModel::class.java)) {
                return MembershipViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
