package su.afk.commercemvvm.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import su.afk.commercemvvm.data.models.User
import su.afk.commercemvvm.util.Resource
import javax.inject.Inject

@HiltViewModel
class ProfileVieModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _user = MutableStateFlow<Resource<User>>(Resource.Start())
    val user = _user.asStateFlow()


    init {
        getUser()
    }

    fun getUser() {
        viewModelScope.launch { _user.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!)
            .addSnapshotListener { value, error ->
                if(error != null){
                    viewModelScope.launch { _user.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                } else {
                    val user = value?.toObject(User::class.java)
                    user?.let {
                        viewModelScope.launch { _user.emit(Resource.Success(user)) }
                    }
                }
            }
    }

    fun logout() {
        auth.signOut()
    }


}