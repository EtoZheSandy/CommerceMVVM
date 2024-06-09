package su.afk.commercemvvm.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import su.afk.commercemvvm.firebase.FirebaseCommon
import su.afk.commercemvvm.util.Constanse.INTRODUCTION_SP
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
//@InstallIn(FragmentComponent::class)
//@InstallIn(ServiceComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFireStoreDataBase(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides //Singleton не нужен потому что она не будет использоваться во всем приложение
    fun provideIndroductionSP(application: Application) =
        application.getSharedPreferences(INTRODUCTION_SP, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseCommon(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): FirebaseCommon {
        return FirebaseCommon(firestore = firestore, auth = firebaseAuth)
    }


    @Provides
    @Singleton
    fun provideStorage() = FirebaseStorage.getInstance().reference // .reference
}