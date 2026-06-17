package com.aksara.membership.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.aksara.membership.data.entity.Member
import com.aksara.membership.data.entity.Product
import com.aksara.membership.data.entity.Redemption
import com.aksara.membership.data.entity.Reward
import com.aksara.membership.data.entity.Transaction
import com.aksara.membership.data.repository.AksaraRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    // ----- PREFERENSI -----
    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()
    fun toggleDarkMode() { _darkMode.value = !_darkMode.value }

    // ----- DATA -----
    val member: StateFlow<Member?> = _currentMemberId
        .flatMapLatest { id -> if (id == null) flowOf(null) else repository.observeMember(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<Transaction>> = _currentMemberId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.observeTransactions(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rewards: StateFlow<List<Reward>> = repository.observeRewards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val redemptions: StateFlow<List<Redemption>> = _currentMemberId
        .flatMapLatest { id -> if (id == null) flowOf(emptyList()) else repository.observeRedemptions(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val products: StateFlow<List<Product>> = repository.observeProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ----- KERANJANG (in-memory) -----
    private val _cart = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val cart: StateFlow<Map<Long, Int>> = _cart.asStateFlow()

    fun addToCart(productId: Long) {
        _cart.value = _cart.value.toMutableMap().apply { this[productId] = (this[productId] ?: 0) + 1 }
    }

    fun decrementCart(productId: Long) {
        val current = _cart.value[productId] ?: 0
        _cart.value = _cart.value.toMutableMap().apply {
            if (current <= 1) remove(productId) else this[productId] = current - 1
        }
    }

    fun removeFromCart(productId: Long) {
        _cart.value = _cart.value.toMutableMap().apply { remove(productId) }
    }

    fun clearCart() { _cart.value = emptyMap() }

    fun cartLines(): List<Pair<Product, Int>> {
        val map = _cart.value
        return products.value.filter { map.containsKey(it.id) }.map { it to (map[it.id] ?: 0) }
    }

    fun cartTotal(): Long = cartLines().sumOf { it.first.price * it.second }
    fun cartCount(): Int = _cart.value.values.sum()

    fun checkout(onDone: (Int) -> Unit) {
        val id = _currentMemberId.value ?: return
        val lines = cartLines()
        if (lines.isEmpty()) return
        viewModelScope.launch {
            val points = repository.checkout(id, lines)
            clearCart()
            _event.value = UiEvent.Success("Pembayaran berhasil, +$points poin!")
            onDone(points)
        }
    }

    // ----- EVENT -----
    private val _event = MutableStateFlow<UiEvent?>(null)
    val event: StateFlow<UiEvent?> = _event.asStateFlow()
    fun consumeEvent() { _event.value = null }

    // ----- AKSI -----
    fun register(name: String, email: String, phone: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            if (repository.isEmailRegistered(email)) {
                _event.value = UiEvent.Error("Email sudah terdaftar. Silakan login.")
                return@launch
            }
            val id = repository.register(name.trim(), email.trim(), phone.trim())
            _currentMemberId.value = id
            onSuccess()
        }
    }

    fun login(email: String, onSuccess: () -> Unit, onNotFound: () -> Unit = {}) {
        viewModelScope.launch {
            val found = repository.login(email.trim())
            if (found == null) {
                _event.value = UiEvent.Error("Login gagal: email belum terdaftar.")
                onNotFound()
            } else {
                _currentMemberId.value = found.id
                onSuccess()
            }
        }
    }

    fun updateProfile(name: String, email: String, phone: String, photoPath: String?) {
        val id = _currentMemberId.value ?: return
        viewModelScope.launch {
            repository.updateProfile(id, name.trim(), email.trim(), phone.trim(), photoPath)
            _event.value = UiEvent.Success("Profil berhasil diperbarui.")
        }
    }

    fun redeem(reward: Reward, onResult: (String?) -> Unit) {
        val id = _currentMemberId.value ?: return
        viewModelScope.launch {
            val code = repository.redeem(id, reward)
            if (code == null) _event.value = UiEvent.Error("Poin Anda belum mencukupi.")
            onResult(code)
        }
    }

    fun logout() {
        _currentMemberId.value = null
        clearCart()
    }

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
