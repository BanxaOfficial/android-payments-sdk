package com.banxa.nativepaymentssdk.di

class AppContainer {

    // Network
    //private val apiService: SDKAuthAPI = NetworkModule.provideApi()

    // Repository
    /*private val userRepository: SDKAuthRepository =
        SDKAuthRepositoryImpl(apiService)*/

    // UseCase
    //val getUserUseCase = SDKAuthUseCase(userRepository)

    //private val api = RetrofitClient.api

    //private val coinRepository: CoinRepository = CoinRepositoryImpl(api)
/*
    val getTopCoinsUseCase = GetTopCoinsUseCase(coinRepository)

    fun provideCoinViewModel(owner: ViewModelStoreOwner): CoinViewModel {
        return ViewModelProvider(
            owner,
            CoinViewModelFactory(getTopCoinsUseCase)
        )[CoinViewModel::class.java]
    }

    fun provideAuthViewModel(owner: ViewModelStoreOwner): AuthViewModel {
        return ViewModelProvider(
            owner,
            SDKViewModelFactory(getUserUseCase)
        )[AuthViewModel::class.java]
    }
*/
}