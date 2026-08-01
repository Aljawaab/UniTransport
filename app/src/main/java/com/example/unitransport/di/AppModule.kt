package com.example.unitransport.di

import com.example.unitransport.data.repository.AuthRepository
import com.example.unitransport.data.repository.BookingRepository
import com.example.unitransport.data.repository.LocationRepository
import com.example.unitransport.data.repository.VehicleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.unitransport.data.repository.UserRepository
import com.example.unitransport.data.repository.IssueReportRepository
import com.example.unitransport.data.repository.RatingRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository = AuthRepository()

    @Provides
    @Singleton
    fun provideBookingRepository(
        authRepository: AuthRepository
    ): BookingRepository = BookingRepository(authRepository)

    @Provides
    @Singleton
    fun provideLocationRepository(): LocationRepository =
        LocationRepository()

    @Provides
    @Singleton
    fun provideVehicleRepository(): VehicleRepository =
        VehicleRepository()

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository = UserRepository()

    @Provides
    @Singleton
    fun provideIssueReportRepository(): IssueReportRepository = IssueReportRepository()

    @Provides
    @Singleton
    fun provideRatingRepository(): RatingRepository = RatingRepository()
}