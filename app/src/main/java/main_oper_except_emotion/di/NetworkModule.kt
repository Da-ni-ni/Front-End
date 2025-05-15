package com.example.appdanini.di

import com.example.appdanini.data.model.remote.network.EmotionApi
import com.example.appdanini.util.AuthInterceptor
import com.example.appdanini.util.TokenAuthenticator
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.network.AuthApi
import main_oper_except_emotion.network.ClosenessApi
import main_oper_except_emotion.network.DiaryApi
import main_oper_except_emotion.network.QuestionApi
import main_oper_except_emotion.repository.EmotionRepository
import main_oper_except_emotion.repsoitory.AuthRepository
import main_oper_except_emotion.repsoitory.ClosenessRepository
import main_oper_except_emotion.repsoitory.DiaryRepository
import main_oper_except_emotion.repsoitory.QuestionRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://58e82444-ec96-478b-83c9-58ba3bff9111.mock.pstmn.io/"

    @Provides
    @Singleton
    fun provideLogginInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES) // ✅ 핵심 설정
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson)) // ✅ 커스텀 gson 적용
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideClosenessApi(retrofit: Retrofit): ClosenessApi {
        return retrofit.create(ClosenessApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDiaryApi(retrofit: Retrofit): DiaryApi {
        return retrofit.create(DiaryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDiaryRepository(diaryApi: DiaryApi): DiaryRepository {
        return DiaryRepository(diaryApi)
    }

    @Provides
    @Singleton
    fun provideEmotionApi(retrofit: Retrofit): EmotionApi {
        return retrofit.create(EmotionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestionApi(retrofit: Retrofit): QuestionApi =
        retrofit.create(QuestionApi::class.java)

    @Provides
    @Singleton
    fun provideQuestionRepository(api: QuestionApi): QuestionRepository =
        QuestionRepository(api)

    @Provides
    @Singleton
    fun provideEmotionRepository(api: EmotionApi): EmotionRepository {
        return EmotionRepository(api)
    }

    @Provides
    @Singleton
    fun provideClosenessRepository(api: ClosenessApi): ClosenessRepository {
        return ClosenessRepository(api)
    }

}



