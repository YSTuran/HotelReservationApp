package yusufs.turan.hotelreservationapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import yusufs.turan.hotelreservationapp.data.repository.AuthRepositoryImpl
import yusufs.turan.hotelreservationapp.data.repository.HotelRepositoryImpl
import yusufs.turan.hotelreservationapp.domain.repository.AuthRepository
import yusufs.turan.hotelreservationapp.domain.repository.HotelRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindHotelRepository(
        hotelRepositoryImpl: HotelRepositoryImpl
    ): HotelRepository
}