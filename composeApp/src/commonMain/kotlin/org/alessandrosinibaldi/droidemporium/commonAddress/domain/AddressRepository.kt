package org.alessandrosinibaldi.droidemporium.commonAddress.domain

import org.alessandrosinibaldi.droidemporium.core.domain.Result

interface AddressRepository {

    suspend fun addAddress(clientId: String, address: Address): Result<Unit>
    suspend fun getAddressesForClient(clientId: String): Result<List<Address>>

    suspend fun updateAddress(clientId: String, address: Address): Result<Unit>



    suspend fun deleteAddress(clientId: String, addressId: String): Result<Unit>

    suspend fun setDefaultAddress(clientId: String, addressId: String): Result<Unit>

}