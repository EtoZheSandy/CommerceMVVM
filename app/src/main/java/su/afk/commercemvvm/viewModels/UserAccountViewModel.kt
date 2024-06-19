package su.afk.commercemvvm.viewModels

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import su.afk.commercemvvm.data.models.User
import su.afk.commercemvvm.di.App
import su.afk.commercemvvm.util.RegisterValidation
import su.afk.commercemvvm.util.Resource
import su.afk.commercemvvm.util.validateEmail
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val app: Application // можно и через di создать Application
): AndroidViewModel(app) {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Start())
    val user = _user.asStateFlow()


    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Start())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch { _user.emit(Resource.Loading() ) }

        firestore.collection("user").document(auth.uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)

                user?.let {
                    viewModelScope.launch { _user.emit(Resource.Success(it)) }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch { _user.emit(Resource.Error(it.message.toString())) }
            }
    }

    fun updateUser(user: User, imageUri: Uri?) {

        val areInputValid = validateEmail(email = user.email) is RegisterValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastName.trim().isNotEmpty()

        if(!areInputValid) {
            viewModelScope.launch { _user.emit(Resource.Error("Некоректный ввод")) }
        }

        viewModelScope.launch { _updateInfo.emit(Resource.Loading() ) }

        if (imageUri == null) {
            saveUserInfo(user, true)
        } else {
            saveUserInfoNewImage(user, imageUri)
        }
    }

    private fun saveUserInfoNewImage(user: User, imageUri: Uri) {
        viewModelScope.launch {
            try {
                // получаем массив байтов из растрового изображения
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    getApplication<App>().contentResolver,
                    imageUri
                )
                // сжимаем изображение
                val byteArrayOutputSteam = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputSteam)
                val imageByteArray = byteArrayOutputSteam.toByteArray()

                // путь до изображения
                val imageDirectory = storage.reference
                    .child("profileImages/${auth.uid}/${UUID.randomUUID()}")
                // загружаем его в байтах
                val result = imageDirectory.putBytes(imageByteArray).await()
                // получаем url нашего пути до картинки
                val imageUrl = result.storage.downloadUrl.await().toString()

                // обновляем изображение профиля
                saveUserInfo(user.copy(photoUrl = imageUrl), false)
            }catch (e: Exception) {
                viewModelScope.launch { _updateInfo.emit(Resource.Error(e.message.toString())) }
            }
        }
    }

    // логическое значение означает нужно ли нам обновить изображение профиля или сохранить старое
    private fun saveUserInfo(user: User, shouldRetrievedImage: Boolean) {
        firestore.runTransaction {  transaction ->
            val documentRef = firestore.collection("user").document(auth.uid!!)

            // сохраняем старое изображение
            if(shouldRetrievedImage) {
                val currentUser = transaction.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(photoUrl = currentUser?.photoUrl ?: "")
                transaction.set(documentRef, newUser)
            } else {
                transaction.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch { _updateInfo.emit(Resource.Success(user)) }
        }.addOnFailureListener {
            viewModelScope.launch { _updateInfo.emit(Resource.Error(it.message.toString())) }
        }
    }
}