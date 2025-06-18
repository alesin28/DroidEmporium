package org.alessandrosinibaldi.droidemporium.commonAddress.data

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.CollectionReference
import dev.gitlive.firebase.firestore.firestore
import org.alessandrosinibaldi.droidemporium.commonAddress.data.dto.AddressDto
import org.alessandrosinibaldi.droidemporium.commonAddress.data.dto.toDomain
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.AddressRepository
import org.alessandrosinibaldi.droidemporium.core.domain.Result
import org.alessandrosinibaldi.droidemporium.commonAddress.domain.Address


class FirestoreAddressRepository : AddressRepository {

    private val firestore = Firebase.firestore

    private fun getAddressesCollection(clientId: String): CollectionReference {
        return firestore.collection("clients").document(clientId).collection("addresses")
    }

    private suspend fun unsetCurrentDefault(clientId: String, newDefaultId: String? = null) {
        val addresses = getAddressesCollection(clientId)
        val currentDefaultDoc = addresses
            .where { "isDefault" equalTo true }
            .limit(1)
            .get()
            .documents
            .firstOrNull()

        if (currentDefaultDoc != null && currentDefaultDoc.id != newDefaultId) {
            addresses.document(currentDefaultDoc.id).update("isDefault" to false)
        }
    }

    override suspend fun addAddress(clientId: String, address: Address): Result<Unit> {
        return try {
            if (address.isDefault) {
                unsetCurrentDefault(clientId)
            }

            val newAddressDto = AddressDto(
                label = address.label, street = address.street, city = address.city,
                state = address.state, postalCode = address.postalCode, country = address.country,
                isDefault = address.isDefault
            )
            getAddressesCollection(clientId).add(newAddressDto)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun getAddressesForClient(clientId: String): Result<List<Address>> {
        return try {
            val querySnapshot = getAddressesCollection(clientId).get()
            val addresses = querySnapshot.documents.map { doc ->
                doc.data<AddressDto>().toDomain(id = doc.id)
            }
            Result.Success(addresses)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun updateAddress(clientId: String, address: Address): Result<Unit> {
        return try {
            if (address.isDefault) {
                unsetCurrentDefault(clientId, address.id)
            }

            val addressDto = AddressDto(
                label = address.label, street = address.street, city = address.city,
                state = address.state, postalCode = address.postalCode, country = address.country,
                isDefault = address.isDefault
            )
            getAddressesCollection(clientId).document(address.id).set(addressDto)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun deleteAddress(clientId: String, addressId: String): Result<Unit> {
        return try {
            getAddressesCollection(clientId).document(addressId).delete()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    override suspend fun setDefaultAddress(clientId: String, addressId: String): Result<Unit> {
        return try {
            unsetCurrentDefault(clientId, addressId)

            getAddressesCollection(clientId).document(addressId).update("isDefault" to true)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}