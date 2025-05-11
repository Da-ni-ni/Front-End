package com.example.appdanini.di

import com.example.appdanini.data.model.remote.network.EmotionApi
import com.example.appdanini.util.AuthInterceptor
import com.example.appdanini.util.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import main_oper_except_emotion.TokenManager
import main_oper_except_emotion.network.DiaryApi
import main_oper_except_emotion.network.QuestionApi
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

    private const val BASE_URL = "https://ce935491-363e-4a9e-9d36-d95c076c9aef.mock.pstmn.io/"

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
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideEmotionApi(retrofit: Retrofit): EmotionApi {
        return retrofit.create(EmotionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestionApi(retrofit: Retrofit): QuestionApi {
        return retrofit.create(QuestionApi::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(api: QuestionApi): QuestionRepository {
        return QuestionRepository(api)
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
}
